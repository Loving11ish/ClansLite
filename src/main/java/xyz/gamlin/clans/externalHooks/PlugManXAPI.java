package xyz.gamlin.clans.externalHooks;

import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.utils.ColorUtils;

import java.util.logging.Logger;

public class PlugManXAPI {

    static Logger logger = Clans.getPlugin().getLogger();

    public static boolean isPlugManXEnabled() {
        try {
            Class.forName("com.rylinaux.plugman.PlugMan");
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFound PlugManX main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (Clans.getPlugin().getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aCould not find PlugManX main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return false;
        }
    }
}
