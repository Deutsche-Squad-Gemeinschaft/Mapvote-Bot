package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.ValidLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository of database table which contains a collection of individual database queries.
 */
@Repository
public interface ValidLayerRepository extends JpaRepository<ValidLayer, Integer> {
    ValidLayer findByLayer(String layer);
}
