package commands.runnables.fisherycategory;

import commands.listeners.CommandProperties;
import commands.runnables.FisheryAbstract;
import constants.Category;
import constants.FisheryCategoryInterface;
import constants.Permission;
import core.EmbedFactory;
import core.utils.EmbedUtil;
import core.utils.MentionUtil;
import core.PatreonCache;
import core.TextManager;
import core.utils.StringUtil;
import mysql.modules.fisheryusers.DBFishery;
import mysql.modules.fisheryusers.FisheryUserBean;
import mysql.modules.fisheryusers.FisheryUserPowerUpBean;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@CommandProperties(
        trigger = "gear",
        botPermissions = Permission.USE_EXTERNAL_EMOJIS,
        emoji = "\uD83C\uDFA3",
        executableWithoutArgs = true,
        aliases = {"equip", "equipment", "inventory", "level"}
)
public class GearCommand extends FisheryAbstract {

    public GearCommand(Locale locale, String prefix) {
        super(locale, prefix);
    }

    @Override
    public boolean onMessageReceivedSuccessful(MessageCreateEvent event, String followedString) throws ExecutionException, InterruptedException {
        Server server = event.getServer().get();
        Message message = event.getMessage();
        ArrayList<User> list = MentionUtil.getUsers(message, followedString).getList();
        if (list.size() > 5) {
            event.getChannel().sendMessage(EmbedFactory.getEmbedError(this,
                    TextManager.getString(getLocale(), TextManager.GENERAL, "too_many_users"))).get();
            return false;
        }
        boolean userMentioned = true;
        boolean userBefore = list.size() > 0;
        list.removeIf(User::isBot);
        if (list.size() == 0) {
            if (userBefore) {
                event.getChannel().sendMessage(EmbedFactory.getEmbedError(this, TextManager.getString(getLocale(), TextManager.GENERAL, "nobot"))).get();
                return false;
            } else {
                list.add(message.getUserAuthor().get());
                userMentioned = false;
            }
        }

        List<Role> buyableRoles = DBFishery.getInstance().getBean(server.getId()).getRoles();
        for (User user : list) {
            FisheryUserBean fisheryUserBean = DBFishery.getInstance().getBean(event.getServer().get().getId()).getUserBean(user.getId());
            EmbedBuilder eb = EmbedFactory.getEmbedDefault()
                    .setDescription(getString("desc", StringUtil.numToString(fisheryUserBean.getFish()), StringUtil.numToString(fisheryUserBean.getCoins())));
            if (eb != null) {
                boolean patron = PatreonCache.getInstance().getPatreonLevel(user.getId()) >= 1;
                String patreonEmoji = "\uD83D\uDC51";
                String displayName = user.getDisplayName(server);
                while (displayName.length() > 0 && displayName.startsWith(patreonEmoji))
                    displayName = displayName.substring(patreonEmoji.length());

                eb.setAuthor(TextManager.getString(getLocale(), TextManager.GENERAL, "rankingprogress_title", patron, displayName, patreonEmoji), "", user.getAvatar())
                        .setThumbnail(user.getAvatar());
                if (patron) eb.setColor(Color.YELLOW);

                //Gear
                StringBuilder gearString = new StringBuilder();
                for (FisheryUserPowerUpBean slot : fisheryUserBean.getPowerUpMap().values()) {
                    gearString.append(getString("gear_slot",
                            FisheryCategoryInterface.PRODUCT_EMOJIS[slot.getPowerUpId()],
                            TextManager.getString(getLocale(), Category.FISHERY, "buy_product_" + slot.getPowerUpId() + "_0"),
                            String.valueOf(slot.getLevel())
                    )).append("\n");
                }
                eb.addField(getString("gear_title"), gearString.toString(), false);

                int roleLvl = fisheryUserBean.getPowerUp(FisheryCategoryInterface.ROLE).getLevel();
                eb.addField(getString("stats_title"), getString("stats_content",
                        StringUtil.numToString(fisheryUserBean.getPowerUp(FisheryCategoryInterface.PER_MESSAGE).getEffect()),
                        StringUtil.numToString(fisheryUserBean.getPowerUp(FisheryCategoryInterface.PER_DAY).getEffect()),
                        StringUtil.numToString(fisheryUserBean.getPowerUp(FisheryCategoryInterface.PER_VC).getEffect()),
                        StringUtil.numToString(fisheryUserBean.getPowerUp(FisheryCategoryInterface.PER_TREASURE).getEffect()),
                        buyableRoles.size() > 0 && roleLvl > 0 && roleLvl <= buyableRoles.size() ? buyableRoles.get(roleLvl - 1).getMentionTag() : "**-**",
                        StringUtil.numToString(fisheryUserBean.getPowerUp(FisheryCategoryInterface.PER_SURVEY).getEffect()),
                        fisheryUserBean.getServerBean().hasFisheryCoinsGivenLimit() ? StringUtil.numToString(fisheryUserBean.getCoinsGivenMax()) : "∞"
                ), false);

                if (!userMentioned) {
                    eb.setFooter(TextManager.getString(getLocale(), TextManager.GENERAL, "mention_optional"));
                    if (followedString.length() > 0)
                        EmbedUtil.addNoResultsLog(eb, getLocale(), followedString);
                }

                event.getChannel().sendMessage(eb).get();
            }
        }
        return true;
    }

}