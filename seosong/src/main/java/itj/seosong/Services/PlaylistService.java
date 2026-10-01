package itj.seosong.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itj.seosong.Entities.Playlist;
import itj.seosong.Entities.Song;
import itj.seosong.Entities.User;
import itj.seosong.Exceptions.BadRequestException;
import itj.seosong.Exceptions.NotFoundException;
import itj.seosong.Repositories.PlaylistRepository;
import itj.seosong.Repositories.SongRepository;
import itj.seosong.Repositories.UserRepository;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public PlaylistService(PlaylistRepository playlistRepository, SongRepository songRepository,
                           UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    public List<Playlist> findAll() {
        return playlistRepository.findAll();
    }

    public Playlist findById(Long id) {
        return playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Playlist não encontrada"));
    }

    @Transactional
    public Playlist save(Playlist playlist) {
        playlist.setId(null);

        User owner = resolveUser(playlist.getUser());
        if (owner == null) {
            throw new BadRequestException("Informe o usuário dono da playlist ({ \"user\": { \"id\": 1 } }).");
        }
        playlist.setUser(owner);
        playlist.setSongs(resolveSongs(playlist.getSongs()));

        return playlistRepository.save(playlist);
    }

    public void deleteById(Long id) {
        if (!playlistRepository.existsById(id)) {
            throw new NotFoundException("Playlist não encontrada");
        }
        playlistRepository.deleteById(id);
    }

    @Transactional
    public Optional<Playlist> update(Long id, Playlist data) {
        return playlistRepository.findById(id).map(existing -> {
            existing.setName(data.getName());
            existing.setPhoto(data.getPhoto());
            existing.setDescription(data.getDescription());

            // Só troca o dono se o corpo trouxer um; antes o PUT apagava o dono da playlist.
            User newOwner = resolveUser(data.getUser());
            if (newOwner != null) {
                existing.setUser(newOwner);
            }
            return playlistRepository.save(existing);
        });
    }

    @Transactional
    public Optional<Playlist> addSong(Long playlistId, Long songId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        Optional<Song> songOpt = songRepository.findById(songId);

        if (playlistOpt.isEmpty() || songOpt.isEmpty()) {
            return Optional.empty();
        }

        Playlist playlist = playlistOpt.get();
        Song song = songOpt.get();

        boolean jaTem = playlist.getSongs().stream()
                .anyMatch(s -> s.getId().equals(song.getId()));

        if (!jaTem) {
            playlist.getSongs().add(song);
            playlist = playlistRepository.save(playlist);
        }

        return Optional.of(playlist);
    }

    @Transactional
    public Optional<Playlist> removeSong(Long playlistId, Long songId) {
        return playlistRepository.findById(playlistId).map(playlist -> {
            playlist.getSongs().removeIf(s -> s.getId().equals(songId));
            return playlistRepository.save(playlist);
        });
    }

    private User resolveUser(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new BadRequestException("Usuário #" + user.getId() + " não existe."));
    }

    private List<Song> resolveSongs(List<Song> songs) {
        List<Song> result = new ArrayList<>();
        if (songs == null) {
            return result;
        }
        for (Song s : songs) {
            if (s == null || s.getId() == null) continue;
            Song real = songRepository.findById(s.getId())
                    .orElseThrow(() -> new BadRequestException("Música #" + s.getId() + " não existe."));
            if (result.stream().noneMatch(r -> r.getId().equals(real.getId()))) {
                result.add(real);
            }
        }
        return result;
    }
}
