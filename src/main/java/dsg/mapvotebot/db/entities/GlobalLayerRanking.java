package dsg.mapvotebot.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Model of database table which contains the ranking of layers based on the elo ranking system.
 */
@Entity
@Getter
@Setter
public class GlobalLayerRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /** Name of layer. */
    private String layer;

    /** Name of map. */
    private String map;

    /** Amount of elo the layer has. */
    private Double elo;

    /** Adjustment option if layer is playable. Not playable layers will not appear in automatic mapvotes.  */
    private boolean playable;

    /** Adjustment option if layer is a seed or skirmish layer. Layers marked with seeding will not appear in automatic mapvotes for live-matches. */
    private boolean seeding;

    /** Adjustment option if layer is suitable to be played as the first live-map.
      * Layers marked with firstLiveMap form a pool from which the automatic first live map mapvote picks three suitable layers. */
    private boolean firstLiveMap;

    /** Ratio of numberOfCorrectPredictions to numberOfFalsePredictions in percent forms the reliability of a layer being in the correct ranking spot. */
    private Double reliability;

    /** Times of which a layer appeared in mapvotes in the past. */
    private int appearance;

    private boolean layerWithoutAppearancePriority;

    /** "K-Factor" which regulates the impact on how much elo a layer gaines or loses after winning or failing a mapvote. */
    private int levelOfDevelopment;

    /** Amount of correct predictions. This is an outcome of the process of the elo system. */
    private int numberOfCorrectPredictions;

    /** Amount of false predictions. */
    private int numberOfFalsePredictions;
}
