package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.LastLoggedMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastLoggedMatchRepository extends JpaRepository<LastLoggedMatch, Integer> {
}
