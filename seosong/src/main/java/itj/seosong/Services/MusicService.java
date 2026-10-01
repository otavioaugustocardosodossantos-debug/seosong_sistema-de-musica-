package itj.seosong.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itj.seosong.Entities.Artist;
import itj.seosong.Entities.Playlist;
import itj.seosong.Entities.Song;
import itj.seosong.Entities.User;
import itj.seosong.Exceptions.BadRequestException;
import itj.seosong.Exceptions.NotFoundException;
import itj.seosong.Repositories.ArtistRepository;
import itj.seosong.Repositories.SongRepository;

@Service
public class MusicService {

    private final SongRepository musicRepository;
    private final ArtistRepository artistRepository;

    public MusicService(SongRepository musicRepository, ArtistRepository artistRepository) {
        this.musicRepository = musicRepository;
        this.artistRepository = artistRepository;
    }

    public List<Song> findAll() {
        return musicRepository.findAll();
    }

    public Song findById(Long id) {
        return musicRepository.findById(id).orElseThrow(() -> new NotFoundException("Música não encontrada"));
    }

    public Song save(Song music) {
        music.setId(null);
        music.setArtist(resolveArtist(music.getArtist()));
        return musicRepository.save(music);
    }

    @Transactional
    public void deleteById(Long id) {
        Song song = findById(id);

        // As tabelas playlist_song e user_favorites apontam para a música;
        // sem remover esses vínculos antes, o banco recusa o DELETE.
        for (Playlist playlist : new ArrayList<>(song.getPlaylists())) {
            playlist.getSongs().removeIf(s -> s.getId().equals(id));
        }
        for (User user : new ArrayList<>(song.getFavoritedBy())) {
            user.getFavoriteSongs().removeIf(s -> s.getId().equals(id));
        }

        musicRepository.delete(song);
    }

    public Optional<Song> update(Long id, Song data) {
        return musicRepository.findById(id).map(existing -> {
            existing.setName(data.getName());
            existing.setAlbum(data.getAlbum());
            existing.setYear(data.getYear());
            existing.setDuration(data.getDuration());
            existing.setGenre(data.getGenre());
            existing.setPhoto(data.getPhoto());
            existing.setArtist(resolveArtist(data.getArtist()));
            return musicRepository.save(existing);
        });
    }

    /** Troca o { "id": X } recebido no JSON pelo artista real do banco (ou erro 400 se não existir). */
    private Artist resolveArtist(Artist artist) {
        if (artist == null || artist.getId() == null) {
            return null;
        }
        return artistRepository.findById(artist.getId())
                .orElseThrow(() -> new BadRequestException("Artista #" + artist.getId() + " não existe."));
    }
}
