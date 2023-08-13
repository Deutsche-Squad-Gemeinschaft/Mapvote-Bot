package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.GlobalLayerRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalLayerRankingRepository extends JpaRepository<GlobalLayerRanking, Integer> {
    GlobalLayerRanking findByLayer(String layer);
}
