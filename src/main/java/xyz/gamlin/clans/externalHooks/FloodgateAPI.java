package xyz.gamlin.clans.externalHooks;

import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.utils.ColorUtils;

import java.util.logging.Logger;

public class FloodgateAPI {

    static Logger logger = Clans.getPlugin().getLogger();

    public static boolean isFloodgateEnabled() {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFound FloodgateApi class at:"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dorg.geysermc.floodgate.api.FloodgateApi"));
            }
            return true;
        } catch (ClassNotFoundException e) {
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aCould not find FloodgateApi class at:"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dorg.geysermc.floodgate.api.FloodgateApi"));
            }
            return false;
        }
    }
}
