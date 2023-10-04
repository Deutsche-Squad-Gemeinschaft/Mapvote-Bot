package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.LastLoggedGamemodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastLoggedGamemodesRepository extends JpaRepository<LastLoggedGamemodes, Integer> {
}
