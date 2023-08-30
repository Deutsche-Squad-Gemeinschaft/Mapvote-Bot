package dsg.mapvotebot.service;

import dsg.mapvotebot.db.entities.MapvoteLog;
import dsg.mapvotebot.db.repositories.MapvoteLogRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Contains all methods which interfere with the mapvote log.
 */
@Setter
@Getter
@Service
@RequiredArgsConstructor
public class MapvoteLogService {
    private final MapvoteLogRepository mapvoteLogRepository;

    /**
     * Creates embeds that display the mapvote log from the past 7 days.
     *
     * @return List with discord embeds
     */
    public List<MessageEmbed> createEmbedMapvoteLog(){

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

        ArrayList<String> mapvoteLogsList = buildLogString();
        List<MessageEmbed> embeds = new ArrayList<>();

        for (String s : mapvoteLogsList) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Mapvote Historie " + fmt.print(getDateTimeNow().minusDays(7).plusHours(2)) + " - " + fmt.print(getDateTimeNow().plusHours(2)), null);
            eb.setColor(new Color(255, 196, 12));
            eb.setDescription(s);
            embeds.add(eb.build());
        }

        return embeds;
    }

    /**
     * Builds strings for every log entry and concats them until a character limit is reached.
     * If limit is reached a new string gets created and added to list.
     *
     * @return List of log entries.
     */
    private ArrayList<String> buildLogString(){

        List<MapvoteLog> mapvoteLogs = mapvoteLogRepository.findAll();

        ArrayList<String> mapvoteLogsList = new ArrayList<>();
        int i = 0;

        DateTime dateTimeNowUTC = getDateTimeNow();

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .withLocale(Locale.ROOT)
                .withChronology(ISOChronology.getInstanceUTC());

        String logEntrys = "";

        for (MapvoteLog mapvoteLog : mapvoteLogs){
            String dateTime = mapvoteLog.getMapvoteStart();
            DateTime dt = formatter.parseDateTime(dateTime);

            if (dt.plusDays(7).isAfter(dateTimeNowUTC)){
                logEntrys = logEntrys.concat(
                                        "**Admin:** " + mapvoteLog.getAdminName() + "\n" +
                                            "**Start:** " + fmt.print(dt.plusHours(2)) + "\n" +
                                            "**Timer:** " + mapvoteLog.getTimer() + "min \n" +
                                            "**Layer1:** " + mapvoteLog.getLayer1() + " **Votes:** " + mapvoteLog.getLayer1Votes()+ "\n" +
                                            "**Layer2:** " + mapvoteLog.getLayer2() + " **Votes:** " + mapvoteLog.getLayer2Votes()+ "\n" +
                                            "**Layer3:** " + mapvoteLog.getLayer3() + " **Votes:** " + mapvoteLog.getLayer3Votes()+ "\n\n"
                );
            }

            if (logEntrys.length() > 3500) {
                mapvoteLogsList.add(i, logEntrys);
                i = i + 1;
                logEntrys = "";
            }
        }

            if (mapvoteLogsList.size() == 0 || logEntrys.length() > 0) {
                mapvoteLogsList.add(logEntrys);
            }

        return mapvoteLogsList;
    }

    /**
     * Gets the current date time for timezone Europe/Berlin
     *
     * @return Current Date Time
     */
    private DateTime getDateTimeNow(){
        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTimeNow = new DateTime(date, timeZone);
        return dateTimeNow.withZone(DateTimeZone.UTC);
    }
}
