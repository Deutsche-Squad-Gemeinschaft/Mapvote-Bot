package dsg.mapvotebot;

import dsg.mapvotebot.discord.Bot;
import dsg.mapvotebot.service.BattlemetricsService;
import dsg.mapvotebot.service.ServerDataRequestScheduler;
import net.dv8tion.jda.api.JDA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Main class which initiates the software.
 */
@Configuration
@ConfigurationPropertiesScan
@ComponentScan
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class})
public class MapvoteBotApplication {

    /**
     * Initiates the software.
     * Sets the valid layers for verification and starts the scheduler for periodical snapshot server data requests.
     * Starts the discord bot.
     *
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(MapvoteBotApplication.class, args);

        BattlemetricsService battlemetricsService = applicationContext.getBean(BattlemetricsService.class);
        battlemetricsService.setValidLayers();

        ServerDataRequestScheduler serverDataRequestScheduler = applicationContext.getBean(ServerDataRequestScheduler.class);
        serverDataRequestScheduler.startScheduleTask();

        Bot bot = applicationContext.getBean(Bot.class);
        JDA jda = bot.startBot();

    }
}
