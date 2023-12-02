package me.loving11ish.clans.utils.databaseutils.storageutils.flatfile;

import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.events.ClanChatSpyToggledEvent;
import me.loving11ish.clans.models.ClanPlayer;
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

    private Map<UUID, ClanPlayer> usermap = new HashMap<>();

    private FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private FileConfiguration usermapConfig = Clans.getPlugin().usermapFileManager.getUsermapConfig();
    private FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";

    @Override
    public void saveUsermap() throws IOException {
        for (Map.Entry<UUID, ClanPlayer> entry : usermap.entrySet()){
            usermapConfig.set("users.data." + entry.getKey() + ".javaUUID", entry.getValue().getJavaUUID());
            usermapConfig.set("users.data." + entry.getKey() + ".lastPlayerName", entry.getValue().getLastPlayerName());
            usermapConfig.set("users.data." + entry.getKey() + ".pointBalance", entry.getValue().getPointBalance());
            usermapConfig.set("users.data." + entry.getKey() + ".canChatSpy", entry.getValue().getCanChatSpy());
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

            ClanPlayer clanPlayer = new ClanPlayer(javaUUID, lastPlayerName);

            clanPlayer.setPointBalance(pointBalance);
            clanPlayer.setCanChatSpy(canChatSpy);
            clanPlayer.setBedrockPlayer(isBedrockPlayer);
            clanPlayer.setBedrockUUID(bedrockUUID);

            usermap.put(uuid, clanPlayer);
        });
    }

    @Override
    public void addToUsermap(Player player){
        UUID uuid = player.getUniqueId();
        String javaUUID = uuid.toString();
        String lastPlayerName = player.getName();
        ClanPlayer clanPlayer = new ClanPlayer(javaUUID, lastPlayerName);
        usermap.put(uuid, clanPlayer);
    }

    @Override
    public void addBedrockPlayerToUsermap(Player player){
        UUID uuid = player.getUniqueId();
        if (Clans.getFloodgateApi() != null){
            FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
            UUID bedrockPlayerUUID = floodgatePlayer.getJavaUniqueId();
            String javaUUID = floodgatePlayer.getJavaUniqueId().toString();
            String lastPlayerName = floodgatePlayer.getUsername();
            ClanPlayer clanPlayer = new ClanPlayer(javaUUID, lastPlayerName);
            clanPlayer.setBedrockPlayer(true);
            clanPlayer.setBedrockUUID(floodgatePlayer.getCorrectUniqueId().toString());
            usermap.put(bedrockPlayerUUID, clanPlayer);
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
    public ClanPlayer getClanPlayerByBukkitPlayer(Player player){
        UUID uuid = player.getUniqueId();
        if (usermap.containsKey(uuid)){
            ClanPlayer clanPlayer = usermap.get(uuid);
            return clanPlayer;
        }else {
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-1")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-player-not-found-2")
                    .replace(PLAYER_PLACEHOLDER, player.getName())));
        }
        return null;
    }

    @Override
    public ClanPlayer getClanPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        if (usermap.containsKey(uuid)){
            ClanPlayer clanPlayer = usermap.get(uuid);
            return clanPlayer;
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
        for (ClanPlayer clanPlayer : usermap.values()){
            if (clanPlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getPlayer(clanPlayer.getLastPlayerName());
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
        for (ClanPlayer clanPlayer : usermap.values()){
            if (clanPlayer.getLastPlayerName().equalsIgnoreCase(name)){
                return Bukkit.getOfflinePlayer(UUID.fromString(clanPlayer.getJavaUUID()));
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
        for (ClanPlayer clanPlayer : usermap.values()){
            if (!player.getName().equals(clanPlayer.getLastPlayerName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasBedrockPlayerJavaUUIDChanged(Player player){
        UUID uuid = player.getUniqueId();
        for (ClanPlayer clanPlayer : usermap.values()){
            if (Clans.getFloodgateApi() != null){
                FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
                if (!(floodgatePlayer.getJavaUniqueId().toString().equals(clanPlayer.getBedrockUUID()))){
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
        ClanPlayer clanPlayer = usermap.get(uuid);
        clanPlayer.setLastPlayerName(newPlayerName);
        usermap.replace(uuid, clanPlayer);
    }

    @Override
    public void updateBedrockPlayerJavaUUID(Player player){
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        if (Clans.getFloodgateApi() != null){
            FloodgatePlayer floodgatePlayer = Clans.getFloodgateApi().getPlayer(uuid);
            String newJavaUUID = floodgatePlayer.getJavaUniqueId().toString();
            clanPlayer.setJavaUUID(newJavaUUID);
            usermap.replace(uuid, clanPlayer);
        }

    }

    @Override
    public boolean toggleChatSpy(Player player){
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        if (!clanPlayer.getCanChatSpy()){
            clanPlayer.setCanChatSpy(true);
            fireClanChatSpyToggledEvent(player, clanPlayer ,true);
            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanChatSpyToggledEvent"));
            }
            return true;
        }else {
            clanPlayer.setCanChatSpy(false);
            fireClanChatSpyToggledEvent(player, clanPlayer ,false);
            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanChatSpyToggledEvent"));
            }
            return false;
        }
    }

    @Override
    public boolean hasEnoughPoints(Player player, int points){
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        if (clanPlayer.getPointBalance() >= points){
            return true;
        }
        return false;
    }

    @Override
    public int getPointBalanceByBukkitPlayer(Player player){
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        return clanPlayer.getPointBalance();
    }

    @Override
    public int getPointBalanceByBukkitOfflinePlayer(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        return clanPlayer.getPointBalance();
    }

    @Override
    public void addPointsToOnlinePlayer(Player player, int value){
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        int currentPointBalance = clanPlayer.getPointBalance();
        clanPlayer.setPointBalance(currentPointBalance + value);
        usermap.replace(uuid, clanPlayer);
    }

    @Override
    public void addPointsToOfflinePlayer(OfflinePlayer offlinePlayer, int value){
        UUID uuid = offlinePlayer.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        int currentPointBalance = clanPlayer.getPointBalance();
        clanPlayer.setPointBalance(currentPointBalance + value);
        usermap.replace(uuid, clanPlayer);
    }

    @Override
    public boolean withdrawPoints(Player player, int points){
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        int currentPointValue = clanPlayer.getPointBalance();
        if (currentPointValue != 0){
            if (hasEnoughPoints(player, points)){
                clanPlayer.setPointBalance(currentPointValue - points);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void resetOnlinePlayerPointBalance(Player player){
        UUID uuid = player.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        clanPlayer.setPointBalance(0);
        usermap.replace(uuid, clanPlayer);
    }

    @Override
    public void resetOfflinePlayerPointBalance(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        ClanPlayer clanPlayer = usermap.get(uuid);
        clanPlayer.setPointBalance(0);
        usermap.replace(uuid, clanPlayer);
    }

    @Override
    public Set<UUID> getRawUsermapList(){
        return usermap.keySet();
    }

    @Override
    public List<String> getAllPlayerPointsValues(){
        List<String> pointValues = new ArrayList<>();
        for (ClanPlayer clanPlayer : usermap.values()){
            String value = String.valueOf(clanPlayer.getPointBalance());
            pointValues.add(value);
        }
        return pointValues;
    }

    @Override
    public Map<UUID, ClanPlayer> getUsermap() {
        return usermap;
    }

    private void fireClanChatSpyToggledEvent(Player player, ClanPlayer clanPlayer, boolean chatSpyToggledState) {
        ClanChatSpyToggledEvent clanChatSpyToggledEvent = new ClanChatSpyToggledEvent(player, clanPlayer, chatSpyToggledState);
        Bukkit.getPluginManager().callEvent(clanChatSpyToggledEvent);
    }
}
