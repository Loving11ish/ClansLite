package me.loving11ish.clans.externalhooks;

import me.loving11ish.clans.utils.MessageUtils;

public class FloodgateAPI {

    public static boolean isFloodgateEnabled() {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            MessageUtils.sendDebugConsole("Found FloodgateApi class at:");
            MessageUtils.sendDebugConsole("&dorg.geysermc.floodgate.api.FloodgateApi");
            return true;
        }catch (ClassNotFoundException e) {
            MessageUtils.sendDebugConsole("Could not find FloodgateApi class at:");
            MessageUtils.sendDebugConsole("&dorg.geysermc.floodgate.api.FloodgateApi");
            return false;
        }
    }
}
