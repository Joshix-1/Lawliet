package ServerStuff;

import Constants.Settings;
import General.Internet.Internet;
import General.Internet.InternetResponse;
import General.Pair;
import General.SecretManager;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class BotsOnDiscord {

    public static boolean updateServerCount(int serverCount) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("guildCount", serverCount);
            Pair[] properties = new Pair[]{
                    new Pair<>("Content-Type", "application/json"),
                    new Pair<>("Authorization", SecretManager.getString("bots.ondiscord.token"))
            };
            InternetResponse internetResponse = Internet.getData("https://bots.ondiscord.xyz/bot-api/bots/" + Settings.LAWLIET_ID + "/guilds", jsonObject.toString(), properties).get();
            return internetResponse.getCode() == 204;
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}