package dsg.mapvotebot.service;

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
public class LastLoggedWipeService {

    public String getCurrentDayOfYear(){
        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTime = new DateTime(date, timeZone);
        return String.valueOf(dateTime.getDayOfYear());
    }
}
