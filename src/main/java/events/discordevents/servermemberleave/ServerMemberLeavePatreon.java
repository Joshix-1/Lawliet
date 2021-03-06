package events.discordevents.servermemberleave;

import constants.AssetIds;
import core.DiscordApiCollection;
import core.PatreonCache;
import core.utils.StringUtil;
import events.discordevents.DiscordEvent;
import events.discordevents.eventtypeabstracts.ServerMemberLeaveAbstract;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.util.logging.ExceptionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DiscordEvent
public class ServerMemberLeavePatreon extends ServerMemberLeaveAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerMemberLeavePatreon.class);

    @Override
    public boolean onServerMemberLeave(ServerMemberLeaveEvent event) throws Throwable {
        if (event.getServer().getId() == AssetIds.SUPPORT_SERVER_ID) {
            if (PatreonCache.getInstance().getPatreonLevel(event.getUser().getId()) > 0) {
                LOGGER.info("PATREON LEFT (LEFT SERVER) {} ({})", event.getUser().getDiscriminatedName(), event.getUser().getId());
                DiscordApiCollection.getInstance().getOwner().sendMessage("PATREON USER LEFT (LEFT SERVER): " + StringUtil.escapeMarkdown(event.getUser().getDiscriminatedName())).exceptionally(ExceptionLogger.get());
            }
            PatreonCache.getInstance().resetUser(event.getUser().getId());
        }

        return true;
    }

}
