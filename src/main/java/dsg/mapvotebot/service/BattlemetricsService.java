package dsg.mapvotebot.service;

import dsg.mapvotebot.api.controller.BattlemetricsController;
import dsg.mapvotebot.api.model.PlayerMessage;
import dsg.mapvotebot.api.model.ServerInfo;
import dsg.mapvotebot.db.entities.GlobalLayerRanking;
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


/**
 * Contains all methods which interfere with battlemetrics.
 */
@Setter
@Getter
@Service
@RequiredArgsConstructor
public class BattlemetricsService {

    private final BattlemetricsController battlemetricsController;
    private final MapvoteLogRepository mapvoteLogRepository;
    private final ValidLayerRepository validLayerRepository;
    private final GlobalLayerRankingService globalLayerRankingService;

    /** List of all valid layers to verify the spelling. */
    private List<ValidLayer> validLayers;

    /** Indicator if a mapvote is already running. If so, no other mapvote can be started. */
    private boolean mapvoteRunning;

    /** Indicator if server is live. Server is live at 51 players and resets if server has 0 players. */
    private boolean isServerLive;

    /** Indicator if the first live map mapvote was started. It starts if the player amount is greater or equal 45 and if firstLiveMapMapvote is false.
      * It resets if server has 0 players. */
    private boolean firstLiveMapMapvote;

    /** Indicator if first map was technically set to prevent seeding match cancellation before the first live map was set.
      * True if mapvote from first live map mapvote was evaluated (and the setMap command was sent). Resets after the seeding match was cancelled.  */
    private boolean firstMapIsSet;

