package websockets.webcomserver.events;

import commands.listeners.OnTrackerRequestListener;
import commands.Command;
import commands.CommandContainer;
import commands.CommandManager;
import constants.Category;
import core.TextManager;
import websockets.webcomserver.EventAbstract;
import websockets.webcomserver.WebComServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;

public class OnCommandList extends EventAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(OnCommandList.class);

    public OnCommandList(WebComServer webComServer, String event) {
        super(webComServer, event);
    }

    @Override
    protected JSONObject processData(JSONObject requestJSON, WebComServer webComServer) throws Exception {
        JSONObject mainJSON = new JSONObject();
        JSONArray arrayJSON = new JSONArray();
        HashMap<String, JSONObject> categories = new HashMap<>();

        //Add every command category
        for(String categoryId: Category.LIST) {
            JSONObject categoryJSON = new JSONObject();
            categoryJSON.put("id", categoryId);
            categoryJSON.put("name", webComServer.getLanguagePack(TextManager.COMMANDS, categoryId));
            categoryJSON.put("commands", new JSONArray());
            categories.put(categoryId, categoryJSON);
            arrayJSON.put(categoryJSON);
        }

        //Add every command
        CommandContainer.getInstance().getFullCommandList()
                .forEach(clazz -> {
                    try {
                        Command command = CommandManager.createCommandByClass(clazz, Locale.US, "L.");
                        String trigger = command.getTrigger();

                        if (!trigger.equals("help")) {
                            JSONObject commandJSON = new JSONObject();
                            commandJSON.put("trigger", trigger);
                            commandJSON.put("emoji", command.getEmoji());
                            commandJSON.put("title", webComServer.getLanguagePack(command.getCategory(), trigger + "_title"));
                            commandJSON.put("desc_short", webComServer.getLanguagePack(command.getCategory(), trigger + "_description"));
                            commandJSON.put("desc_long", webComServer.getLanguagePack(command.getCategory(), trigger + "_helptext"));
                            commandJSON.put("usage", webComServer.getCommandSpecs(command.getCategory(), trigger + "_usage", trigger));
                            commandJSON.put("examples", webComServer.getCommandSpecs(command.getCategory(), trigger + "_examples", trigger));
                            commandJSON.put("user_permissions", webComServer.getCommandPermissions(command));
                            commandJSON.put("nsfw", command.isNsfw());
                            commandJSON.put("requires_user_permissions", command.isModCommand());
                            commandJSON.put("can_be_tracked", command instanceof OnTrackerRequestListener);
                            commandJSON.put("patron_only", command.isPatreonRequired());

                            categories.get(command.getCategory()).getJSONArray("commands").put(commandJSON);
                        }
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        LOGGER.error("Could not create class", e);
                    }
                });

        mainJSON.put("categories", arrayJSON);
        return mainJSON;
    }

}
