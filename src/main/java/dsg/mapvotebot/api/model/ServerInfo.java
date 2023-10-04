package dsg.mapvotebot.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Collection of data which contains a snapshot of server data.
 */
@Getter
@Setter
@EqualsAndHashCode
@Component
public class ServerInfo {

    /** Amount of players currently in the server. */
    private int players;

    /** Name of Layer currently played. */
    private String layer;

    /** For how long the current layer is played in seconds.  */
    private int playTime;
}
