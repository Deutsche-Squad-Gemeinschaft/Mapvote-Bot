package dsg.mapvotebot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application-prod.properties")
@ConfigurationProperties(prefix ="mapvotebot")
public class Configuration {

    private String botToken;
    private String battlemetricsApiToken;
    private String battlemetricsApiTokenRcon;
}
