package dsg.mapvotebot.service;

import dsg.mapvotebot.db.entities.LastLoggedWipe;
import dsg.mapvotebot.db.repositories.LastLoggedWipeRepository;
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
    private final LastLoggedWipeRepository lastLoggedWipeRepository;

    public String getCurrentDayOfYear(){
        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTime = new DateTime(date, timeZone);
        return String.valueOf(dateTime.getDayOfYear());
    }

    public void updateCurrentDayOfYear(){
        LastLoggedWipe lastLoggedWipe = lastLoggedWipeRepository.findById(1).get();

        Date date = new Date();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        DateTime dateTime = new DateTime(date, timeZone);
        lastLoggedWipe.setDayOfYear(String.valueOf(dateTime.getDayOfYear()));

        lastLoggedWipeRepository.save(lastLoggedWipe);
    }
}
