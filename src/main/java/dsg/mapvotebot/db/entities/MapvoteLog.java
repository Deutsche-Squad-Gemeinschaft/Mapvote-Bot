package dsg.mapvotebot.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Model of database table which contains a log of all mapvotes from the past.
 */
@Entity
@Getter
@Setter
public class MapvoteLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /** Timestamp when the mapvote was initiated. */
    private String mapvoteStart;

    /** Discord Username of admin which initiated the vote. If the vote was automatically initiated adminName will contain the kind of automatic mapvote*/
    private String adminName;

    /** Number of minutes the mapvote was running. */
    private int timer;

    /** Name of first votable layer. */
    private String layer1;

    /** Amount of votes the first votable layer got. */
    private String layer1Votes;

    /** Name of second votable layer. */
    private String layer2;

    /** Amount of votes the second votable layer got. */
    private String layer2Votes;

    /** Name of third votable layer. */
    private String layer3;

    /** Amount of votes the third votable layer got. */
    private String layer3Votes;
}
