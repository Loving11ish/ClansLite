package xyz.gamlin.clans.utils.abstractUtils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.models.ClanPlayer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class UsermapUtils {

    public abstract void saveUsermap() throws IOException;

    public abstract void restoreUsermap() throws IOException;

    public abstract void addToUsermap(Player player);

    public abstract void addBedrockPlayerToUsermap(Player player);

    public abstract boolean isUserExisting(Player player);

    public abstract ClanPlayer getClanPlayerByBukkitPlayer(Player player);

    public abstract ClanPlayer getClanPlayerByBukkitOfflinePlayer(OfflinePlayer offlinePlayer);

    public abstract Player getBukkitPlayerByName(String name);

    public abstract OfflinePlayer getBukkitOfflinePlayerByName(String name);

    public abstract boolean hasPlayerNameChanged(Player player);

    public abstract boolean hasBedrockPlayerJavaUUIDChanged(Player player);

    public abstract void updatePlayerName(Player player);

    public abstract void updateBedrockPlayerJavaUUID(Player player);

    public abstract boolean toggleChatSpy(Player player);

    public abstract boolean hasEnoughPoints(Player player, int points);

    public abstract int getPointBalanceByBukkitPlayer(Player player);

    public abstract int getPointBalanceByBukkitOfflinePlayer(OfflinePlayer offlinePlayer);

    public abstract void addPointsToOnlinePlayer(Player player, int value);

    public abstract void addPointsToOfflinePlayer(OfflinePlayer offlinePlayer, int value);

    public abstract boolean withdrawPoints(Player player, int points);

    public abstract void resetOnlinePlayerPointBalance(Player player);

    public abstract void resetOfflinePlayerPointBalance(OfflinePlayer offlinePlayer);

    public abstract Set<UUID> getRawUsermapList();

    public abstract List<String> getAllPlayerPointsValues();

    public abstract Map<UUID, ClanPlayer> getUsermap();
}
