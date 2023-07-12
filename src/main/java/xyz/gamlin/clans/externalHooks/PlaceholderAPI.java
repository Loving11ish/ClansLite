package xyz.gamlin.clans.externalHooks;

import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.utils.ColorUtils;

import java.util.logging.Logger;

public class PlaceholderAPI {

    static Logger logger = Clans.getPlugin().getLogger();

    public static boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFound PlaceholderAPI main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aCould not find PlaceholderAPI main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return false;
        }
    }
}
