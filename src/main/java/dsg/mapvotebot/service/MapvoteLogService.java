package dsg.mapvotebot.service;

import dsg.mapvotebot.db.entities.MapvoteLog;
import dsg.mapvotebot.db.repositories.MapvoteLogRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class MapvoteLogService {
    private final MapvoteLogRepository mapvoteLogRepository;

    public EmbedBuilder createEmbedMapvoteLog(){

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Mapvote Historie "+ fmt.print(getDateTimeNow().minusDays(7).plusHours(2)) +" - "+ fmt.print(getDateTimeNow().plusHours(2)), null);
        eb.setColor(new Color(255, 196, 12));
        eb.setDescription(buildLogString());
        return eb;
    }

    private String buildLogString(){
        List<MapvoteLog> mapvoteLogs = mapvoteLogRepository.findAll();

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
        }
        return logEntrys;
    }

    private DateTime getDateTimeNow(){
        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTimeNow = new DateTime(date, timeZone);
        return dateTimeNow.withZone(DateTimeZone.UTC);
    }
}
