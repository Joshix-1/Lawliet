package events.discordevents.messagecreate;

import events.discordevents.DiscordEvent;
import events.discordevents.EventPriority;
import events.discordevents.eventtypeabstracts.MessageCreateAbstract;
import modules.WordFilterCheck;
import org.javacord.api.event.message.MessageCreateEvent;

@DiscordEvent(priority = EventPriority.HIGH, allowBannedUser = true)
public class MessageCreateBannedWords extends MessageCreateAbstract {

    @Override
    public boolean onMessageCreate(MessageCreateEvent event) throws Throwable {
        return WordFilterCheck.check(event.getMessage());
    }

}