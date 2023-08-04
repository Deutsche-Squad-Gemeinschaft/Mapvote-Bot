package dsg.mapvotebot;

import dsg.mapvotebot.discord.Bot;
import dsg.mapvotebot.service.BattlemetricsService;
import net.dv8tion.jda.api.JDA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConfigurationPropertiesScan
@ComponentScan
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class})
public class MapvoteBotApplication {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(MapvoteBotApplication.class, args);


        BattlemetricsService battlemetricsService = applicationContext.getBean(BattlemetricsService.class);
        battlemetricsService.setValidLayers();

        Bot bot = applicationContext.getBean(Bot.class);
        JDA jda = bot.startBot();

    }
}
