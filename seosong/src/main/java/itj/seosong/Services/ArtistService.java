package itj.seosong.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itj.seosong.Entities.Artist;
import itj.seosong.Exceptions.ConflictException;
import itj.seosong.Exceptions.NotFoundException;
import itj.seosong.Repositories.ArtistRepository;
import itj.seosong.Repositories.SongRepository;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    public ArtistService(ArtistRepository artistRepository, SongRepository songRepository) {
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
    }

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id).orElseThrow(() -> new NotFoundException("Artista não encontrado"));
    }

    public Artist save(Artist artist) {
        // Garante que o POST sempre cria (um "id" no corpo sobrescreveria um artista existente).
        artist.setId(null);
        return artistRepository.save(artist);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new NotFoundException("Artista não encontrado");
        }
        if (songRepository.existsByArtist_Id(id)) {
            throw new ConflictException("Esse artista tem músicas cadastradas. Exclua ou altere as músicas antes.");
        }
        artistRepository.deleteById(id);
    }

    public Optional<Artist> update(Long id, Artist data) {
        return artistRepository.findById(id).map(existing -> {
            existing.setName(data.getName());
            existing.setFormationDate(data.getFormationDate());
            existing.setDescription(data.getDescription());
            existing.setPhoto(data.getPhoto());
            return artistRepository.save(existing);
        });
    }
}
