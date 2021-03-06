package commands.runnables.nsfwcategory;

import commands.listeners.CommandProperties;
import commands.runnables.GelbooruAbstract;

import java.util.Locale;

@CommandProperties(
        trigger = "trap",
        executableWithoutArgs = true,
        emoji = "\uD83D\uDD1E",
        nsfw = true,
        requiresEmbeds = false,
        withLoadingBar = true
)
public class TrapCommand extends GelbooruAbstract {

    public TrapCommand(Locale locale, String prefix) {
        super(locale, prefix);
    }

    @Override
    protected String getSearchKey() {
        return "animated trap";
    }

    @Override
    protected boolean isAnimatedOnly() {
        return true;
    }

}