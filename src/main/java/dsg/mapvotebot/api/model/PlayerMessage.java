package dsg.mapvotebot.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@EqualsAndHashCode
@Component
public class PlayerMessage {
    private String timestamp;
    private String playerName;
    private String message;
}
