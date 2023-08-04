package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.ValidLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidLayerRepository extends JpaRepository<ValidLayer, Integer> {
}
