package dsg.mapvotebot.db.repositories;

import dsg.mapvotebot.db.entities.MapvoteLog;
import dsg.mapvotebot.db.entities.VotersLeaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotersLeaderboardRepository extends JpaRepository<VotersLeaderboard, Integer> {
    VotersLeaderboard findByPlayerId(String playerId);
}
