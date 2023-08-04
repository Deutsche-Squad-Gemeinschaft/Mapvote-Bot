package dsg.mapvotebot.service;

import dsg.mapvotebot.api.controller.BattlemetricsController;
import dsg.mapvotebot.api.model.PlayerMessage;
import dsg.mapvotebot.db.entities.MapvoteLog;
import dsg.mapvotebot.db.entities.ValidLayer;
import dsg.mapvotebot.db.repositories.MapvoteLogRepository;
import dsg.mapvotebot.db.repositories.ValidLayerRepository;
import dsg.mapvotebot.model.MapvoteModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class BattlemetricsService {

    private final BattlemetricsController battlemetricsController;
    private final MapvoteLogRepository mapvoteLogRepository;
    private final ValidLayerRepository validLayerRepository;
    private List<ValidLayer> validLayers;
    private boolean mapvoteRunning;

    public boolean verifyLayer(String layer) {
        for (ValidLayer validLayer : validLayers) {
            if (validLayer.getLayer().equals(layer)) {
                return true;
            }
        }
        return false;
    }

    public void setValidLayers() {
        validLayers = validLayerRepository.findAll();
    }

    public MapvoteModel createMapvoteBroadcastModel(int timer, String layer1, String layer2, String layer3, String admin) {
        MapvoteModel mapvoteModel;

        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTime = new DateTime(date, timeZone);
        DateTime dateTimeUtc = dateTime.withZone(DateTimeZone.UTC);

        switch (timer) {
            case 3 -> {
                mapvoteModel = new MapvoteModel(180, 144, 108, 72,36, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 4 -> {
                mapvoteModel = new MapvoteModel(240, 192, 144,96,48, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 5 -> {
                mapvoteModel = new MapvoteModel(300, 240, 180,120,60, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 6 -> {
                mapvoteModel = new MapvoteModel(360, 288, 216,144,72, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 7 -> {
                mapvoteModel = new MapvoteModel(420, 336, 252,168,84, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 8 -> {
                mapvoteModel = new MapvoteModel(480, 384, 288,192,96, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 9 -> {
                mapvoteModel = new MapvoteModel(540, 432, 324,216,108, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 10 -> {
                mapvoteModel = new MapvoteModel(600, 480, 360,240,120, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
        }
        return null;
    }

    public MapvoteModel evaluateVotes(MapvoteModel mapvoteModel) throws IOException {
        List<PlayerMessage> playerMessages = battlemetricsController.getChatData();

        HashMap<String, Integer> mapvotes = new HashMap<>();
        mapvotes.put("1", 0);
        mapvotes.put("2", 0);
        mapvotes.put("3", 0);

        HashMap<String, String> voters = new HashMap<>();

        for (PlayerMessage playerMessage : playerMessages) {
            String dateTime = playerMessage.getTimestamp();

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .withLocale(Locale.ROOT)
                    .withChronology(ISOChronology.getInstanceUTC());

            DateTime dt = formatter.parseDateTime(dateTime);

            if (dt.isAfter(mapvoteModel.getVoteStart())){
                if(playerMessage.getMessage().equals("1") || playerMessage.getMessage().equals("2") || playerMessage.getMessage().equals("3")){
                    if(!voters.containsKey(playerMessage.getPlayerName())){
                       voters.put(playerMessage.getPlayerName(), playerMessage.getMessage());
                       mapvotes.put(playerMessage.getMessage(), mapvotes.get(playerMessage.getMessage()) +1);
                    }else{
                        String voteBefore = voters.get(playerMessage.getPlayerName());
                        mapvotes.put(voteBefore, mapvotes.get(voteBefore) -1);
                        voters.put(playerMessage.getPlayerName(), playerMessage.getMessage());
                        mapvotes.put(playerMessage.getMessage(), mapvotes.get(playerMessage.getMessage()) +1);
                    }
                }
            }
        }
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : mapvotes.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                maxEntry = entry;
            }
        }
        if(maxEntry == null){
            String layerWinner = mapvoteModel.getLayer1();
            battlemetricsController.sendMapvoteEndBroadcast(layerWinner, "[Ausgewählt, da keine Mapvote-Teilnehmer vorhanden]");
        }else{
            String layerWinner;
            switch (maxEntry.getKey()){
                case "1":
                    layerWinner = mapvoteModel.getLayer1();
                    battlemetricsController.sendMapvoteEndBroadcast(layerWinner, String.valueOf(maxEntry.getValue()));
                    battlemetricsController.setNextMap(layerWinner);
                    break;
                case "2":
                    layerWinner = mapvoteModel.getLayer2();
                    battlemetricsController.sendMapvoteEndBroadcast(layerWinner, String.valueOf(maxEntry.getValue()));
                    battlemetricsController.setNextMap(layerWinner);
                    break;
                case "3":
                    layerWinner = mapvoteModel.getLayer3();
                    battlemetricsController.sendMapvoteEndBroadcast(layerWinner, String.valueOf(maxEntry.getValue()));
                    battlemetricsController.setNextMap(layerWinner);
                    break;
            }
            mapvoteModel.setLayer1Votes(String.valueOf(mapvotes.get("1")));
            mapvoteModel.setLayer2Votes(String.valueOf(mapvotes.get("2")));
            mapvoteModel.setLayer3Votes(String.valueOf(mapvotes.get("3")));

            MapvoteLog mapvoteLog = new MapvoteLog();
            mapvoteLog.setAdminName(mapvoteModel.getAdmin());
            mapvoteLog.setLayer1(mapvoteModel.getLayer1());
            mapvoteLog.setLayer2(mapvoteModel.getLayer2());
            mapvoteLog.setLayer3(mapvoteModel.getLayer3());
            mapvoteLog.setMapvoteStart(String.valueOf(mapvoteModel.getVoteStart()));
            mapvoteLog.setTimer(mapvoteModel.getTimer());
            mapvoteLog.setLayer1Votes(mapvoteModel.getLayer1Votes());
            mapvoteLog.setLayer2Votes(mapvoteModel.getLayer2Votes());
            mapvoteLog.setLayer3Votes(mapvoteModel.getLayer3Votes());
            mapvoteLogRepository.save(mapvoteLog);
        }

        return mapvoteModel;
    }

    public void startScheduledMapvoteBroadcasts(MapvoteModel mapvoteModel) {
        final ScheduledExecutorService scheduler = Executors
                .newScheduledThreadPool(1);

        mapvoteRunning = true;

        AtomicInteger secondsLeft = new AtomicInteger(mapvoteModel.getTimeLeftAtFirstBroadcast());

        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                () -> {

                    if (secondsLeft.get() == 0) {
                        scheduler.shutdown();
                        try {
                            evaluateVotes(mapvoteModel);
                            mapvoteRunning = false;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtFirstBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(mapvoteModel.getLayer1(), mapvoteModel.getLayer2(), mapvoteModel.getLayer3(), String.valueOf(mapvoteModel.getTimeLeftAtFirstBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtSecondBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(mapvoteModel.getLayer1(), mapvoteModel.getLayer2(), mapvoteModel.getLayer3(), String.valueOf(mapvoteModel.getTimeLeftAtSecondBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtThirdBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(mapvoteModel.getLayer1(), mapvoteModel.getLayer2(), mapvoteModel.getLayer3(), String.valueOf(mapvoteModel.getTimeLeftAtThirdBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtFourthBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(mapvoteModel.getLayer1(), mapvoteModel.getLayer2(), mapvoteModel.getLayer3(), String.valueOf(mapvoteModel.getTimeLeftAtFourthBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtFifthBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(mapvoteModel.getLayer1(), mapvoteModel.getLayer2(), mapvoteModel.getLayer3(), String.valueOf(mapvoteModel.getTimeLeftAtFifthBroadcast()));
                    }

                    secondsLeft.getAndDecrement();
                    System.out.println(secondsLeft);

                }, 0, 1, TimeUnit.SECONDS);
    }
}
