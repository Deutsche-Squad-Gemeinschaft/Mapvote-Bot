package dsg.mapvotebot.api.controller;

import dsg.mapvotebot.api.model.PlayerMessage;
import dsg.mapvotebot.config.Configuration;
import dsg.mapvotebot.db.entities.ValidLayer;
import dsg.mapvotebot.db.repositories.ValidLayerRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BattlemetricsController {

    private final Configuration configuration;
    private final ValidLayerRepository validLayerRepository;

    public List<PlayerMessage> getChatData() throws IOException {

        String rconUrl = "https://api.battlemetrics.com/activity?version=%5E0.1.0&tagTypeMode=and&filter%5Btypes%5D%5Bblacklist%5D=event%3Aquery&filter%5Bservers%5D=3219649&include=organization%2Cuser&page%5Bsize%5D=100&access_token="+configuration.getBattlemetricsApiToken()+"&audit_log=t%3DRCON%20Server%3Bp%3D%2Frcon%2Fservers%2F3219649%3Bh%3D225c05a3-65fb-4b28-9ee9-0a416352bdd9%3Bid%3Df663c89b-ba29-499f-8f38-93eb2230b749";

        // Set up the HTTP connection
        URL url = new URL(rconUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        // Read the response from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String responseNextLayer = in.readLine();

        in.close();
        JSONObject jsonResponse = new JSONObject(responseNextLayer);

        List<PlayerMessage> playerMessages = new ArrayList<>();

        int i = jsonResponse.getJSONArray("data").length()-1;
        for (i = i; i != 0; i--){
           JSONObject jsonObject = (JSONObject) jsonResponse.getJSONArray("data").get(i);
           if(jsonObject.getJSONObject("attributes").getString("messageType").equals("playerMessage")) {
               //System.out.println(jsonObject.getJSONObject("attributes").getString("timestamp")+ " ("+jsonObject.getJSONObject("attributes").getJSONObject("data").getString("channel")+") "+jsonObject.getJSONObject("attributes").getJSONObject("data").getString("playerName") + ": "+ jsonObject.getJSONObject("attributes").getJSONObject("data").getString("message"));
               PlayerMessage playerMessage = new PlayerMessage();
               playerMessage.setPlayerName(jsonObject.getJSONObject("attributes").getJSONObject("data").getString("playerName"));
               playerMessage.setTimestamp(jsonObject.getJSONObject("attributes").getString("timestamp"));
               playerMessage.setMessage(jsonObject.getJSONObject("attributes").getJSONObject("data").getString("message"));
               playerMessages.add(playerMessage);
           }
        }
        return playerMessages;
    }

    public void sendMapvoteBroadcast(ValidLayer layer1, ValidLayer layer2, ValidLayer layer3, String timer){

        try {
            // Construct the URL to send the RCON command
            String rconUrl = "https://api.battlemetrics.com/servers/3219649/command";

            // Set up the HTTP connection
            URL url = new URL(rconUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", configuration.getBattlemetricsApiTokenRcon());
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Construct the JSON payload for the RCON command
            String jsonPayload = "{\n" +
                    "  \"data\": {\n" +
                    "    \"type\": \"rconCommand\",\n" +
                    "    \"attributes\": {\n" +
                    "        \"command\": \"squad:broadcast\",\n" +
                    "        \"options\": {\n" +
                    "            \"message\": \"Mapvote! (1) "+layer1.getLayer()+" "+layer1.getTeamOne()+"-"+layer1.getTeamTwo()+" (2) "+layer2.getLayer()+" "+layer2.getTeamOne()+"-"+layer2.getTeamTwo()+" (3) "+layer3.getLayer()+" "+layer3.getTeamOne()+"-"+layer3.getTeamTwo()+" | noch "+timer+"sec!\"\n" +
                    "            }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            // Send the JSON payload as the HTTP request body
            con.getOutputStream().write(jsonPayload.getBytes(StandardCharsets.UTF_8));

            // Read the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String responseNextLayer = in.readLine();

            in.close();

            // Convert the response to a JSON object
            JSONObject jsonResponse = new JSONObject(responseNextLayer);

            String result = jsonResponse.getJSONObject("data").getJSONObject("attributes").getString("result");
            //System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMapvoteEndBroadcast(String layerWon, String votes){
        try {
            // Construct the URL to send the RCON command
            String rconUrl = "https://api.battlemetrics.com/servers/3219649/command";

            // Set up the HTTP connection
            URL url = new URL(rconUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", configuration.getBattlemetricsApiTokenRcon());
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Construct the JSON payload for the RCON command
            String jsonPayload = "{\n" +
                    "  \"data\": {\n" +
                    "    \"type\": \"rconCommand\",\n" +
                    "    \"attributes\": {\n" +
                    "        \"command\": \"squad:broadcast\",\n" +
                    "        \"options\": {\n" +
                    "            \"message\": \""+layerWon+" hat den Mapvote mit "+votes+" Stimmen gewonnen!\"\n" +
                    "            }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            // Send the JSON payload as the HTTP request body
            con.getOutputStream().write(jsonPayload.getBytes(StandardCharsets.UTF_8));

            // Read the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String responseNextLayer = in.readLine();

            in.close();

            // Convert the response to a JSON object
            JSONObject jsonResponse = new JSONObject(responseNextLayer);

            String result = jsonResponse.getJSONObject("data").getJSONObject("attributes").getString("result");
            //System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNextMap(String map){
        try {
            // Construct the URL to send the RCON command
            String rconUrl = "https://api.battlemetrics.com/servers/3219649/command";

            // Set up the HTTP connection
            URL url = new URL(rconUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", configuration.getBattlemetricsApiTokenRcon());
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Construct the JSON payload for the RCON command
            String jsonPayload = "{\n" +
                    "  \"data\": {\n" +
                    "    \"type\": \"rconCommand\",\n" +
                    "    \"attributes\": {\n" +
                    "        \"command\": \"squad:setNextMap\",\n" +
                    "        \"options\": {\n" +
                    "            \"map\": \""+map+"\"\n" +
                    "            }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            // Send the JSON payload as the HTTP request body
            con.getOutputStream().write(jsonPayload.getBytes(StandardCharsets.UTF_8));

            // Read the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String responseNextLayer = in.readLine();

            in.close();

            // Convert the response to a JSON object
            JSONObject jsonResponse = new JSONObject(responseNextLayer);

            String result = jsonResponse.getJSONObject("data").getJSONObject("attributes").getString("result");
            //System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
