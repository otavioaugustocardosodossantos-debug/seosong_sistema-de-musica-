package itj.seosong.services;

import itj.seosong.entities.Song;
import itj.seosong.repositories.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicService {

    private final SongRepository musicRepository;

    public MusicService(SongRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Song> findAll() {
        return musicRepository.findAll();
    }

    public Song findById(Long id) {
        return musicRepository.findById(id).orElseThrow(() -> new RuntimeException("Música não encontrada"));
    }

    public Song save(Song music) {
        return musicRepository.save(music);
    }

    public void deleteById(Long id) {
        musicRepository.deleteById(id);
    }
}