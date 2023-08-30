package dsg.mapvotebot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * Collection of properties which get set via application.properties files
 */
@Getter
@Setter
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application-prod.properties")
@ConfigurationProperties(prefix ="mapvotebot")
public class Configuration {

    /** Token to authenticate the discord bot. */
    private String botToken;

    /** Token to authenticate the battlemetrics api calls. */
    private String battlemetricsApiToken;

    /** Token to authenticate the battlemetrics api calls via rcon. */
    private String battlemetricsApiTokenRcon;
}
