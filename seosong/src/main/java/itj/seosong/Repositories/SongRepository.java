package itj.seosong.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itj.seosong.Entities.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    boolean existsByArtist_Id(Long artistId);
}
