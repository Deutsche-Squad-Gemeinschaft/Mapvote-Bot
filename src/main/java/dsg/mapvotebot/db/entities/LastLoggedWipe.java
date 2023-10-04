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
public class LastLoggedWipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String dayOfYear;
}
