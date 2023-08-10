package me.loving11ish.clans.externalHooks;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.ColorUtils;

public class PlugManXAPI {

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static boolean isPlugManXEnabled() {
        try {
            Class.forName("com.rylinaux.plugman.PlugMan");
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFound PlugManX main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aCould not find PlugManX main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return false;
        }
    }
}
