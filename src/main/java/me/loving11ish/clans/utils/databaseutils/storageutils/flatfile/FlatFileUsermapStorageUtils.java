package me.loving11ish.clans.utils.databaseutils.storageutils.flatfile;

import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.events.ClanChatSpyToggledEvent;
import me.loving11ish.clans.models.ClansLitePlayer;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.databaseutils.UsermapUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.io.IOException;
import java.util.*;

public class FlatFileUsermapStorageUtils extends UsermapUtils {

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    private Map<UUID, ClansLitePlayer> usermap = new HashMap<>();

    private FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private FileConfiguration usermapConfig = Clans.getPlugin().usermapFileManager.getUsermapConfig();
    private FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";

    @Override
    public void saveUsermap() throws IOException {
        for (Map.Entry<UUID, ClansLitePlayer> entry : usermap.entrySet()){
            usermapConfig.set("users.data." + entry.getKey() + ".javaUUID", entry.getValue().getJavaUUID());
            usermapConfig.set("users.data." + entry.getKey() + ".lastPlayerName", entry.getValue().getLastPlayerName());
            usermapConfig.set("users.data." + entry.getKey() + ".pointBalance", entry.getValue().getPointBalance());
            usermapConfig.set("users.data." + entry.getKey() + ".canChatSpy", entry.getValue().getWantsChatSpy());
            usermapConfig.set("users.data." + entry.getKey() + ".isBedrockPlayer", entry.getValue().isBedrockPlayer());
            if (entry.getValue().isBedrockPlayer()){
                usermapConfig.set("users.data." + entry.getKey() + ".bedrockUUID", entry.getValue().getBedrockUUID());
            }
        }
        Clans.getPlugin().usermapFileManager.saveUsermapConfig();
    }

    @Override
    public void restoreUsermap() throws IOException {
        usermap.clear();
        usermapConfig.getConfigurationSection("users.data").getKeys(false).forEach(key ->{
            UUID uuid = UUID.fromString(key);

            String javaUUID = usermapConfig.getString("users.data." + key + ".javaUUID");
            String lastPlayerName = usermapConfig.getString("users.data." + key + ".lastPlayerName");
            int pointBalance = usermapConfig.getInt("users.data." + key + ".pointBalance");
            boolean canChatSpy = usermapConfig.getBoolean("users.data." + key + ".canChatSpy");
            boolean isBedrockPlayer = usermapConfig.getBoolean("users.data." + key + ".isBedrockPlayer");
            String bedrockUUID = usermapConfig.getString("users.data." + key + ".bedrockUUID");

            ClansLitePlayer clansLitePlayer = new ClansLitePlayer(javaUUID, lastPlayerName);

            clansLitePlayer.setPointBalance(pointBalance);
            clansLitePlayer.setWantsChatSpy(canChatSpy);
            clansLitePlayer.setBedrockPlayer(isBedrockPlayer);
            clansLitePlayer.setBedrockUUID(bedrockUUID);

            usermap.put(uuid, clansLitePlayer);
        });
    }

    @Override
    public void addToUsermap(Player player){
        UUID uuid = player.getUniqueId();
        String javaUUID = uuid.toString();
        String lastPlayerName = player.getName();
        ClansLitePlayer clansLitePlayer = new ClansLitePlayer(javaUUID, lastPlayerName);
        usermap.put(uuid, clansLitePlayer);
    }

    @Override
    public void addBedrockPlayerToUsermap(Player player){
        UUID uuid = player.getUniqueId();
        if (Clans.getFloodgateApi() != null){
            FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
            UUID bedrockPlayerUUID = floodgatePlayer.getJavaUniqueId();
            String javaUUID = floodgatePlayer.getJavaUniqueId().toString();
            String lastPlayerName = floodgatePlayer.getUsername();
            ClansLitePlayer clansLitePlayer = new ClansLitePlayer(javaUUID, lastPlayerName);
            clansLitePlayer.setBedrockPlayer(true);
            clansLitePlayer.setBedrockUUID(floodgatePlayer.getCorrectUniqueId().toString());
            usermap.put(bedrockPlayerUUID, clansLitePlayer);
        }

    }

    @Override
    public boolean isUserExisting(Player player){
        UUID uuid = player.getUniqueId();
        if (usermap.containsKey(uuid)){
            return true;
        }
        return false;
    }

    @Override
    public ClansLitePlayer getClanPlayerByBukkitPlayer(Player player){
        UUID uuid = player.getUniqueId();
        if (usermap.containsKey(uuid)){
            ClansLitePlayer clansLitePlayer = usermap.get(uuid);
            return clansLitePlayer;
        }else {
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
        }
        return null;
    }

