package dsg.mapvotebot.service;

import dsg.mapvotebot.db.entities.VotersLeaderboard;
import dsg.mapvotebot.db.repositories.VotersLeaderboardRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class VotersLeaderboardService {
    private final VotersLeaderboardRepository votersLeaderboardRepository;

    public void increaseVoteNumberForPlayer(String playerId, String playerName){
       VotersLeaderboard votersLeaderboard = votersLeaderboardRepository.findByPlayerId(playerId);
       if(votersLeaderboard == null){
           VotersLeaderboard newVoter = new VotersLeaderboard();
           newVoter.setPlayerId(playerId);
           newVoter.setPlayerName(playerName);
           newVoter.setNumberOfVotes(1);
           votersLeaderboardRepository.save(newVoter);
       }else {
           votersLeaderboard.setNumberOfVotes(votersLeaderboard.getNumberOfVotes() + 1);

           if (!votersLeaderboard.getPlayerName().equals(playerName)){
               votersLeaderboard.setPlayerName(playerName);
           }
           votersLeaderboardRepository.save(votersLeaderboard);
       }
    }
}
