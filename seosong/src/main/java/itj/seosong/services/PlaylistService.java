package itj.seosong.services;

import itj.seosong.entities.Playlist;
import itj.seosong.repositories.PlaylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public List<Playlist> findAll() {
        return playlistRepository.findAll();
    }

    public Playlist findById(Long id) {
        return playlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Playlist não encontrada"));
    }

    public Playlist save(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public void deleteById(Long id) {
        playlistRepository.deleteById(id);
    }
}