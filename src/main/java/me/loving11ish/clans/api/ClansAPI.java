package me.loving11ish.clans.api;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.ClanPlayer;
import me.loving11ish.clans.utils.UsermapStorageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ClansAPI {

    /**
     *
     * @return Returns the plugin instance.
     */
    public Clans getClansPlugin(){
        return Clans.getPlugin();
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
        return Clans.isOnlineMode();
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
    public static boolean isClansLitePluginUpdateAvailable() {
        return Clans.getPlugin().isUpdateAvailable();
    }

    /**
     *
     * @param player The Bukkit Player object to get a ClanPlayer from.
     * @return Returns a ClanPlayer object from the provided player, or null if the User cannot be found.
     */
    public static ClanPlayer getClanPlayerByBukkitPlayer(Player player) {
        return UsermapStorageUtil.getClanPlayerByBukkitPlayer(player);
    }

    /**
     *
     * @param offlinePlayer The Bukkit OfflinePlayer object to get a ClanPlayer from.
     * @return Returns a ClanPlayer object from the provided offline player, or null if the User cannot be found.
     */
    public static ClanPlayer getClanPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer) {
        return UsermapStorageUtil.getClanPlayerByBukkitOfflinePlayer(offlinePlayer);
    }

    /**
     *
     * @param lastKnownPlayerName The String value of the last known name for the Offline Player to get.
     * @return Returns a Bukkit OfflinePlayer object or may throw an error if an OfflinePlayer cannot be found.
     */
    public static OfflinePlayer getBukkitOfflinePlayerFromLastKnownPlayerName(String lastKnownPlayerName) {
        return UsermapStorageUtil.getBukkitOfflinePlayerByName(lastKnownPlayerName);
    }
}
