package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.LastLoggedMatch;
import dsg.mapvotebot.db.entities.LastLoggedWipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastLoggedWipeRepository extends JpaRepository<LastLoggedWipe, Integer> {
}
