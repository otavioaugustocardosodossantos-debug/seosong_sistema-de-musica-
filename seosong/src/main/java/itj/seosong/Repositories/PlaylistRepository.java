package itj.seosong.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itj.seosong.Entities.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}