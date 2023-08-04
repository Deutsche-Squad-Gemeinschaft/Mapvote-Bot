package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.MapvoteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapvoteLogRepository extends JpaRepository<MapvoteLog, Integer> {
    MapvoteLog findByMapvoteStart(String mapvoteStart);

}
