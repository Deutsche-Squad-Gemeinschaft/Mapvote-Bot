package dsg.mapvotebot.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MapvoteLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String mapvoteStart;
    private String adminName;
    private int timer;
    private String layer1;
    private String layer1Votes;
    private String layer2;
    private String layer2Votes;
    private String layer3;
    private String layer3Votes;
}
