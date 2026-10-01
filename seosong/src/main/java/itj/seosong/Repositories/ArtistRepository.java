package itj.seosong.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itj.seosong.Entities.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
}