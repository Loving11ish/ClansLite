package me.loving11ish.clans.externalHooks;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.ColorUtils;

public class PlaceholderAPI {

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFound PlaceholderAPI main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aCould not find PlaceholderAPI main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return false;
        }
    }
}
