package dsg.mapvotebot.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Model of a mapvote which contains details of the specific mapvote
 */
@Getter
@Setter
@EqualsAndHashCode
public class MapvoteModel {

    /** Specifies when the first mapvote broadcast will be sent. */
    private int timeLeftAtFirstBroadcast;

    /** Specifies when the second mapvote broadcast will be sent. */
    private int timeLeftAtSecondBroadcast;

    /** Specifies when the third mapvote broadcast will be sent. */
    private int timeLeftAtThirdBroadcast;

    /** Specifies when the fourth mapvote broadcast will be sent. */
    private int timeLeftAtFourthBroadcast;

    /** Specifies when the fifth mapvote broadcast will be sent. */
    private int timeLeftAtFifthBroadcast;

    /** Name of first votable layer. */
    private String layer1;

    /** Name of second votable layer. */
    private String layer2;

    /** Name of third votable layer. */
    private String layer3;

    /** Timestamp when the mapvote was initiated. */
    private DateTime voteStart;

    /** Number of minutes the mapvote will run. */
    private int timer;

    /** Discord username of admin which initiated the vote. If the vote was automatically initiated adminName will contain the kind of automatic mapvote. */
    private String admin;

    /** Amount of votes the first votable layer got. */
    private String layer1Votes;

    /** Amount of votes the second votable layer got. */
    private String layer2Votes;

    /** Amount of votes the third votable layer got. */
    private String layer3Votes;

    public MapvoteModel(int timeLeftAtFirstBroadcast, int timeLeftAtSecondBroadcast, int timeLeftAtThirdBroadcast, int timeLeftAtFourthBroadcast, int timeLeftAtFifthBroadcast, String layer1, String layer2, String layer3, DateTime voteStart, int timer, String admin) {
        this.timeLeftAtFirstBroadcast = timeLeftAtFirstBroadcast;
        this.timeLeftAtSecondBroadcast = timeLeftAtSecondBroadcast;
        this.timeLeftAtThirdBroadcast = timeLeftAtThirdBroadcast;
        this.timeLeftAtFourthBroadcast = timeLeftAtFourthBroadcast;
        this.timeLeftAtFifthBroadcast = timeLeftAtFifthBroadcast;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.voteStart = voteStart;
        this.timer = timer;
        this.admin = admin;
    }
}
