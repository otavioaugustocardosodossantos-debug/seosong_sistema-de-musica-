package itj.seosong.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import itj.seosong.Entities.Playlist;
import itj.seosong.Entities.Song;
import itj.seosong.Entities.User;
import itj.seosong.Exceptions.BadRequestException;
import itj.seosong.Exceptions.LlmException;
import itj.seosong.Exceptions.NotFoundException;
import itj.seosong.Repositories.PlaylistRepository;
import itj.seosong.Repositories.SongRepository;
import itj.seosong.Repositories.UserRepository;

@Service
public class LlmService {

    private static final int PLAYLIST_DESCRIPTION_MAX = 500; // tamanho da coluna playlist.description
    private static final int GENRE_MAX = 60;
    private static final int VIBE_MAX_SONGS = 8;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final ObjectMapper objectMapper;

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.api.key:}")
    private String apiKey;

    @Value("${llm.api.model}")
    private String model;

    public LlmService(RestTemplate restTemplate, UserRepository userRepository,
                      PlaylistRepository playlistRepository, SongRepository songRepository,
                      ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.objectMapper = objectMapper;
    }

    // ===== 1. Recomendação de músicas/artistas com base no gosto do usuário =====

    @Transactional(readOnly = true)
    public String recommendForUser(Long userId) {
        User user = findUser(userId);
        String gosto = userTaste(user);

        if (gosto.isBlank()) {
            return "Esse usuário ainda não tem favoritos nem playlists com músicas — adicione algumas primeiro para receber recomendações.";
        }

        String prompt = "Com base nestas músicas de um usuário: " + gosto +
                ". Sugira 5 artistas ou músicas parecidas que ele provavelmente vai gostar. " +
                "Responda em português, em uma lista simples e curta, sem introdução.";

        return askLlm(prompt);
    }

    // ===== 2. Geração automática de descrição de playlist =====

    @Transactional
    public Playlist generatePlaylistDescription(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist não encontrada"));

        if (playlist.getSongs().isEmpty()) {
            playlist.setDescription("Essa playlist ainda não tem músicas.");
            return playlistRepository.save(playlist);
        }

        playlist.setDescription(buildDescription(playlist.getName(), playlist.getSongs()));
        return playlistRepository.save(playlist);
    }

    // ===== 3. Gerar playlist a partir de um humor/vibe =====

    @Transactional
    public Playlist generateVibePlaylist(Long userId, String vibe, String nomeDesejado) {
        if (vibe == null || vibe.isBlank()) {
            throw new BadRequestException("Descreva a vibe da playlist.");
        }
        vibe = vibe.trim();

        User user = findUser(userId);

        List<Song> catalogo = songRepository.findAll();

        if (catalogo.isEmpty()) {
            throw new BadRequestException("Não há músicas cadastradas no catálogo ainda.");
        }

        String listaCatalogo = catalogo.stream()
                .map(s -> s.getId() + " - " + describe(s) + (isBlank(s.getGenre()) ? "" : " [" + s.getGenre() + "]"))
                .collect(Collectors.joining(" | "));

        String prompt = "Catálogo de músicas disponíveis (formato id - nome (artista) [gênero]): " + listaCatalogo +
                ". Com base nesta vibe/humor: \"" + vibe + "\", escolha até " + VIBE_MAX_SONGS +
                " músicas desse catálogo que combinem bem. " +
                "Responda APENAS com os IDs separados por vírgula, sem nenhum texto extra, por exemplo: 3,7,12";

        String resposta = askLlm(prompt);

        // Pega qualquer número da resposta (a IA às vezes escreve "IDs: 3, 7").
        Set<Long> idsEscolhidos = new LinkedHashSet<>();
        Matcher m = Pattern.compile("\\d+").matcher(resposta);
        while (m.find() && idsEscolhidos.size() < VIBE_MAX_SONGS) {
            try {
                idsEscolhidos.add(Long.parseLong(m.group()));
            } catch (NumberFormatException ignored) {
                // número grande demais: ignora
            }
        }

        List<Song> musicasEscolhidas = new ArrayList<>();
        for (Long id : idsEscolhidos) {
            catalogo.stream().filter(s -> s.getId().equals(id)).findFirst().ifPresent(musicasEscolhidas::add);
        }

        if (musicasEscolhidas.isEmpty()) {
            throw new LlmException("A IA não conseguiu escolher músicas do catálogo para essa vibe. Tente descrever de outro jeito.");
        }

        Playlist playlist = new Playlist();
        playlist.setUser(user);
        playlist.setName(!isBlank(nomeDesejado) ? nomeDesejado.trim() : "Playlist: " + vibe);
        playlist.setSongs(musicasEscolhidas);
        playlist.setDescription(buildDescription(playlist.getName(), musicasEscolhidas));

        return playlistRepository.save(playlist);
    }

    // ===== 4. Resumo do perfil musical do usuário =====

    @Transactional(readOnly = true)
    public String summarizeUserProfile(Long userId) {
        User user = findUser(userId);
        String contexto = userTaste(user);

        if (contexto.isBlank()) {
            return "Esse usuário ainda não tem músicas favoritas nem playlists — não dá pra traçar um perfil musical ainda.";
        }

        String prompt = "Com base nestas músicas favoritas e de playlists de um usuário: " + contexto +
                ". Escreva um resumo curto (2 a 3 frases), descontraído e em português, sobre o perfil musical dele.";

        return askLlm(prompt);
    }

    // ===== 5. Detectar gênero de uma música quando não preenchido =====

    @Transactional
    public Song detectGenre(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new NotFoundException("Música não encontrada"));

        if (!isBlank(song.getGenre())) {
            return song;
        }

        String artista = song.getArtist() != null && !isBlank(song.getArtist().getName())
                ? song.getArtist().getName() : "desconhecido";

        String prompt = "Qual é o gênero musical predominante da música '" + song.getName() +
                "' do artista '" + artista + "'? Responda ESTRITAMENTE com o nome do gênero, " +
                "uma palavra ou expressão curta (ex: Rock, Britpop, MPB, Funk). " +
                "Não adicione nenhum comentário, explicação, correção ou observação — " +
                "mesmo que o artista informado pareça incorreto, responda apenas o gênero mais provável para essa música.";

        song.setGenre(cleanGenre(askLlm(prompt)));
        return songRepository.save(song);
    }

    // ===== auxiliares =====

    private User findUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Informe o usuário.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    /** Músicas favoritas + músicas das playlists do usuário, sem repetir. */
    private String userTaste(User user) {
        Set<String> musicas = new LinkedHashSet<>();
        user.getFavoriteSongs().forEach(s -> musicas.add(describe(s)));
        user.getPlaylists().stream()
                .flatMap(p -> p.getSongs().stream())
                .forEach(s -> musicas.add(describe(s)));
        return String.join(", ", musicas);
    }

    private String describe(Song s) {
        String nome = isBlank(s.getName()) ? "Sem nome" : s.getName();
        return nome + (s.getArtist() != null && !isBlank(s.getArtist().getName())
                ? " (" + s.getArtist().getName() + ")" : "");
    }

    private String buildDescription(String playlistName, List<Song> songs) {
        String musicas = songs.stream()
                .map(this::describe)
                .collect(Collectors.joining(", "));

        String prompt = "Você é um curador de música. Gere uma descrição curta (1 a 2 frases, no máximo 300 caracteres), " +
                "criativa e envolvente, para uma playlist chamada '" + playlistName +
                "' que contém estas músicas: " + musicas +
                ". Responda em português, só com a descrição, sem aspas e sem introdução.";

        return truncate(stripQuotes(askLlm(prompt)), PLAYLIST_DESCRIPTION_MAX);
    }

    private String cleanGenre(String raw) {
        String g = stripQuotes(raw.split("\\R")[0]).replaceAll("[.!]+$", "").trim();
        if (g.isEmpty()) {
            throw new LlmException("A IA não retornou um gênero válido.");
        }
        return truncate(g, GENRE_MAX);
    }

    /**
     * Chama a API da Anthropic. Em caso de falha lança LlmException,
     * que o GlobalExceptionHandler transforma em HTTP 502 com { "error": "..." }.
     */
    private String askLlm(String prompt) {

        if (isBlank(apiKey)) {
            throw new LlmException("Chave da IA não configurada. Defina ANTHROPIC_API_KEY (variável de ambiente ou secrets.properties).");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("max_tokens", 300);
        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String response;
        try {
            response = restTemplate.postForObject(apiUrl, request, String.class);
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            String motivo = switch (status) {
                case 401 -> "chave da API inválida ou revogada";
                case 404 -> "modelo \"" + model + "\" não encontrado";
                case 429 -> "limite de uso atingido, tente de novo em instantes";
                case 529 -> "serviço da IA sobrecarregado, tente de novo em instantes";
                default -> "erro HTTP " + status;
            };
            throw new LlmException("A IA não respondeu: " + motivo + ".", e);
        } catch (ResourceAccessException e) {
            throw new LlmException("Não foi possível conectar à IA (sem internet ou tempo esgotado).", e);
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            for (JsonNode block : root.path("content")) {
                if ("text".equals(block.path("type").asText())) {
                    String text = block.path("text").asText().trim();
                    if (!text.isEmpty()) {
                        return text;
                    }
                }
            }
        } catch (Exception e) {
            throw new LlmException("Resposta da IA em formato inesperado.", e);
        }
        throw new LlmException("A IA retornou uma resposta vazia.");
    }

    private static String stripQuotes(String s) {
        return s.trim().replaceAll("^[\"'“”«»]+|[\"'“”«»]+$", "").trim();
    }

    private static String truncate(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, max - 1).trim() + "…";
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
