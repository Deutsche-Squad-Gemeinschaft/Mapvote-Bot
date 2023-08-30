package dsg.mapvotebot.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Model of database table which contains a collection of all layers in existence. This data is used to verify the spelling of layers.
 */
@Entity
@Getter
@Setter
public class ValidLayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /** Name of layer. */
    private String layer;

    /** Short version of first faction. */
    private String teamOne;

    /** Short version of second faction. */
    private String teamTwo;
}
