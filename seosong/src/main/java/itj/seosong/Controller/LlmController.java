package itj.seosong.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import itj.seosong.Entities.Playlist;
import itj.seosong.Entities.Song;
import itj.seosong.Exceptions.BadRequestException;
import itj.seosong.Services.LlmService;

@CrossOrigin
@RestController
@RequestMapping("/llm")
public class LlmController {

    private final LlmService service;

    public LlmController(LlmService service) {
        this.service = service;
    }

    // 1. Recomendação de músicas/artistas
    @PostMapping("/users/{userId}/recommendations")
    public ResponseEntity<Map<String, String>> recommend(@PathVariable Long userId) {
        String texto = service.recommendForUser(userId);
        return ResponseEntity.ok(Map.of("recommendations", texto));
    }

    // 2. Descrição automática de playlist
    @PostMapping("/playlists/{playlistId}/description")
    public ResponseEntity<Playlist> generateDescription(@PathVariable Long playlistId) {
        Playlist playlist = service.generatePlaylistDescription(playlistId);
        return ResponseEntity.ok(playlist);
    }

    // 3. Playlist a partir de um humor/vibe
    // corpo esperado: { "userId": 1, "vibe": "estudar à noite", "name": "opcional" }
    @PostMapping("/vibe-playlist")
    public ResponseEntity<Playlist> vibePlaylist(@RequestBody Map<String, Object> body) {
        Long userId;
        try {
            userId = Long.valueOf(String.valueOf(body.get("userId")));
        } catch (NumberFormatException e) {
            throw new BadRequestException("Informe um userId válido.");
        }
        String vibe = body.get("vibe") == null ? null : body.get("vibe").toString();
        String nome = body.get("name") == null ? null : body.get("name").toString();
        Playlist playlist = service.generateVibePlaylist(userId, vibe, nome);
        return ResponseEntity.status(201).body(playlist);
    }

    // 4. Resumo do perfil musical do usuário
    @PostMapping("/users/{userId}/profile-summary")
    public ResponseEntity<Map<String, String>> profileSummary(@PathVariable Long userId) {
        String texto = service.summarizeUserProfile(userId);
        return ResponseEntity.ok(Map.of("summary", texto));
    }

    // 5. Detectar gênero de uma música
    // "/songs/..." é a rota usada pelo ia.html (igual ao resto da API); "/musics/..." fica como alias antigo.
    @PostMapping({ "/songs/{songId}/detect-genre", "/musics/{songId}/detect-genre" })
    public ResponseEntity<Song> detectGenre(@PathVariable Long songId) {
        Song song = service.detectGenre(songId);
        return ResponseEntity.ok(song);
    }
}