    /**
     * Verifies the existence and correct spelling of layers provided by a user for the manual mapvote
     *
     * @param layer name of layer which should get verified
     * @return true if layer was verified
     */
    public boolean verifyLayer(String layer) {
        for (ValidLayer validLayer : validLayers) {
            if (validLayer.getLayer().equals(layer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Synchronizes the list of valid layers with the database
     */
    public void setValidLayers() {
        validLayers = validLayerRepository.findAll();
    }

    /**
     * Creates a mapvote model and adds the broadcast times based on the timer used.
     *
     * @param timer Number of minutes the mapvote should run. Cant be lower than 3 and higher than 10.
     * @param layer1 Name of first layer that should be votable.
     * @param layer2 Name of second layer that should be votable.
     * @param layer3 Name of third layer that should be votable.
     * @param admin Discord username of admin which initiated the vote. If the vote was automatically initiated adminName will contain the kind of automatic mapvote.
     * @return model containing all mapvote details
     */
    public MapvoteModel createMapvoteBroadcastModel(int timer, String layer1, String layer2, String layer3, String admin) {
        MapvoteModel mapvoteModel;

        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTime = new DateTime(date, timeZone);
        DateTime dateTimeUtc = dateTime.withZone(DateTimeZone.UTC);

        switch (timer) {
            case 3 -> {
                mapvoteModel = new MapvoteModel(180, 144, 108, 72, 36, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 4 -> {
                mapvoteModel = new MapvoteModel(240, 192, 144, 96, 48, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 5 -> {
                mapvoteModel = new MapvoteModel(300, 240, 180, 120, 60, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 6 -> {
                mapvoteModel = new MapvoteModel(360, 288, 216, 144, 72, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 7 -> {
                mapvoteModel = new MapvoteModel(420, 336, 252, 168, 84, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 8 -> {
                mapvoteModel = new MapvoteModel(480, 384, 288, 192, 96, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 9 -> {
                mapvoteModel = new MapvoteModel(540, 432, 324, 216, 108, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
            case 10 -> {
                mapvoteModel = new MapvoteModel(600, 480, 360, 240, 120, layer1, layer2, layer3, dateTimeUtc, timer, admin);
                return mapvoteModel;
            }
        }
        return null;
    }


    /**
     * Evaluates the results of the mapvote and makes sure, that only the latest vote from a player counts. In case the player voted more than once, the new vote overwrites the old vote.
     * Also, the voter has to provide just a number between 1 and 3 with no other text so the vote is valid.
     * The Mapvote Result gets logged in database, a broadcast gets send with the winning layer and the layer gets set as the next map.
     *
     *
     * @param mapvoteModel Mapvote model that includes details like the time when mapvote started to specify the time range the chat messages have to be searched through for.
     * @return Mapvote model which contains the mapvote details and results.
     * @throws IOException
     */
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

            if (dt.isAfter(mapvoteModel.getVoteStart())) {
                if (playerMessage.getMessage().equals("1") || playerMessage.getMessage().equals("2") || playerMessage.getMessage().equals("3")) {
                    if (!voters.containsKey(playerMessage.getPlayerName())) {
                        voters.put(playerMessage.getPlayerName(), playerMessage.getMessage());
                        mapvotes.put(playerMessage.getMessage(), mapvotes.get(playerMessage.getMessage()) + 1);
                    } else {
                        String voteBefore = voters.get(playerMessage.getPlayerName());
                        mapvotes.put(voteBefore, mapvotes.get(voteBefore) - 1);
                        voters.put(playerMessage.getPlayerName(), playerMessage.getMessage());
                        mapvotes.put(playerMessage.getMessage(), mapvotes.get(playerMessage.getMessage()) + 1);
                    }
                }
            }
        }
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : mapvotes.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        if (maxEntry == null) {
            String layerWinner = mapvoteModel.getLayer1();
            battlemetricsController.sendMapvoteEndBroadcast(layerWinner, "[Ausgewählt, da keine Mapvote-Teilnehmer vorhanden]");
        } else {
            String layerWinner;
            switch (maxEntry.getKey()) {
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

            globalLayerRankingService.evaluateMapvoteRanking(mapvoteModel.getLayer1(), mapvotes.get("1"), mapvoteModel.getLayer2(), mapvotes.get("2"), mapvoteModel.getLayer3(), mapvotes.get("3"));
        }

        return mapvoteModel;
    }

    /**
     * Starts a countdown which sends broadcasts periodically in the server when the specified amount of seconds in the mapvote model for broadcasts is left.
     * If the countdown reaches 0, the evaluation process gets triggered and the countdown shuts down.
     *
     * @param mapvoteModel Mapvote model that contains all necessary mapvote details.
     * @param firstLiveMapMapvote Option if the mapvote is a first live map mapvote.
     */
    public void startScheduledMapvoteBroadcasts(MapvoteModel mapvoteModel, boolean firstLiveMapMapvote) {
        final ScheduledExecutorService scheduler = Executors
                .newScheduledThreadPool(1);

        mapvoteRunning = true;

        ValidLayer validLayer1 = validLayerRepository.findByLayer(mapvoteModel.getLayer1());
        ValidLayer validLayer2 = validLayerRepository.findByLayer(mapvoteModel.getLayer2());
        ValidLayer validLayer3 = validLayerRepository.findByLayer(mapvoteModel.getLayer3());

        AtomicInteger secondsLeft = new AtomicInteger(mapvoteModel.getTimeLeftAtFirstBroadcast());

        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                () -> {

                    if (secondsLeft.get() == 0) {
                        scheduler.shutdown();
                        try {
                            evaluateVotes(mapvoteModel);
                            mapvoteRunning = false;
                            if(firstLiveMapMapvote){
                                firstMapIsSet = true;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtFirstBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(validLayer1, validLayer2, validLayer3, String.valueOf(mapvoteModel.getTimeLeftAtFirstBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtSecondBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(validLayer1, validLayer2, validLayer3, String.valueOf(mapvoteModel.getTimeLeftAtSecondBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtThirdBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(validLayer1, validLayer2, validLayer3, String.valueOf(mapvoteModel.getTimeLeftAtThirdBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtFourthBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(validLayer1, validLayer2, validLayer3, String.valueOf(mapvoteModel.getTimeLeftAtFourthBroadcast()));
                    }

                    if (secondsLeft.get() == mapvoteModel.getTimeLeftAtFifthBroadcast()) {
                        battlemetricsController.sendMapvoteBroadcast(validLayer1, validLayer2, validLayer3, String.valueOf(mapvoteModel.getTimeLeftAtFifthBroadcast()));
                    }

                    secondsLeft.getAndDecrement();
                    System.out.println(secondsLeft);

                }, 0, 1, TimeUnit.SECONDS);
    }


    /**
     * Processes the server data and triggers events based on player amounts.
     * Contains logic which is responsible for sending the automated first live map mapvote.
     *
     * @param serverInfo Collection of snapshot server data which gets requested periodically.
     * @throws InterruptedException
     */
    public void evaluateServerData(ServerInfo serverInfo) throws InterruptedException {
        String playtime = parsePlayTime(serverInfo.getPlayTime());
        int player = serverInfo.getPlayers();
        String layer = serverInfo.getLayer();

        if (player > 50) {
            isServerLive = true;
            if(firstMapIsSet){
                battlemetricsController.sendMatchEndsGameLiveBroadcast();
                Thread.sleep(7000);
                battlemetricsController.endMatch();
                firstMapIsSet = false;
            }
        }

        if (player >= 45 && !isServerLive && !firstLiveMapMapvote) {
            List<GlobalLayerRanking> selectedLayerList = globalLayerRankingService.selectFirstLiveMaps();
            startScheduledMapvoteBroadcasts(createMapvoteBroadcastModel(3, selectedLayerList.get(0).getLayer(), selectedLayerList.get(1).getLayer(), selectedLayerList.get(2).getLayer(), "Automatic FirstLiveMap Mapvote"), true);
            firstLiveMapMapvote = true;
        }

        if (player == 0) {
            isServerLive = false;
            firstLiveMapMapvote = false;
        }
    }

    /**
     * Parses play time from milliseconds to a hh:mm:ss format
     *
     * @param playTime Time played which should be parsed.
     * @return Parsed play time.
     */
    private String parsePlayTime(int playTime) {

        return String.format("%02d:%02d:%02d", playTime / 3600, (playTime % 3600) / 60, (playTime % 60));
    }
}
