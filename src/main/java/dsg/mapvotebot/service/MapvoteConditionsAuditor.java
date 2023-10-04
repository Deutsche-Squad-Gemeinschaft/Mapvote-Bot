package dsg.mapvotebot.service;

import dsg.mapvotebot.api.model.ServerInfo;
import dsg.mapvotebot.db.entities.GlobalLayerRanking;
import dsg.mapvotebot.db.entities.LastLoggedGamemodes;
import dsg.mapvotebot.db.entities.LastLoggedMaps;
import dsg.mapvotebot.db.entities.LastLoggedMatch;
import dsg.mapvotebot.db.repositories.GlobalLayerRankingRepository;
import dsg.mapvotebot.db.repositories.LastLoggedGamemodesRepository;
import dsg.mapvotebot.db.repositories.LastLoggedMapsRepository;
import dsg.mapvotebot.db.repositories.LastLoggedMatchRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.util.Date;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class MapvoteConditionsAuditor {

    private boolean goodConditions;
    private final LastLoggedMatchRepository lastLoggedMatchRepository;
    private final LastLoggedMapsRepository lastLoggedMapsRepository;
    private final LastLoggedGamemodesRepository lastLoggedGamemodesRepository;
    private final GlobalLayerRankingRepository globalLayerRankingRepository;
    private final GlobalLayerRankingService globalLayerRankingService;

    public void checkForGoodConditions(ServerInfo serverInfo){
        setGoodConditions(checkForDateTimeConditions() && checkForPlayerAmountConditions(serverInfo));
    }

    private boolean checkForDateTimeConditions(){
        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTime = new DateTime(date, timeZone);
        String dayOfWeek = dateTime.dayOfWeek().getAsString();
        int minuteOfDay = dateTime.getMinuteOfDay();

        return switch (dayOfWeek) {
            case "1", "2", "3", "4", "7" -> minuteOfDay < 1200;
            case "5", "6" -> minuteOfDay < 1320;
            default -> true;
        };
    }

    private boolean checkForPlayerAmountConditions(ServerInfo serverInfo){
        return serverInfo.getPlayers() >= 80;
    }

    public void checkForNewMatch(ServerInfo serverInfo){
        LastLoggedMatch lastLoggedMatch = lastLoggedMatchRepository.findById(1).get();
        String lastLoggedMatchLayerName = lastLoggedMatch.getLayerName();
        LastLoggedMaps lastLoggedMaps = lastLoggedMapsRepository.findById(1).get();
        LastLoggedGamemodes lastLoggedGamemodes = lastLoggedGamemodesRepository.findById(1).get();

        if (!serverInfo.getLayer().equals(lastLoggedMatchLayerName)){
            lastLoggedMatch.setLayerName(serverInfo.getLayer());
            lastLoggedMatch.setMapvoteInitiated(false);
            lastLoggedMatchRepository.save(lastLoggedMatch);

            lastLoggedMaps.setFourthLastMap(lastLoggedMaps.getThirdLastMap());
            lastLoggedMaps.setThirdLastMap(lastLoggedMaps.getSecondLastMap());
            lastLoggedMaps.setSecondLastMap(lastLoggedMaps.getLastMap());
            lastLoggedMaps.setLastMap(lastLoggedMaps.getCurrentMap());
            lastLoggedMaps.setCurrentMap(globalLayerRankingRepository.findByLayer(serverInfo.getLayer()).getMap());
            lastLoggedMapsRepository.save(lastLoggedMaps);

            lastLoggedGamemodes.setSecondLastGamemode(lastLoggedGamemodes.getLastGamemode());
            lastLoggedGamemodes.setLastGamemode(lastLoggedGamemodes.getCurrentGamemode());

            lastLoggedGamemodes.setCurrentGamemode(getGamemode(serverInfo));

            lastLoggedGamemodesRepository.save(lastLoggedGamemodes);
        }
    }

    public String getGamemode(ServerInfo serverInfo){
        if(serverInfo.getLayer().contains("RAAS")){
            return "RAAS";
        } else if (serverInfo.getLayer().contains("AAS")) {
            return "AAS";
        } else if (serverInfo.getLayer().contains("Insurgency")) {
            return "Insurgency";
        } else if (serverInfo.getLayer().contains("Invasion")) {
            return "Invasion";
        } else if (serverInfo.getLayer().contains("Seed")) {
            return "Seed";
        } else if (serverInfo.getLayer().contains("Skirmish")) {
            return "Skirmish";
        } else if (serverInfo.getLayer().contains("TA")) {
            return "TA";
        } else if (serverInfo.getLayer().contains("TC")) {
            return "TC";
        } else if (serverInfo.getLayer().contains("Destruction")) {
            return "Destruction";
        } else if (serverInfo.getLayer().contains("Tanks")) {
            return "Tanks";
        }
        return null;
    }
}
