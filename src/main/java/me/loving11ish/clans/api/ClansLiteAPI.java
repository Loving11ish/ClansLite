package me.loving11ish.clans.api;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.events.AsyncClanCreateEvent;
import me.loving11ish.clans.api.events.AsyncClanPrefixChangeEvent;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.models.ClanPlayer;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.MessageUtils;
import me.loving11ish.clans.utils.UserMapStorageUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

/**
 * This is the main API class for ClansLite.
 * This class contains all the methods that can be used to interact with the plugin.
 * This class must be initialized in order to use any of the methods.
 */
public class ClansLiteAPI {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();

    private final ArrayList<String> clanNamesList = new ArrayList<>();
    private final ArrayList<String> clanPrefixList = new ArrayList<>();

    int MIN_CHAR_LIMIT = clansConfig.getInt("clan-tags.min-character-limit");
    int MAX_CHAR_LIMIT = clansConfig.getInt("clan-tags.max-character-limit");
    private final List<String> bannedTags;

    /**
     * This constructor must be called in order to use any of the methods in this class.
     */
    public ClansLiteAPI() {
        Set<Map.Entry<UUID, Clan>> clansMap = ClansStorageUtil.getClans();
        clansMap.forEach((clans) -> clanNamesList.add(clans.getValue().getClanFinalName()));
        clansMap.forEach((clans) -> clanPrefixList.add(clans.getValue().getClanPrefix()));
        bannedTags = Clans.getPlugin().getConfig().getStringList("clan-tags.disallowed-tags");
    }

    /**
     *
     * @return Returns the plugin instance.
     */
    public Clans getEpicEcoPlugin(){
        return Clans.getPlugin();
    }

    /**
     *
     * @return Returns true if the plugin was enabled successfully, otherwise returns false.
     */
    public boolean isPluginEnabled() {
        return Clans.getPlugin().isPluginEnabled();
    }

    /**
     *
     * @return Returns a string of text that contains the full server package.
     */
    public String getServerPackage() {
        return Clans.getVersionCheckerUtils().getServerPackage();
    }

    /**
     *
     * @return Returns an integer that is the base major server version.
     */
    public int getMajorServerVersion() {
        return Clans.getVersionCheckerUtils().getVersion();
    }

    /**
     *
     * @return Returns `true` if the server or network is able to connect to the Mojang auth servers. Otherwise, returns `false`.
     *
     */
    public boolean isServerRunningOnline() {
        return Clans.getPlugin().isOnlineMode();
    }

    /**
     *
     * @return Returns the FoliaLib instance for ClansLite.
     */
    public FoliaLib getFoliaLibInstance() {
        return Clans.getFoliaLib();
    }

    /**
     *
     * @return Returns `true` if your current ClansLite plugin version does NOT mach the latest version listed on SpigotMC.
     */
    public boolean isClansLitePluginUpdateAvailable() {
        return Clans.getPlugin().isUpdateAvailable();
    }

    /**
     *
     * @return Returns the URL of the development build repository for ClansLite.
     */
    public String getClansLiteDevelopmentBuildRepository() {
        return Clans.getPlugin().getDevBuildRepository();
    }

    /**
     *
     * @return Returns a HashMap of all connected Bedrock players with a key of the Player and a value of their Java UUID.
     */
    public HashMap<Player, String> getConnectedBedrockPlayers() {
        return Clans.bedrockPlayers;
    }

    /**
     *
     * @return Returns a HashMap of all stored Clans.
     */
    public HashMap<UUID, Clan> getAllClans() {
        return ClansStorageUtil.getClansMap();
    }

    /**
     *
     * @return Returns a HashMap of all stored ClanPlayers.
     */
    public HashMap<UUID, ClanPlayer> getAllClanPlayers() {
        return UserMapStorageUtil.getClanPlayersMap();
    }

    /**
     * THIS METHOD IS NOT RECOMMENDED FOR USE ON LARGE SERVERS.
     * Please use {@link #getTopClansByClanPointsCache()} instead.
     *
     * @param maxListSize The maximum size of the list of Clans to return.
     * @return Returns a list of Clans sorted by clan points.
     */
    public List<Clan> getTopClansByClanPointsOnDemand(int maxListSize) {
        return ClansStorageUtil.getTopClansByClanPoints(maxListSize);
    }

    /**
     * The returned list is cached and updated asynchronously every 10 minutes.
     *
     * @return Returns a list of Clans sorted by clan points.
     */
    public List<Clan> getTopClansByClanPointsCache() {
        return ClansStorageUtil.getTopClansCache();
    }

    /**
     * THIS METHOD IS NOT RECOMMENDED FOR USE ON LARGE SERVERS.
     * Please use {@link #getTopClanPlayersByPlayerPointsCache()} instead.
     *
     * @param maxListSize The maximum size of the list of ClanPlayers to return.
     * @return Returns a list of ClanPlayers sorted by clan points.
     */
    public List<ClanPlayer> getTopClanPlayersByClanPointsOnDemand(int maxListSize) {
        return UserMapStorageUtil.getTopClanPlayersByPlayerPoints(maxListSize);
    }

