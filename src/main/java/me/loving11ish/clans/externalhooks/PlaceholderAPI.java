package me.loving11ish.clans.externalhooks;

import me.loving11ish.clans.utils.MessageUtils;

public class PlaceholderAPI {

    public static boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");
            MessageUtils.sendDebugConsole("Found PlaceholderAPI main class at:");
            MessageUtils.sendDebugConsole("&dme.clip.placeholderapi.PlaceholderAPIPlugin");
            return true;
        }catch (ClassNotFoundException e){
            MessageUtils.sendDebugConsole("Could not find PlaceholderAPI main class at:");
            MessageUtils.sendDebugConsole("&dme.clip.placeholderapi.PlaceholderAPIPlugin");
            return false;
        }
    }
}
