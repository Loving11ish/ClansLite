package me.loving11ish.clans.utils;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanChatSpyToggledEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.ClanPlayer;

import java.io.IOException;
import java.util.*;

public class UserMapStorageUtil {

    private static final FoliaLib foliaLib = Clans.getFoliaLib();

    private static final Map<UUID, ClanPlayer> userMap = new HashMap<>();

    private static final FileConfiguration userMapConfig = Clans.getPlugin().userMapFileManager.getUserMapConfig();
    private static final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";

    public static void saveUserMap() throws IOException {
        for (Map.Entry<UUID, ClanPlayer> entry : userMap.entrySet()) {
            userMapConfig.set("users.data." + entry.getKey() + ".javaUUID", entry.getValue().getJavaUUID());
            userMapConfig.set("users.data." + entry.getKey() + ".lastPlayerName", entry.getValue().getLastPlayerName());
            userMapConfig.set("users.data." + entry.getKey() + ".pointBalance", entry.getValue().getPointBalance());
            userMapConfig.set("users.data." + entry.getKey() + ".canChatSpy", entry.getValue().getCanChatSpy());
            userMapConfig.set("users.data." + entry.getKey() + ".isBedrockPlayer", entry.getValue().isBedrockPlayer());
            if (entry.getValue().isBedrockPlayer()) {
                userMapConfig.set("users.data." + entry.getKey() + ".bedrockUUID", entry.getValue().getBedrockUUID());
            }
        }
        Clans.getPlugin().userMapFileManager.saveUserMapConfig();
    }

    public static void restoreUserMap() throws IOException {
        userMap.clear();
        userMapConfig.getConfigurationSection("users.data").getKeys(false).forEach(key -> {
            UUID uuid = UUID.fromString(key);

            String javaUUID = userMapConfig.getString("users.data." + key + ".javaUUID");
            String lastPlayerName = userMapConfig.getString("users.data." + key + ".lastPlayerName");
            int pointBalance = userMapConfig.getInt("users.data." + key + ".pointBalance");
            boolean canChatSpy = userMapConfig.getBoolean("users.data." + key + ".canChatSpy");
            boolean isBedrockPlayer = userMapConfig.getBoolean("users.data." + key + ".isBedrockPlayer");
            String bedrockUUID = userMapConfig.getString("users.data." + key + ".bedrockUUID");

            ClanPlayer clanPlayer = new ClanPlayer(javaUUID, lastPlayerName);

            clanPlayer.setPointBalance(pointBalance);
            clanPlayer.setCanChatSpy(canChatSpy);
            clanPlayer.setBedrockPlayer(isBedrockPlayer);
            clanPlayer.setBedrockUUID(bedrockUUID);

            userMap.put(uuid, clanPlayer);
        });
    }

    public static void addToUserMap(Player player) {
        UUID uuid = player.getUniqueId();
        String javaUUID = uuid.toString();
        String lastPlayerName = player.getName();
        ClanPlayer clanPlayer = new ClanPlayer(javaUUID, lastPlayerName);
        userMap.put(uuid, clanPlayer);
    }

    public static void addBedrockPlayerToUserMap(Player player) {
        UUID uuid = player.getUniqueId();
        if (Clans.getFloodgateApi() != null) {
            FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
            UUID bedrockPlayerUUID = floodgatePlayer.getJavaUniqueId();
            String javaUUID = floodgatePlayer.getJavaUniqueId().toString();
            String lastPlayerName = floodgatePlayer.getUsername();
            ClanPlayer clanPlayer = new ClanPlayer(javaUUID, lastPlayerName);
            clanPlayer.setBedrockPlayer(true);
            clanPlayer.setBedrockUUID(floodgatePlayer.getCorrectUniqueId().toString());
            userMap.put(bedrockPlayerUUID, clanPlayer);
        }

    }

    public static boolean isUserExisting(Player player) {
        UUID uuid = player.getUniqueId();
        if (userMap.containsKey(uuid)) {
            return true;
        }
        return false;
    }

