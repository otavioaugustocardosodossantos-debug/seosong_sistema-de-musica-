package itj.seosong.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itj.seosong.Entities.Playlist;
import itj.seosong.Entities.Song;
import itj.seosong.Entities.User;
import itj.seosong.Exceptions.BadRequestException;
import itj.seosong.Exceptions.ConflictException;
import itj.seosong.Repositories.PlaylistRepository;
import itj.seosong.Repositories.SongRepository;
import itj.seosong.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, SongRepository songRepository,
                       PlaylistRepository playlistRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(User newUser) {
        newUser.setId(null);
        // Favoritos e playlists têm endpoints próprios; não aceitamos no cadastro.
        newUser.setFavoriteSongs(new ArrayList<>());
        newUser.setPlaylists(new ArrayList<>());

        if (isBlank(newUser.getEmail())) {
            throw new BadRequestException("O e-mail é obrigatório.");
        }
        if (isBlank(newUser.getPassword())) {
            throw new BadRequestException("A senha é obrigatória.");
        }
        if (repository.existsByEmailIgnoreCase(newUser.getEmail().trim())) {
            throw new ConflictException("Já existe um usuário com esse e-mail.");
        }

        newUser.setEmail(newUser.getEmail().trim());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return repository.save(newUser);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<User> update(Long id, User data) {
        return repository.findById(id).map(existing -> {
            if (!isBlank(data.getEmail())
                    && repository.existsByEmailIgnoreCaseAndIdNot(data.getEmail().trim(), id)) {
                throw new ConflictException("Já existe outro usuário com esse e-mail.");
            }

            existing.setName(data.getName());
            existing.setEmail(data.getEmail() == null ? null : data.getEmail().trim());
            existing.setBirthDate(data.getBirthDate());
            existing.setZipCode(data.getZipCode());
            existing.setPhoto(data.getPhoto());

            // A senha não volta no JSON, então um PUT sem senha significa "manter a atual".
            if (!isBlank(data.getPassword())) {
                existing.setPassword(passwordEncoder.encode(data.getPassword()));
            }
            return repository.save(existing);
        });
    }

    @Transactional
    public boolean deleteById(Long id) {
        Optional<User> userOpt = repository.findById(id);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();

        // playlist.user_id aponta para o usuário: as playlists dele são excluídas junto.
        List<Playlist> playlists = new ArrayList<>(user.getPlaylists());
        user.getPlaylists().clear();
        playlistRepository.deleteAll(playlists);

        // user_favorites é limpo automaticamente (o User é o dono da relação).
        repository.delete(user);
        return true;
    }

    @Transactional
    public Optional<User> addFavorite(Long userId, Long songId) {
        Optional<User> userOpt = repository.findById(userId);
        Optional<Song> songOpt = songRepository.findById(songId);

        if (userOpt.isEmpty() || songOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        Song song = songOpt.get();

        boolean jaTem = user.getFavoriteSongs().stream()
                .anyMatch(s -> s.getId().equals(song.getId()));

        if (!jaTem) {
            user.getFavoriteSongs().add(song);
            user = repository.save(user);
        }

        return Optional.of(user);
    }

    @Transactional
    public Optional<User> removeFavorite(Long userId, Long songId) {
        return repository.findById(userId).map(user -> {
            user.getFavoriteSongs().removeIf(s -> s.getId().equals(songId));
            return repository.save(user);
        });
    }

    /** Senhas antigas foram salvas em texto puro. Converte para hash BCrypt (roda na inicialização). */
    @Transactional
    public int hashLegacyPasswords() {
        int count = 0;
        for (User user : repository.findAll()) {
            String pwd = user.getPassword();
            if (pwd != null && !pwd.isEmpty() && !looksLikeBcrypt(pwd)) {
                user.setPassword(passwordEncoder.encode(pwd));
                count++;
            }
        }
        return count;
    }

    /** Útil para um futuro login: compara a senha digitada com o hash salvo. */
    public boolean checkPassword(User user, String rawPassword) {
        return user.getPassword() != null && rawPassword != null
                && passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private static boolean looksLikeBcrypt(String s) {
        return s.length() == 60 && (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$"));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
