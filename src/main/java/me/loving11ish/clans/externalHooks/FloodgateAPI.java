package me.loving11ish.clans.externalHooks;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.ColorUtils;

public class FloodgateAPI {

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static boolean isFloodgateEnabled() {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFound FloodgateApi class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dorg.geysermc.floodgate.api.FloodgateApi"));
            }
            return true;
        } catch (ClassNotFoundException e) {
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aCould not find FloodgateApi class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dorg.geysermc.floodgate.api.FloodgateApi"));
            }
            return false;
        }
    }
}
