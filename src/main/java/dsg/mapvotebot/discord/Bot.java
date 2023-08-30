package dsg.mapvotebot.discord;

import dsg.mapvotebot.config.Configuration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.stereotype.Service;

/**
 * Represents the discord bot.
 */
@RequiredArgsConstructor
@Service
@Getter
@Setter
public class Bot {

    private final Configuration configuration;
    private final EventHandler eventHandler;

    /** Instance of concrete bot. */
    private JDA jda;

    /**
     * Creates an instance of the discord bot and starts the bot.
     *
     * @return Instance of bot.
     * @throws InterruptedException
     */
    public JDA startBot() throws InterruptedException {

        JDA jda = JDABuilder.createDefault(configuration.getBotToken())
                .addEventListeners(eventHandler)
                .setActivity(Activity.playing(" mit euren Votes"))
                .build()
                .awaitReady();

        setJda(jda);
        System.out.println("Im online.");

        return jda;
    }
}
