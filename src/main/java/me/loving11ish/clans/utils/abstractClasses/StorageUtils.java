package me.loving11ish.clans.utils.abstractClasses;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import me.loving11ish.clans.models.Chest;
import me.loving11ish.clans.models.Clan;

import java.io.IOException;
import java.util.*;

public abstract class StorageUtils {

    public abstract void saveClans() throws IOException;

    public abstract void restoreClans() throws IOException;

    public abstract Clan createClan(Player player, String clanName);

    public abstract boolean isClanExisting(Player player);

    public abstract boolean deleteClan(Player player) throws IOException;

    public abstract boolean deleteOfflineClan(OfflinePlayer offlinePlayer) throws IOException;

    public abstract boolean isClanOwner(Player player);

    public abstract Clan findClanByOwner(Player player);

    public abstract Clan findClanByOfflineOwner(OfflinePlayer offlinePlayer);

    public abstract Clan findClanByPlayer(Player player);

    public abstract Clan findClanByOfflinePlayer(OfflinePlayer player);

    public abstract void updatePrefix(Player player, String prefix);

    public abstract boolean addClanMember(Clan clan, Player player);

    public abstract boolean removeClanMember(Clan clan, Player player);

    public abstract boolean removeOfflineClanMember(Clan clan, OfflinePlayer offlinePlayer);

    public abstract void addClanEnemy(Player clanOwner, Player enemyClanOwner);

    public abstract void removeClanEnemy(Player clanOwner, Player enemyClanOwner);

    public abstract void addClanAlly(Player clanOwner, Player allyClanOwner);

    public abstract void removeClanAlly(Player clanOwner, Player allyClanOwner);

    public abstract boolean isHomeSet(Clan clan);

    public abstract void deleteHome(Clan clan);

    public abstract String stripClanNameColorCodes(Clan clan);

    public abstract Clan transferClanOwner(Clan originalClan, Player originalClanOwner, Player newClanOwner) throws IOException;

    public abstract boolean hasEnoughPoints(Clan clan, int points);

    public abstract void addPoints(Clan clan, int points);

    public abstract boolean withdrawPoints(Clan clan, int points);

    public abstract void setPoints(Clan clan, int points);

    public abstract void resetPoints(Clan clan);

    public abstract Location getChestLocation(Chest chest);

    public abstract boolean isChestLocked(Clan clan, Location location);

    public abstract boolean isChestLocked(Location location);

    public abstract boolean addProtectedChest(Clan clan, Location location, Player player);

    public abstract boolean removeProtectedChest(String clanOwnerUUID, Location location) throws IOException;

    public abstract boolean removeProtectedChest(Clan clan, Location location, Player player) throws IOException;

    public abstract boolean removeProtectedChest(String clanOwnerUUID, Location location, Player player) throws IOException;

    public abstract Set<Map.Entry<String, Chest>> getAllProtectedChestsByClan(Clan clan);

    public abstract Location getChestByLocation(Clan clan, Location location);

    public abstract Chest getChestByLocation(Location location);

    public abstract List<Location> getAllProtectedChestsLocationsByClan(Clan clan);

    public abstract List<String> getPlayersWithChestAccessByChest(Chest chest);

    public abstract List<OfflinePlayer> getOfflinePlayersWithChestAccessByChest(Chest chest);

    public abstract boolean hasAccessToLockedChest(OfflinePlayer offlinePlayer, Chest chest);

    public abstract List<Location> getGlobalLockedChestLocations();

    public abstract List<Chest> getGlobalLockedChests();

    public abstract Set<Map.Entry<UUID, Clan>> getClans();

    public abstract Set<UUID> getRawClansList();

    public abstract Collection<Clan> getClanList();
}