    @Override
    public ClansLitePlayer getClanPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        if (usermap.containsKey(uuid)){
            ClansLitePlayer clansLitePlayer = usermap.get(uuid);
            return clansLitePlayer;
        }else {
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName())));
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, offlinePlayer.getName())));
        }
        return null;
    }

    @Override
    public Player getBukkitPlayerByName(String name){
        for (ClansLitePlayer clansLitePlayer : usermap.values()){
            if (clansLitePlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getPlayer(clansLitePlayer.getLastPlayerName());
            }else {
                console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name)));
                console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-2")
                        .replace(PLAYER_PLACEHOLDER, name)));
            }
        }
        return null;
    }

    @Override
    public OfflinePlayer getBukkitOfflinePlayerByName(String name){
        for (ClansLitePlayer clansLitePlayer : usermap.values()){
            if (clansLitePlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getOfflinePlayer(UUID.fromString(clansLitePlayer.getJavaUUID()));
            }else {
                console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-1")
                        .replace(PLAYER_PLACEHOLDER, name)));
                console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-2")
                        .replace(PLAYER_PLACEHOLDER, name)));
            }
        }
        return null;
    }

    @Override
    public boolean hasPlayerNameChanged(Player player){
        for (ClansLitePlayer clansLitePlayer : usermap.values()){
            if (!player.getName().equals(clansLitePlayer.getLastPlayerName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasBedrockPlayerJavaUUIDChanged(Player player){
        UUID uuid = player.getUniqueId();
        for (ClansLitePlayer clansLitePlayer : usermap.values()){
            if (Clans.getFloodgateApi() != null){
                FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
                if (!(floodgatePlayer.getJavaUniqueId().toString().equals(clansLitePlayer.getBedrockUUID()))){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updatePlayerName(Player player){
        UUID uuid = player.getUniqueId();
        String newPlayerName = player.getName();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        clansLitePlayer.setLastPlayerName(newPlayerName);
        usermap.replace(uuid, clansLitePlayer);
    }

    @Override
    public void updateBedrockPlayerJavaUUID(Player player){
        UUID uuid = player.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        if (Clans.getFloodgateApi() != null){
            FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
            String newJavaUUID = floodgatePlayer.getJavaUniqueId().toString();
            clansLitePlayer.setJavaUUID(newJavaUUID);
            usermap.replace(uuid, clansLitePlayer);
        }

    }

    @Override
    public boolean toggleChatSpy(Player player){
        UUID uuid = player.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        if (!clansLitePlayer.getWantsChatSpy()){
            clansLitePlayer.setWantsChatSpy(true);
            fireClanChatSpyToggledEvent(player, clansLitePlayer,true);
            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanChatSpyToggledEvent"));
            }
            return true;
        }else {
            clansLitePlayer.setWantsChatSpy(false);
            fireClanChatSpyToggledEvent(player, clansLitePlayer,false);
            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanChatSpyToggledEvent"));
            }
            return false;
        }
    }

    @Override
    public boolean hasEnoughPoints(Player player, int points){
        UUID uuid = player.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        if (clansLitePlayer.getPointBalance() >= points){
            return true;
        }
        return false;
    }

    @Override
    public int getPointBalanceByBukkitPlayer(Player player){
        UUID uuid = player.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        return clansLitePlayer.getPointBalance();
    }

    @Override
    public int getPointBalanceByBukkitOfflinePlayer(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        return clansLitePlayer.getPointBalance();
    }

    @Override
    public void addPointsToOnlinePlayer(Player player, int value){
        UUID uuid = player.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        int currentPointBalance = clansLitePlayer.getPointBalance();
        clansLitePlayer.setPointBalance(currentPointBalance + value);
        usermap.replace(uuid, clansLitePlayer);
    }

    @Override
    public void addPointsToOfflinePlayer(OfflinePlayer offlinePlayer, int value){
        UUID uuid = offlinePlayer.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        int currentPointBalance = clansLitePlayer.getPointBalance();
        clansLitePlayer.setPointBalance(currentPointBalance + value);
        usermap.replace(uuid, clansLitePlayer);
    }

    @Override
    public boolean withdrawPoints(Player player, int points){
        UUID uuid = player.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        int currentPointValue = clansLitePlayer.getPointBalance();
        if (currentPointValue != 0){
            if (hasEnoughPoints(player, points)){
                clansLitePlayer.setPointBalance(currentPointValue - points);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void resetOnlinePlayerPointBalance(Player player){
        UUID uuid = player.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        clansLitePlayer.setPointBalance(0);
        usermap.replace(uuid, clansLitePlayer);
    }

    @Override
    public void resetOfflinePlayerPointBalance(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        ClansLitePlayer clansLitePlayer = usermap.get(uuid);
        clansLitePlayer.setPointBalance(0);
        usermap.replace(uuid, clansLitePlayer);
    }

    @Override
    public Set<UUID> getRawUsermapList(){
        return usermap.keySet();
    }

    @Override
    public List<String> getAllPlayerPointsValues(){
        List<String> pointValues = new ArrayList<>();
        for (ClansLitePlayer clansLitePlayer : usermap.values()){
            String value = String.valueOf(clansLitePlayer.getPointBalance());
            pointValues.add(value);
        }
        return pointValues;
    }

    @Override
    public Map<UUID, ClansLitePlayer> getUsermap() {
        return usermap;
    }

    private void fireClanChatSpyToggledEvent(Player player, ClansLitePlayer clansLitePlayer, boolean chatSpyToggledState) {
        ClanChatSpyToggledEvent clanChatSpyToggledEvent = new ClanChatSpyToggledEvent(player, clansLitePlayer, chatSpyToggledState);
        Bukkit.getPluginManager().callEvent(clanChatSpyToggledEvent);
    }
}