    public static ClanPlayer getClanPlayerByBukkitPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (userMap.containsKey(uuid)) {
            return userMap.get(uuid);
        } else {
            MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, player.getName()));
            MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, player.getName()));
        }
        return null;
    }

    public static ClanPlayer getClanPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        if (userMap.containsKey(uuid)) {
            return userMap.get(uuid);
        } else {
            MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName()));
            MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName()));
        }
        return null;
    }

    public static Player getBukkitPlayerByName(String name) {
        for (ClanPlayer clanPlayer : userMap.values()) {
            if (clanPlayer.getLastPlayerName().equalsIgnoreCase(name)) {
                return Bukkit.getPlayer(clanPlayer.getLastPlayerName());
            } else {
                MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name));
                MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-2")
                        .replace(PLAYER_PLACEHOLDER, name));
            }
        }
        return null;
    }

    public static Player getBukkitPlayerByUUID(UUID uuid) {
        String uuidString = uuid.toString();
        for (ClanPlayer clanPlayer : userMap.values()) {
            if (clanPlayer.getJavaUUID().equalsIgnoreCase(uuidString)) {
                return Bukkit.getPlayer(clanPlayer.getJavaUUID());
            }
        }
        return null;
    }

    public static OfflinePlayer getBukkitOfflinePlayerByName(String name) {
        for (ClanPlayer clanPlayer : userMap.values()) {
            if (clanPlayer.getLastPlayerName().equalsIgnoreCase(name)) {
                return Bukkit.getOfflinePlayer(UUID.fromString(clanPlayer.getJavaUUID()));
            } else {
                MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name));
                MessageUtils.sendConsole(messagesConfig.getString("clan-player-not-found-2")
                        .replace(PLAYER_PLACEHOLDER, name));
            }
        }
        return null;
    }

    public static OfflinePlayer getBukkitOfflinePlayerByUUID(UUID uuid) {
        String uuidString = uuid.toString();
        for (ClanPlayer clanPlayer : userMap.values()) {
            if (clanPlayer.getJavaUUID().equalsIgnoreCase(uuidString)) {
                return Bukkit.getOfflinePlayer(UUID.fromString(clanPlayer.getJavaUUID()));
            }
        }
        return null;
    }

    public static boolean hasPlayerNameChanged(Player player) {
        for (ClanPlayer clanPlayer : userMap.values()) {
            if (!player.getName().equals(clanPlayer.getLastPlayerName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBedrockPlayerJavaUUIDChanged(Player player) {
        UUID uuid = player.getUniqueId();
        for (ClanPlayer clanPlayer : userMap.values()) {
            if (Clans.getFloodgateApi() != null) {
                FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
                if (!(floodgatePlayer.getJavaUniqueId().toString().equals(clanPlayer.getBedrockUUID()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void updatePlayerName(Player player) {
        UUID uuid = player.getUniqueId();
        String newPlayerName = player.getName();
        ClanPlayer clanPlayer = userMap.get(uuid);
        clanPlayer.setLastPlayerName(newPlayerName);
        userMap.replace(uuid, clanPlayer);
    }

    public static void updateBedrockPlayerJavaUUID(Player player) {
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        if (Clans.getFloodgateApi() != null) {
            FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
            String newJavaUUID = floodgatePlayer.getJavaUniqueId().toString();
            clanPlayer.setJavaUUID(newJavaUUID);
            userMap.replace(uuid, clanPlayer);
        }

    }

    public static boolean toggleChatSpy(Player player) {
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        if (!clanPlayer.getCanChatSpy()) {
            clanPlayer.setCanChatSpy(true);

            foliaLib.getImpl().runAsync((task) -> {
                fireAsyncClanChatSpyToggledEvent(player, clanPlayer, true);
                MessageUtils.sendDebugConsole("Fired AsyncClanChatSpyToggledEvent");
            });

            return true;
        } else {
            clanPlayer.setCanChatSpy(false);

            foliaLib.getImpl().runAsync((task) -> {
                fireAsyncClanChatSpyToggledEvent(player, clanPlayer, false);
                MessageUtils.sendDebugConsole("Fired AsyncClanChatSpyToggledEvent");
            });

            return false;
        }
    }

    public static boolean hasEnoughPoints(Player player, int points) {
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        if (clanPlayer.getPointBalance() >= points) {
            return true;
        }
        return false;
    }

    public static int getPointBalanceByBukkitPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        return clanPlayer.getPointBalance();
    }

    public static int getPointBalanceByBukkitOfflinePlayer(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        return clanPlayer.getPointBalance();
    }

    public static void addPointsToOnlinePlayer(Player player, int value) {
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        int currentPointBalance = clanPlayer.getPointBalance();
        clanPlayer.setPointBalance(currentPointBalance + value);
        userMap.replace(uuid, clanPlayer);
    }

    public static void addPointsToOfflinePlayer(OfflinePlayer offlinePlayer, int value) {
        UUID uuid = offlinePlayer.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        int currentPointBalance = clanPlayer.getPointBalance();
        clanPlayer.setPointBalance(currentPointBalance + value);
        userMap.replace(uuid, clanPlayer);
    }

    public static boolean withdrawPoints(Player player, int points) {
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        int currentPointValue = clanPlayer.getPointBalance();
        if (currentPointValue != 0) {
            if (hasEnoughPoints(player, points)) {
                clanPlayer.setPointBalance(currentPointValue - points);
                return true;
            }
            return false;
        }
        return false;
    }

    public static void resetOnlinePlayerPointBalance(Player player) {
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        clanPlayer.setPointBalance(0);
        userMap.replace(uuid, clanPlayer);
    }

    public static void resetOfflinePlayerPointBalance(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        ClanPlayer clanPlayer = userMap.get(uuid);
        clanPlayer.setPointBalance(0);
        userMap.replace(uuid, clanPlayer);
    }

    public static Set<UUID> getRawUserMapList() {
        return userMap.keySet();
    }

    public static List<String> getAllPlayerPointsValues() {
        List<String> pointValues = new ArrayList<>();
        for (ClanPlayer clanPlayer : userMap.values()) {
            String value = String.valueOf(clanPlayer.getPointBalance());
            pointValues.add(value);
        }
        return pointValues;
    }

    public static Map<UUID, ClanPlayer> getUserMap() {
        return userMap;
    }

    private static void fireAsyncClanChatSpyToggledEvent(Player player, ClanPlayer clanPlayer, boolean chatSpyToggledState) {
        AsyncClanChatSpyToggledEvent asyncClanChatSpyToggledEvent = new AsyncClanChatSpyToggledEvent(true, player, clanPlayer, chatSpyToggledState);
        Bukkit.getPluginManager().callEvent(asyncClanChatSpyToggledEvent);
    }
}
