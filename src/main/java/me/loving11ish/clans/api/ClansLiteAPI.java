package me.loving11ish.clans.api;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.Clans;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ClansLiteAPI {

    private static final FileConfiguration config = Clans.getPlugin().getConfig();

    /**
     *
     * @return Returns the plugin instance.
     */
    public static Clans getEpicEcoPlugin(){
        return Clans.getPlugin();
    }

    /**
     *
     * @return Returns true if the plugin was enabled successfully, otherwise returns false.
     */
    public static boolean isPluginEnabled() {
        return Clans.getPlugin().isEnabled();
    }

    /**
     *
     * @return Returns a string of text that contains the full server package.
     */
    public static String getServerPackage() {
        return Clans.getVersionCheckerUtils().getServerPackage();
    }

    /**
     *
     * @return Returns an integer that is the base major server version.
     */
    public static int getMajorServerVersion() {
        return Clans.getVersionCheckerUtils().getVersion();
    }

    /**
     *
     * @return Returns `true` if the server or network is able to connect to the Mojang auth servers. Otherwise, returns `false`.
     *
     */
    public static boolean isServerRunningOnline() {
        return Clans.getPlugin().isOnlineMode();
    }

    /**
     *
     * @return Returns the FoliaLib instance for ClansLite.
     */
    public static FoliaLib getFoliaLibInstance() {
        return Clans.getFoliaLib();
    }

    /**
     *
     * @return Returns `true` if your current ClansLite plugin version does NOT mach the latest version listed on SpigotMC.
     */
    public static boolean isEpicEcoPluginUpdateAvailable() {
        return Clans.getPlugin().isUpdateAvailable();
    }

    /**
     *
     * @return Returns a HashMap of all connected Bedrock players with a key of the Player and a value of their Java UUID.
     */
    public static HashMap<Player, String> getConnectedBedrockPlayers() {
        return Clans.bedrockPlayers;
    }


}
