package dsg.mapvotebot.discord;


import dsg.mapvotebot.model.MapvoteModel;
import dsg.mapvotebot.service.BattlemetricsService;
import dsg.mapvotebot.service.MapvoteLogService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles all discord specific events that happen at runtime
 */
@Component
@RequiredArgsConstructor
public class EventHandler extends ListenerAdapter {

    private final BattlemetricsService battlemetricsService;
    private final MapvoteLogService mapvoteLogService;

    /**
     * Occurs when a slash command was executed. The event name has to be filtered to assign the specific slash command to the event.
     *
     * @param event automatic handed over event which contains event-details
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("starte-mapvote")) {

            if(battlemetricsService.isMapvoteRunning()){
                event.reply("Es läuft bereits ein Mapvote!").setEphemeral(true).queue();
                return;
            }

            event.reply("Du kannst derzeit keinen manuellen Mapvote durchführen, da dies automatisiert geschieht!").setEphemeral(true).queue();
            /*
            String layer1 = Objects.requireNonNull(event.getOption("layer1")).getAsString();
            String layer2 = Objects.requireNonNull(event.getOption("layer2")).getAsString();
            String layer3 = Objects.requireNonNull(event.getOption("layer3")).getAsString();
            int timer = Objects.requireNonNull(event.getOption("timer")).getAsInt();

            if (!battlemetricsService.verifyLayer(layer1)){
                event.reply("Das angegebene Layer " + layer1 + " konnte nicht verifiziert werden. Bitte achte auf die richtige Schreibweise und versuche es nochmal!").setEphemeral(true).queue();
                return;
            }

            if (!battlemetricsService.verifyLayer(layer2)){
                event.reply("Das angegebene Layer " + layer2 + " konnte nicht verifiziert werden. Bitte achte auf die richtige Schreibweise und versuche es nochmal!").setEphemeral(true).queue();
                return;
            }

            if (!battlemetricsService.verifyLayer(layer3)){
                event.reply("Das angegebene Layer " + layer3 + " konnte nicht verifiziert werden. Bitte achte auf die richtige Schreibweise und versuche es nochmal!").setEphemeral(true).queue();
                return;
            }

            MapvoteModel mapvoteModel = battlemetricsService.createMapvoteBroadcastModel(timer, layer1, layer2, layer3, event.getUser().getName());
            if(mapvoteModel == null){
                event.reply("Error 420: Timer could not be initiated. Please contact budmuecke!").setEphemeral(true).queue();
                return;
            }

            battlemetricsService.startScheduledMapvoteBroadcasts(mapvoteModel, false);
            event.reply("Mapvote initiiert. Lehn dich zurück und genieß' die Show!").setEphemeral(true).queue();
            */

        }else if (event.getName().equals("mapvote-history")) {
            event.getInteraction().replyEmbeds(mapvoteLogService.createEmbedMapvoteLog()).setEphemeral(true).queue();
        }
    }

    /**
     * Updates all usable slash commands when guild is ready
     *
     * @param event automatic handed over event which contains event-details
     */
    @Override
    public void onGuildReady(GuildReadyEvent event) {

        //Server-Only
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("starte-mapvote", "Starte einen Mapvote!")
                .addOptions(
                        new OptionData(OptionType.STRING, "layer1", "Das erste Layer, welches im Mapvote zur Verfügung gestellt werden soll", true),
                        new OptionData(OptionType.STRING, "layer2", "Das zweite Layer, welches im Mapvote zur Verfügung gestellt werden soll", true),
                        new OptionData(OptionType.STRING, "layer3", "Das dritte Layer, welches im Mapvote zur Verfügung gestellt werden soll", true),
                        new OptionData(OptionType.INTEGER, "timer", "Anzahl an Minuten wie lange der Vote laufen soll", true).setMinValue(3).setMaxValue(10)
                ));

        commandData.add(Commands.slash("mapvote-history", "Bekomme die Historie der durchgeführten Mapvotes der letzten 30 Tage inklusive Layer und Stimmzahl!"));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