    /**
     * The returned list is cached and updated asynchronously every 10 minutes.
     *
     * @return Returns a list of ClanPlayers sorted by Player points.
     */
    public List<ClanPlayer> getTopClanPlayersByPlayerPointsCache() {
        return UserMapStorageUtil.getTopClanPlayersCache();
    }

    /**
     *
     * @param player The Bukkit Player object to get a ClanPlayer from.
     * @return Returns a ClanPlayer object or null if not found.
     */
    public ClanPlayer getClanPlayerByBukkitPlayer(Player player) {
        return UserMapStorageUtil.getClanPlayerByBukkitPlayer(player);
    }

    /**
     *
     * @param offlinePlayer The Bukkit OfflinePlayer object to get a ClanPlayer from.
     * @return Returns a ClanPlayer object or null if not found.
     */
    public ClanPlayer getClanPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer) {
        return UserMapStorageUtil.getClanPlayerByBukkitOfflinePlayer(offlinePlayer);
    }

    /**
     * THIS WILL RETURN NULL IF THE PLAYER HAS NEVER JOINED THE SERVER BEFORE.
     * THIS WILL CAUSE AN ERROR IF THE PLAYER CHANGED THEIR NAME AND HAS NOT JOINED THE SERVER SINCE.
     * THIS METHOD IS NOT RECOMMENDED TO BE RELIED ON.
     * Please use {@link #getBukkitOfflinePlayerByUUID(UUID)} instead.
     *
     * @param lastKnownName The last known name of the player to get a Bukkit OfflinePlayer from.
     * @return Returns a Bukkit OfflinePlayer object or null if not found.
     */
    public OfflinePlayer getBukkitOfflinePlayerByLastKnownName(String lastKnownName) {
        return UserMapStorageUtil.getBukkitOfflinePlayerByName(lastKnownName);
    }

    /**
     *
     * @param uuid The UUID of the player to get a Bukkit OfflinePlayer from.
     * @return Returns a Bukkit OfflinePlayer object or null if not found.
     */
    public OfflinePlayer getBukkitOfflinePlayerByUUID(UUID uuid) {
        return UserMapStorageUtil.getBukkitOfflinePlayerByUUID(uuid);
    }

    /**
     * This method will only return a clan if the player is a member of a clan and NOT the clan owner.
     *
     * @param player The Bukkit Player object to get a Clan from.
     * @return Returns a Clan object or null if not found.
     */
    public Clan getClanByBukkitPlayer(Player player) {
        return ClansStorageUtil.findClanByPlayer(player);
    }

    /**
     * This method will only return a clan if the offline player is a member of a clan and NOT the clan owner.
     *
     * @param offlinePlayer The Bukkit OfflinePlayer object to get a Clan from.
     * @return Returns a Clan object or null if not found.
     */
    public Clan getClanByBukkitOfflinePlayer(OfflinePlayer offlinePlayer) {
        return ClansStorageUtil.findClanByOfflinePlayer(offlinePlayer);
    }

    /**
     * This method will only return a clan if the player is the owner of a clan.
     *
     * @param player The Bukkit Player object to get a Clan from.
     * @return Returns a Clan object or null if not found.
     */
    public Clan getClanByBukkitPlayerOwner(Player player) {
        return ClansStorageUtil.findClanByOwner(player);
    }

    /**
     * This method will only return a clan if the offline player is the owner of a clan.
     *
     * @param offlinePlayer The Bukkit OfflinePlayer object to get a Clan from.
     * @return Returns a Clan object or null if not found.
     */
    public Clan getClanByBukkitOfflinePlayerOwner(OfflinePlayer offlinePlayer) {
        return ClansStorageUtil.findClanByOfflineOwner(offlinePlayer);
    }

    /**
     * This method will only return a clan if the clan name is found.
     *
     * @param clanName The name of the clan to get a Clan from.
     * @return Returns a Clan object or null if not found.
     */
    public Clan getClanByClanName(String clanName) {
        return ClansStorageUtil.findClanByClanName(clanName);
    }

    /**
     * This method will perform multiple checks to see if the new clan is valid and can be created.
     * This method will create a Clan object and add it to the HashMap of Clans.
     * This method will fire an AsyncClanCreateEvent upon successful Clan creation.
     *
     * @param player The Bukkit Player object to create a Clan from.
     * @param clanName The name of the clan to create. (This cannot be changed later & cannot contain color codes)
     * @return Returns a Clan object.
     */
    public Clan createClan(Player player, String clanName) {
        if (bannedTags.contains(clanName)) {
            return null;
        }
        if (clanNamesList.contains(clanName)) {
            return null;
        }
        for (String names : clanNamesList) {
            if (StringUtils.equalsAnyIgnoreCase(names, clanName)) {
                return null;
            }
        }
        if (clanName.contains("&") || clanName.contains("#")) {
            return null;
        }
        if (ClansStorageUtil.isClanOwner(player)) {
            return null;
        }
        if (ClansStorageUtil.findClanByPlayer(player) != null) {
            return null;
        }
        if (clanName.length() < MIN_CHAR_LIMIT) {
            return null;
        } else if (clanName.length() > MAX_CHAR_LIMIT) {
            return null;
        }
        Clan clan = ClansStorageUtil.createClan(player, clanName);
        foliaLib.getImpl().runAsync((task) -> {
            fireAsyncClanCreateEvent(player, clan);
            MessageUtils.sendDebugConsole("Fired AsyncClanCreateEvent");
        });
        return clan;
    }

    /**
     *
     * @param player The Bukkit Player object to delete a Clan from.
     * @return Returns true if the Clan was deleted successfully, otherwise returns false.
     * @throws IOException Throws an IOException if the Clan could not be deleted.
     */
    public boolean deleteClan(Player player) throws IOException {
        return ClansStorageUtil.deleteClan(player);
    }

    /**
     * This method will perform multiple checks to see if the new prefix is valid and can be set.
     * This method will fire an AsyncClanPrefixChangeEvent upon successful prefix change.
     *
     * @param player The Bukkit Player object to get the clan from.
     * @param prefix The new prefix to set for the clan.
     */
    public void setClanPrefix(Player player, String prefix) {
        if (bannedTags.contains(prefix)) {
            return;
        }
        if (clanPrefixList.contains(prefix)) {
            return;
        }
        if (prefix.length() < MIN_CHAR_LIMIT) {
            return;
        } else if (prefix.length() > MAX_CHAR_LIMIT) {
            return;
        }
        foliaLib.getImpl().runAsync((task) -> {
            fireAsyncClanPrefixChangeEvent(player, ClansStorageUtil.findClanByOwner(player), ClansStorageUtil.findClanByOwner(player).getClanPrefix(), prefix);
            MessageUtils.sendDebugConsole("Fired AsyncClanPrefixChangeEvent");
        });
        ClansStorageUtil.updatePrefix(player, prefix);
    }

    /**
     * This method will perform multiple checks to see if the player is not already a member of a clan or a clan owner.
     *
     * @param clan The Clan object to add a member to.
     * @param player The Bukkit Player object to add to the Clan.
     * @return Returns true if the player was added to the Clan successfully, otherwise returns false.
     */
    public boolean addClanMember(Clan clan, Player player) {
        if (ClansStorageUtil.findClanByOwner(player) != null) {
            return false;
        }
        if (ClansStorageUtil.findClanByPlayer(player) != null) {
            return false;
        }
        return ClansStorageUtil.addClanMember(clan, player);
    }

    /**
     * This method will perform multiple checks to see if the player is in a clan and not a clan owner.
     *
     * @param clan The Clan object to remove a member from.
     * @param player The Bukkit Player object to remove from the Clan.
     * @return Returns true if the player was removed from the Clan successfully, otherwise returns false.
     */
    public boolean removeClanMember(Clan clan, Player player) {
        if (ClansStorageUtil.isClanOwner(player)) {
            return false;
        }
        if (ClansStorageUtil.findClanByPlayer(player) == null) {
            return false;
        }
        return ClansStorageUtil.removeClanMember(clan, player);
    }

    /**
     * This method will perform multiple checks to see if the player is in a clan and not a clan owner.
     *
     * @param clan The Clan object to remove a member from.
     * @param offlinePlayer The Bukkit OfflinePlayer object to remove from the Clan.
     * @return Returns true if the player was removed from the Clan successfully, otherwise returns false.
     */
    public boolean removeOfflineClanMember(Clan clan, OfflinePlayer offlinePlayer) {
        if (ClansStorageUtil.isClanOwner(offlinePlayer)) {
            return false;
        }
        if (ClansStorageUtil.findClanByOfflinePlayer(offlinePlayer) == null) {
            return false;
        }
        return ClansStorageUtil.removeOfflineClanMember(clan, offlinePlayer);
    }

    private void fireAsyncClanCreateEvent(Player player, Clan clan) {
        AsyncClanCreateEvent asyncClanCreateEvent = new AsyncClanCreateEvent(true, player, clan);
        Bukkit.getPluginManager().callEvent(asyncClanCreateEvent);
    }

    private void fireAsyncClanPrefixChangeEvent(Player createdBy, Clan clan, String oldPrefix, String newPrefix) {
        AsyncClanPrefixChangeEvent event = new AsyncClanPrefixChangeEvent(true, createdBy, clan, oldPrefix, newPrefix);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
