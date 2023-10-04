package dsg.mapvotebot.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Model of a players message.
 */
@Getter
@Setter
@EqualsAndHashCode
@Component
public class PlayerMessage {

    /** The timestamp of the time the message was sent. */
    private String timestamp;

    /** The name of the player who sent the message. */
    private String playerName;

    private String playerId;

    /** The message itself. */
    private String message;
}
