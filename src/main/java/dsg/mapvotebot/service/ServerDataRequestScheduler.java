package dsg.mapvotebot.service;

import dsg.mapvotebot.api.model.ServerInfo;
import dsg.mapvotebot.config.Configuration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A scheduler which manages periodical api calls to battlemetrics.
 */
@Setter
@Getter
@Service
@RequiredArgsConstructor
public class ServerDataRequestScheduler {
    private final BattlemetricsService battlemetricsService;
    private final Configuration configuration;
    private final MapvoteConditionsAuditor mapvoteConditionsAuditor;
    private final LiveMapvoteInitiator liveMapvoteInitiator;
    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);

    /**
     * Starts the periodical api call to battlemetrics that requests server snapshot data every 10 seconds.
     */
    public void startScheduleTask() {

        final ScheduledFuture<?> taskHandle = scheduler.scheduleWithFixedDelay(
                () -> {
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        HttpHeaders headers = new HttpHeaders();
                        headers.set("Authorization", configuration.getBattlemetricsApiTokenRcon());
                        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                        String resourceURL = "https://api.battlemetrics.com/servers/3219649";
                        HttpEntity<String> entity = new HttpEntity<>(headers);
                        ResponseEntity<String> response = restTemplate.exchange(resourceURL, HttpMethod.GET, entity, String.class);

                        String data = response.getBody();

                        JSONObject obj = new JSONObject(data);

                        int players = obj.getJSONObject("data").getJSONObject("attributes").getInt("players");
                        String map = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getString("map");
                        int playTime = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getInt("squad_playTime");

                        ServerInfo serverInfo = new ServerInfo();

                        serverInfo.setPlayers(players);
                        serverInfo.setLayer(map);
                        serverInfo.setPlayTime(playTime);

                        mapvoteConditionsAuditor.checkForGoodConditions(serverInfo);
                        battlemetricsService.evaluateServerData(serverInfo);
                        mapvoteConditionsAuditor.checkForNewMatch(serverInfo);
                        liveMapvoteInitiator.checkForPlayTime(serverInfo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, 10, TimeUnit.SECONDS);
    }
}
