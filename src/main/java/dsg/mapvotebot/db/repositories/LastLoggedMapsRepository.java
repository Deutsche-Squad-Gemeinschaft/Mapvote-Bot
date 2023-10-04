package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.LastLoggedMaps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastLoggedMapsRepository extends JpaRepository<LastLoggedMaps, Integer> {
}
