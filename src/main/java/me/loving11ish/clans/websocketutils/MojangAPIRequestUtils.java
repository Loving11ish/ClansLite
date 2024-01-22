package me.loving11ish.clans.websocketutils;

import com.github.lightlibs.simplehttpwrapper.SimpleHttpResponse;
import com.github.lightlibs.simplehttpwrapper.SimpleHttpWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.loving11ish.clans.utils.MessageUtils;

import java.io.IOException;

public class MojangAPIRequestUtils {

    public static boolean canGetOfflinePlayerData(String uuid, String playerName) throws IOException {
        SimpleHttpResponse response = SimpleHttpWrapper.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid, null);

        MessageUtils.sendDebugConsole("https request response code: &e" + response.getStatusCode());

        if (response.getStatusCode() / 100 == 4 || response.getStatusCode() == 204) {

            MessageUtils.sendDebugConsole("Unable to get offlinePlayerData");
            MessageUtils.sendDebugConsole("Server/network is running offline");

            return false;
        } else {

            MessageUtils.sendDebugConsole("Successfully got offlinePlayerData for :&e " + uuid);
            MessageUtils.sendDebugConsole("Server/network is running online");

            JsonObject object = (JsonObject) JsonParser.parseString(response.getData());
            return object.get("name").getAsString().equalsIgnoreCase(playerName);
        }
    }
}
