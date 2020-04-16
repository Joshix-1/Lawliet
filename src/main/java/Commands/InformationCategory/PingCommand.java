package Commands.InformationCategory;

import CommandListeners.CommandProperties;

import CommandSupporters.Command;
import Core.CustomThread;
import Core.EmbedFactory;
import Core.ExceptionHandler;
import Core.Tools.StringTools;
import Core.Tools.TimeTools;
import MySQL.Modules.Survey.DBSurvey;
import ServerStuff.WebCommunicationServer.Events.OnTopGG;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;

@CommandProperties(
        trigger = "ping",
        emoji = "\uD83C\uDFD3",
        thumbnail = "http://icons.iconarchive.com/icons/graphicloads/100-flat-2/128/arrow-refresh-4-icon.png",
        executable = false
)
public class PingCommand extends Command {

    @Override
    public boolean onMessageReceived(MessageCreateEvent event, String followedString) throws Throwable {
        Instant creationTime = ((CustomThread)Thread.currentThread()).getCreationTime();

        long milisInternal = TimeTools.getMilisBetweenInstants(creationTime, Instant.now());

        Instant startTime = Instant.now();
        Message message = event.getServerTextChannel().get().sendMessage(EmbedFactory.getCommandEmbedStandard(this, getString("pong_start", StringTools.numToString(getLocale(), milisInternal)))).get();
        Instant endTime = Instant.now();

        long milisDiscordServers = TimeTools.getMilisBetweenInstants(startTime, endTime);
        message.edit(EmbedFactory.getCommandEmbedStandard(this, getString("pong_end", StringTools.numToString(getLocale(), milisInternal), StringTools.numToString(getLocale(), milisDiscordServers)))).get();

        return true;
    }

}