package events.discordevents.eventtypeabstracts;

import constants.Settings;
import core.EmbedFactory;
import events.discordevents.DiscordEventAbstract;
import org.javacord.api.event.message.MessageCreateEvent;

import java.time.Instant;
import java.util.ArrayList;

public abstract class MessageCreateAbstract extends DiscordEventAbstract {

    private Instant startTime;

    public abstract boolean onMessageCreate(MessageCreateEvent event) throws Throwable;

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }


    public static void onMessageCreateStatic(MessageCreateEvent event, ArrayList<DiscordEventAbstract> listenerList) {
        if (listenerList.isEmpty()) return;

        if (!event.getServer().isPresent()) {
            event.getChannel().sendMessage(EmbedFactory.getEmbedError()
                    .setTitle("❌ NOT SUPPORTED".toUpperCase())
                    .setDescription(String.format("Commands via dm aren't supported, you need to [\uD83D\uDD17 invite](%s) Lawliet into a server!", Settings.BOT_INVITE_URL)));
            return;
        }

        Instant startTime = Instant.now();
        execute(event, listenerList,
                listener -> {
                    ((MessageCreateAbstract) listener).setStartTime(startTime);
                    return ((MessageCreateAbstract) listener).onMessageCreate(event);
                }
        );
    }

}