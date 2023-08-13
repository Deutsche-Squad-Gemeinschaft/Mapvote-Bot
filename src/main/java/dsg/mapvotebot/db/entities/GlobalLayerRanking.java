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
public class GlobalLayerRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String layer;
    private Double elo;
    private boolean playable;
    private boolean seeding;
    private boolean firstLiveMap;
    private Double reliability;
    private int appearance;
    private int levelOfDevelopment;
    private int numberOfCorrectPredictions;
    private int numberOfFalsePredictions;
}
