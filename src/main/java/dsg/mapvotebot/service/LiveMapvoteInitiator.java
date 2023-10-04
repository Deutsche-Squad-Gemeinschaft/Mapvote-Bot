package dsg.mapvotebot.service;

import dsg.mapvotebot.api.model.ServerInfo;
import dsg.mapvotebot.db.entities.GlobalLayerRanking;
import dsg.mapvotebot.db.entities.LastLoggedGamemodes;
import dsg.mapvotebot.db.entities.LastLoggedMatch;
import dsg.mapvotebot.db.repositories.LastLoggedGamemodesRepository;
import dsg.mapvotebot.db.repositories.LastLoggedMatchRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class LiveMapvoteInitiator {

    private final LastLoggedGamemodesRepository lastLoggedGamemodesRepository;
    private final MapvoteConditionsAuditor mapvoteConditionsAuditor;
    private final GlobalLayerRankingService globalLayerRankingService;
    private final BattlemetricsService battlemetricsService;
    private final LastLoggedMatchRepository lastLoggedMatchRepository;

    public void checkForPlayTime(ServerInfo serverInfo){
        LastLoggedGamemodes lastLoggedGamemodes = lastLoggedGamemodesRepository.findById(1).get();
        LastLoggedMatch lastLoggedMatch = lastLoggedMatchRepository.findById(1).get();

        if(serverInfo.getPlayTime() >= 900 && !lastLoggedMatch.isMapvoteInitiated() && (lastLoggedGamemodes.getLastGamemode().equals("Seed") || lastLoggedGamemodes.getLastGamemode().equals("Skirmish")) && (!lastLoggedGamemodes.getCurrentGamemode().equals("Seed") && !lastLoggedGamemodes.getCurrentGamemode().equals("Skirmish"))){
            lastLoggedMatch.setMapvoteInitiated(true);
            lastLoggedMatchRepository.save(lastLoggedMatch);
            List<GlobalLayerRanking> selectedLayerList = globalLayerRankingService.selectLiveMaps(mapvoteConditionsAuditor.isGoodConditions());
            battlemetricsService.startScheduledMapvoteBroadcasts(battlemetricsService.createMapvoteBroadcastModel(5, selectedLayerList.get(0).getLayer(), selectedLayerList.get(1).getLayer(), selectedLayerList.get(2).getLayer(), "Automatic Mapvote"), false);

        } else if (serverInfo.getPlayTime() >= 1500 && !lastLoggedMatch.isMapvoteInitiated() && (!lastLoggedGamemodes.getCurrentGamemode().equals("Seed") && !lastLoggedGamemodes.getCurrentGamemode().equals("Skirmish"))) {
            lastLoggedMatch.setMapvoteInitiated(true);
            lastLoggedMatchRepository.save(lastLoggedMatch);
            List<GlobalLayerRanking> selectedLayerList = globalLayerRankingService.selectLiveMaps(mapvoteConditionsAuditor.isGoodConditions());
            battlemetricsService.startScheduledMapvoteBroadcasts(battlemetricsService.createMapvoteBroadcastModel(5, selectedLayerList.get(0).getLayer(), selectedLayerList.get(1).getLayer(), selectedLayerList.get(2).getLayer(), "Automatic Mapvote"), false);
        }
    }
}
