package Commands.InteractionsCategory;
import CommandListeners.CommandProperties;
import CommandListeners.onRecievedListener;
import Commands.InteractionAbstract;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;

@CommandProperties(
        trigger = "marry",
        emoji = "\uD83D\uDC8D",
        executable = false
)
public class MarryCommand extends InteractionAbstract {
    protected String[] getGifs() {
        return new String[]{"https://media1.tenor.com/images/58bd69fb056bd54b80c92581f3cd9cf9/tenor.gif?itemid=10799169",
                "https://media1.tenor.com/images/d5725e6384ba532281e0ca8ec9e5db24/tenor.gif?itemid=7507476",
                "https://media1.tenor.com/images/69dbcb02b724d26644228a38e367d017/tenor.gif?itemid=14444888",
                "https://media1.tenor.com/images/4acbe4020146bd1a888ac27f6f07da21/tenor.gif?itemid=7302786",
                "https://media1.tenor.com/images/783e9568a1c06da76a50dc2c98129f11/tenor.gif?itemid=12390162",
                "https://media1.tenor.com/images/f3007ab7d6ba111cde3103840f2a5c52/tenor.gif?itemid=5412366"
        };
    }

}