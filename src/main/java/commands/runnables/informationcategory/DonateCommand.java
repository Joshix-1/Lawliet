package commands.runnables.informationcategory;

import commands.Command;
import commands.listeners.CommandProperties;
import constants.ExternalLinks;
import core.DiscordApiCollection;
import core.EmbedFactory;
import core.TextManager;
import core.utils.StringUtil;
import mysql.modules.donators.DBDonators;
import mysql.modules.donators.DonatorBeanSlot;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@CommandProperties(
        trigger = "donate",
        emoji = "\uD83D\uDCB8",
        executableWithoutArgs = true,
        aliases = { "patreon", "donation", "premium" }
)
public class DonateCommand extends Command {

    public DonateCommand(Locale locale, String prefix) {
        super(locale, prefix);
    }

    @Override
    public boolean onMessageReceived(MessageCreateEvent event, String followedString) throws Throwable {
        StringBuilder donators = new StringBuilder();

        List<DonatorBeanSlot> donationsList = DBDonators.getInstance().getBean().getMap().values().stream()
                .filter(slot -> slot.getTotalDollars() > 0)
                .sorted((s1, s2) -> Double.compare(s2.getTotalDollars(), s1.getTotalDollars()))
                .collect(Collectors.toList());

        for(DonatorBeanSlot donatorBean: donationsList) {
            DiscordApiCollection.getInstance().getUserById(donatorBean.getUserId()).ifPresent(user ->
                    donators.append(getString("slot", StringUtil.escapeMarkdown(user.getDiscriminatedName()), StringUtil.doubleToString(donatorBean.getTotalDollars(), 2))).append("\n")
            );
        }

        if (donators.length() == 0) donators.append(TextManager.getString(getLocale(), TextManager.GENERAL, "empty"));
        EmbedBuilder eb = EmbedFactory.getEmbedDefault(this, getString("template", ExternalLinks.PATREON_PAGE, ExternalLinks.DONATION_URL));
        eb.addField(getString("donators"), donators.toString(), false);
        eb.setImage("https://cdn.discordapp.com/attachments/499629904380297226/589143402851991552/donate.png");

        event.getChannel().sendMessage(eb).get();
        return true;
    }

}
