package commands.runnables.nsfwcategory;

import commands.listeners.CommandProperties;
import commands.listeners.OnTrackerRequestListener;
import commands.runnables.PornSearchAbstract;

import java.util.Locale;

@CommandProperties(
        trigger = "r34",
        executableWithoutArgs = true,
        emoji = "\uD83D\uDD1E",
        nsfw = true,
        requiresEmbeds = false,
        withLoadingBar = true,
        aliases = {"rule34", "34"}
)
public class Rule34Command extends PornSearchAbstract implements OnTrackerRequestListener {

    public Rule34Command(Locale locale, String prefix) {
        super(locale, prefix);
    }

    @Override
    protected String getDomain() {
        return "rule34.xxx";
    }

    @Override
    protected String getImageTemplate() {
        return "https://img.rule34.xxx/images/%d/%f";
    }

    @Override
    public boolean isExplicit() { return true; }

}
