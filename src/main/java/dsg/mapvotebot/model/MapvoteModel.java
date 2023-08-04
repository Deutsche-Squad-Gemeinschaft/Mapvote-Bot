package dsg.mapvotebot.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@EqualsAndHashCode
public class MapvoteModel {
    private int timeLeftAtFirstBroadcast;
    private int timeLeftAtSecondBroadcast;
    private int timeLeftAtThirdBroadcast;
    private int timeLeftAtFourthBroadcast;
    private int timeLeftAtFifthBroadcast;
    private String layer1;
    private String layer2;
    private String layer3;
    private DateTime voteStart;
    private int timer;
    private String admin;
    private String layer1Votes;
    private String layer2Votes;
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
