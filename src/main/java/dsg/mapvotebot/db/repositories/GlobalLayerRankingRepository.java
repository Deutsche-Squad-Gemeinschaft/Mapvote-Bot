package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.GlobalLayerRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository of database table which contains a collection of individual database queries.
 */
@Repository
public interface GlobalLayerRankingRepository extends JpaRepository<GlobalLayerRanking, Integer> {
    GlobalLayerRanking findByLayer(String layerName);

    /** Used to get the pool of layers suitable to be played as the first live map. */
    List<GlobalLayerRanking> findAllByFirstLiveMapIsTrueAndPlayableIsTrue();

    List<GlobalLayerRanking> findAllByFirstLiveMapIsFalseAndPlayableIsTrueAndSeedingIsFalse();
}
