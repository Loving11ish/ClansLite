package me.loving11ish.clans.utils.databaseutils.storageutils.mysql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.events.ClanDisbandEvent;
import me.loving11ish.clans.api.events.ClanOfflineDisbandEvent;
import me.loving11ish.clans.api.events.ClanTransferOwnershipEvent;
import me.loving11ish.clans.databaseconnection.SQLConnectionUtils;
import me.loving11ish.clans.models.ProtectedChest;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.models.ClansLitePlayer;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.databaseutils.StorageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class MySQLClanStorageUtils extends StorageUtils {

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    private SQLConnectionUtils sqlConnectionUtils;
    private JdbcPooledConnectionSource connectionSource;

    public MySQLClanStorageUtils(SQLConnectionUtils sqlConnectionUtils, JdbcPooledConnectionSource connectionSource) throws SQLException {
        this.sqlConnectionUtils = sqlConnectionUtils;
        this.connectionSource = connectionSource;
    }

    private Dao<Clan, String> clansDao = DaoManager.createDao(connectionSource, Clan.class);

    private HashMap<UUID, Clan> clansList = new HashMap<>();

    private FileConfiguration clansStorage = Clans.getPlugin().clansFileManager.getClansConfig();
    private FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();
    private FileConfiguration clansConfig = Clans.getPlugin().getConfig();

    private final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");

    @Override
    public void saveClans() throws IOException {
        if (this.connectionSource != null){

        }
    }

    @Override
    public void restoreClans() throws IOException {
        if (this.connectionSource != null){

        }
    }

    @Override
    public Clan createClan(Player player, String clanName) {
        UUID ownerUUID = player.getUniqueId();
        String ownerUUIDString = player.getUniqueId().toString();
        Clan newClan = new Clan(ownerUUIDString, clanName);
        clansList.put(ownerUUID, newClan);

        return newClan;
    }

    @Override
    public boolean isClanExisting(Player player) {
        UUID uuid = player.getUniqueId();
        if (clansList.containsKey(uuid)){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteClan(Player player) throws IOException {
        if (this.connectionSource != null){

        }
        return false;
    }

    @Override
    public boolean deleteOfflineClan(OfflinePlayer offlinePlayer) throws IOException {
        if (this.connectionSource != null){

        }
        return false;
    }

    @Override
    public boolean isClanOwner(Player player) {
        UUID uuid = player.getUniqueId();
        String ownerUUID = uuid.toString();
        Clan clan = clansList.get(uuid);
        if (clan != null){
            if (clan.getId() == null){
                return false;
            }else {
                if (clan.getId().equals(ownerUUID)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Clan findClanByOwner(Player player) {
        UUID uuid = player.getUniqueId();
        return clansList.get(uuid);
    }

    @Override
    public Clan findClanByOfflineOwner(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        return clansList.get(uuid);
    }

    @Override
    public Clan findClanOwnerByClanPlayer(ClansLitePlayer clansLitePlayer) {
        UUID uuid = UUID.fromString(clansLitePlayer.getJavaUUID());
        return clansList.get(uuid);
    }

    @Override
    public Clan findClanByPlayer(Player player) {
        for (Clan clan : clansList.values()){
            if (findClanByOwner(player) != null) {
                return clan;
            }
            if (clan.getMembers() != null) {
                for (String member : clan.getMembers()) {
                    if (member.equals(player.getUniqueId().toString())) {
                        return clan;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Clan findClanByOfflinePlayer(OfflinePlayer player) {
        for (Clan clan : clansList.values()){
            if (findClanByOfflineOwner(player) != null){
                return clan;
            }
            if (clan.getMembers() != null){
                for (String member : clan.getMembers()){
                    if (member.equals(player.getUniqueId().toString())){
                        return clan;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Clan findClanPlayerByClanPlayer(ClansLitePlayer clansLitePlayer) {
        for (Clan clan : clansList.values()){
            if (findClanOwnerByClanPlayer(clansLitePlayer) != null){
                return clan;
            }
            if (clan.getMembers() != null){
                for (String member : clan.getMembers()){
                    if (member.equals(clansLitePlayer.getJavaUUID())){
                        return clan;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void updatePrefix(Player player, String prefix) {
        UUID uuid = player.getUniqueId();
        if (!isClanOwner(player)){
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-must-be-owner")));
            return;
        }
        Clan clan = clansList.get(uuid);
        clan.setPrefix(prefix);
    }

    @Override
    public boolean addClanMember(Clan clan, Player player) {
        UUID uuid = player.getUniqueId();
        String memberUUID = uuid.toString();
        clan.addMember(memberUUID);
        if (clansConfig.getBoolean("protections.chests.enabled")){
            HashMap<String, ProtectedChest> clanChestList = clan.getProtectedChests();
            if (!clanChestList.isEmpty()){
                for (Map.Entry<String, ProtectedChest> chestEntry : clanChestList.entrySet()){
                    ProtectedChest chest = chestEntry.getValue();
                    ArrayList<String> playersWithAccess = chest.getPlayersWithAccess();
                    playersWithAccess.add(memberUUID);
                    chest.setPlayersWithAccess(playersWithAccess);
                    clanChestList.replace(chestEntry.getKey(), chest);
                }
            }
        }
        return true;
    }

    @Override
    public boolean removeClanMember(Clan clan, Player player) {
        UUID uuid = player.getUniqueId();
        String memberUUID = uuid.toString();
        clan.removeMember(memberUUID);
        if (clansConfig.getBoolean("protections.chests.enabled")){
            HashMap<String, ProtectedChest> clanChestList = clan.getProtectedChests();
            if (!clanChestList.isEmpty()){
                for (Map.Entry<String, ProtectedChest> chestEntry : clanChestList.entrySet()){
                    ProtectedChest chest = chestEntry.getValue();
                    ArrayList<String> playersWithAccess = chest.getPlayersWithAccess();
                    playersWithAccess.remove(memberUUID);
                    chest.setPlayersWithAccess(playersWithAccess);
                    clanChestList.replace(chestEntry.getKey(), chest);
                }
            }
        }
        return true;
    }

    @Override
    public boolean removeOfflineClanMember(Clan clan, OfflinePlayer offlinePlayer) {
        UUID offlineUUID = offlinePlayer.getUniqueId();
        String offlineMemberUUID = offlineUUID.toString();
        clan.removeMember(offlineMemberUUID);
        if (clansConfig.getBoolean("protections.chests.enabled")){
            HashMap<String, ProtectedChest> clanChestList = clan.getProtectedChests();
            if (!clanChestList.isEmpty()){
                for (Map.Entry<String, ProtectedChest> chestEntry : clanChestList.entrySet()){
                    ProtectedChest chest = chestEntry.getValue();
                    ArrayList<String> playersWithAccess = chest.getPlayersWithAccess();
                    playersWithAccess.add(offlineMemberUUID);
                    chest.setPlayersWithAccess(playersWithAccess);
                    clanChestList.replace(chestEntry.getKey(), chest);
                }
            }
        }
        return true;
    }

    @Override
    public void addClanEnemy(Player clanOwner, Player enemyClanOwner) {
        UUID ownerUUID = clanOwner.getUniqueId();
        UUID enemyUUID = enemyClanOwner.getUniqueId();
        String enemyOwnerUUID = enemyUUID.toString();
        Clan clan = clansList.get(ownerUUID);
        clan.addEnemy(enemyOwnerUUID);
    }

    @Override
    public void removeClanEnemy(Player clanOwner, Player enemyClanOwner) {
        UUID ownerUUID = clanOwner.getUniqueId();
        UUID enemyUUID = enemyClanOwner.getUniqueId();
        String enemyOwnerUUID = enemyUUID.toString();
        Clan clan = clansList.get(ownerUUID);
        clan.removeEnemy(enemyOwnerUUID);
    }

    @Override
    public void addClanAlly(Player clanOwner, Player allyClanOwner) {
        UUID ownerUUID = clanOwner.getUniqueId();
        UUID uuid = allyClanOwner.getUniqueId();
        String allyUUID = uuid.toString();
        Clan clan = clansList.get(ownerUUID);
        clan.addAlly(allyUUID);
    }

    @Override
    public void removeClanAlly(Player clanOwner, Player allyClanOwner) {
        UUID ownerUUID = clanOwner.getUniqueId();
        UUID uuid = allyClanOwner.getUniqueId();
        String allyUUID = uuid.toString();
        Clan clan = clansList.get(ownerUUID);
        clan.removeAlly(allyUUID);
    }

    @Override
    public boolean isHomeSet(Clan clan) {
        if (clan.getClanHomeWorld() != null){
            return true;
        }
        return false;
    }

    @Override
    public void deleteHome(Clan clan) {
        if (this.connectionSource != null){

        }
    }

    @Override
    public String stripClanNameColorCodes(Clan clan) {
        String clanFinalName = clan.getName();
        if (clansConfig.getBoolean("general.developer-debug-mode.enabled")||!clansStorage.getBoolean("name-strip-colour-complete")
                ||clanFinalName.contains("&")||clanFinalName.contains("#")){
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFound Colour Code To Strip"));
            console.sendMessage(ColorUtils.translateColorCodes("&aOriginal Name: ") + clanFinalName);
            console.sendMessage(ColorUtils.translateColorCodes("&aNew Name: ") + (clanFinalName == null?null:STRIP_COLOR_PATTERN.matcher(clanFinalName).replaceAll("")));
        }
        return clanFinalName == null?null:STRIP_COLOR_PATTERN.matcher(clanFinalName).replaceAll("");
    }

    @Override
    public Clan transferClanOwner(Clan originalClan, Player originalClanOwner, Player newClanOwner) throws IOException {
        if (this.connectionSource != null){

        }
        return null;
    }

    @Override
    public boolean hasEnoughPoints(Clan clan, int points) {
        if (clan.getPoints() >= points){
            return true;
        }
        return false;
    }

    @Override
    public void addPoints(Clan clan, int points) {
        int currentPointValue = clan.getPoints();
        clan.setPoints(currentPointValue + points);
    }

    @Override
    public boolean withdrawPoints(Clan clan, int points) {
        int currentPointValue = clan.getPoints();
        if (currentPointValue != 0){
            if (hasEnoughPoints(clan, points)){
                clan.setPoints(currentPointValue - points);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void setPoints(Clan clan, int points) {
        clan.setPoints(points);
    }

    @Override
    public void resetPoints(Clan clan) {
        clan.setPoints(0);
    }

    @Override
    public Location getChestLocation(ProtectedChest chest) {
        String worldName = chest.getChestWorldName();
        double chestX = chest.getChestLocationX();
        double chestY = chest.getChestLocationY();
        double chestZ = chest.getChestLocationZ();
        World world = Bukkit.getWorld(worldName);
        if (world != null){
            return new Location(world, chestX, chestY, chestZ);
        }
        return null;
    }

    @Override
    public boolean isChestLocked(Clan clan, Location location) {
        HashMap<String, ProtectedChest> clanChestList = clan.getProtectedChests();
        for (Map.Entry<String, ProtectedChest> chest : clanChestList.entrySet()){
            Location chestLocation = getChestLocation(chest.getValue());
            if (chestLocation != null){
                if (chestLocation.equals(location)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isChestLocked(Location location) {
        List<ProtectedChest> allChests = getGlobalLockedChests();
        for (ProtectedChest chest : allChests){
            if (chest != null){
                String worldName = chest.getChestWorldName();
                World world = Bukkit.getWorld(worldName);
                Location chestLocation = new Location(world, chest.getChestLocationX(), chest.getChestLocationY(), chest.getChestLocationZ());
                if (chestLocation.equals(location)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean addProtectedChest(Clan clan, Location location, Player player) {
        HashMap<String, ProtectedChest> clanChestList = clan.getProtectedChests();
        if (isChestLocked(clan, location)){
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-chest-already-protected")));
            return false;
        }else {
            UUID newChestUUID = UUID.randomUUID();
            String chestUUID = newChestUUID.toString();
            ProtectedChest chest = new ProtectedChest(clan, location);
            clanChestList.put(chestUUID, chest);
            return true;
        }
    }

    @Override
    public boolean removeProtectedChest(String clanOwnerUUID, Location location) throws IOException {
        if (this.connectionSource != null){

        }
        return false;
    }

    @Override
    public boolean removeProtectedChest(Clan clan, Location location, Player player) throws IOException {
        String key = clan.getId();
        if (isChestLocked(clan, location)){
            if (removeProtectedChest(key, location)){
                return true;
            }
        }else {
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-chest-not-protected")));
            return false;
        }
        return false;
    }

    @Override
    public boolean removeProtectedChest(String clanOwnerUUID, Location location, Player player) throws IOException {
        UUID uuid = UUID.fromString(clanOwnerUUID);
        Clan clan = findClanByOfflineOwner(Bukkit.getOfflinePlayer(uuid));
        if (removeProtectedChest(clan, location, player)){
            return true;
        }
        return false;
    }

    @Override
    public Set<Map.Entry<String, ProtectedChest>> getAllProtectedChestsByClan(Clan clan) {
        return clan.getProtectedChests().entrySet();
    }

    @Override
    public Location getChestByLocation(Clan clan, Location location) {
        HashMap<String, ProtectedChest> clanChestList = clan.getProtectedChests();
        for (Map.Entry<String, ProtectedChest> chest : clanChestList.entrySet()){
            String worldName = chest.getValue().getChestWorldName();
            World world = Bukkit.getWorld(worldName);
            Location chestLocation = new Location(world, chest.getValue().getChestLocationX(), chest.getValue().getChestLocationY(), chest.getValue().getChestLocationZ());
            if (chestLocation.equals(location)){
                return chestLocation;
            }
        }
        return null;
    }

    @Override
    public ProtectedChest getChestByLocation(Location location) {
        List<ProtectedChest> allChests = getGlobalLockedChests();
        for (ProtectedChest chest : allChests){
            String worldName = chest.getChestWorldName();
            World world = Bukkit.getWorld(worldName);
            Location chestLocation = new Location(world, chest.getChestLocationX(), chest.getChestLocationY(), chest.getChestLocationZ());
            if (location.equals(chestLocation)){
                return chest;
            }
        }
        return null;
    }

    @Override
    public List<Location> getAllProtectedChestsLocationsByClan(Clan clan) {
        HashMap<String, ProtectedChest> clanChestList = clan.getProtectedChests();
        List<Location> allChestLocations = new ArrayList<>();
        for (Map.Entry<String, ProtectedChest> chest : clanChestList.entrySet()){
            String worldName = chest.getValue().getChestWorldName();
            World world = Bukkit.getWorld(worldName);
            Location chestLocation = new Location(world, chest.getValue().getChestLocationX(), chest.getValue().getChestLocationY(), chest.getValue().getChestLocationZ());
            allChestLocations.add(chestLocation);
        }
        return allChestLocations;
    }

    @Override
    public List<String> getPlayersWithChestAccessByChest(ProtectedChest chest) {
        return chest.getPlayersWithAccess();
    }

    @Override
    public List<OfflinePlayer> getOfflinePlayersWithChestAccessByChest(ProtectedChest chest) {
        List<String> playersWithAccess = getPlayersWithChestAccessByChest(chest);
        List<OfflinePlayer> offlinePlayersWithAccess = new ArrayList<>();
        for (String string : playersWithAccess){
            UUID uuid = UUID.fromString(string);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            offlinePlayersWithAccess.add(offlinePlayer);
        }
        return offlinePlayersWithAccess;
    }

    @Override
    public boolean hasAccessToLockedChest(OfflinePlayer offlinePlayer, ProtectedChest chest) {
        return getOfflinePlayersWithChestAccessByChest(chest).contains(offlinePlayer);
    }

    @Override
    public List<Location> getGlobalLockedChestLocations() {
        List<ProtectedChest> allLockedChest = getGlobalLockedChests();
        List<Location> allLockedChestLocations = new ArrayList<>();
        for (ProtectedChest chest : allLockedChest){
            String worldName = chest.getChestWorldName();
            World world = Bukkit.getWorld(worldName);
            Location location = new Location(world, chest.getChestLocationX(), chest.getChestLocationY(), chest.getChestLocationZ());
            allLockedChestLocations.add(location);
        }
        return allLockedChestLocations;
    }

    @Override
    public List<ProtectedChest> getGlobalLockedChests() {
        List<ProtectedChest> allLockedChests = new ArrayList<>();
        for (Clan clan : clansList.values()){
            for (Map.Entry<String, ProtectedChest> chests : clan.getProtectedChests().entrySet()){
                allLockedChests.add(chests.getValue());
            }
        }
        return allLockedChests;
    }

    @Override
    public Set<Map.Entry<UUID, Clan>> getClans() {
        return clansList.entrySet();
    }

    @Override
    public Set<UUID> getRawClansList() {
        return clansList.keySet();
    }

    @Override
    public Collection<Clan> getClanList() {
        return clansList.values();
    }

    private void fireClanDisbandEvent(Player player) {
        Clan clanByOwner = this.findClanByOwner(player);
        ClanDisbandEvent clanDisbandEvent = new ClanDisbandEvent(player, clanByOwner.getName());
        Bukkit.getPluginManager().callEvent(clanDisbandEvent);
    }

    private void fireOfflineClanDisbandEvent(OfflinePlayer offlinePlayer){
        Clan clanByOfflineOwner = this.findClanByOfflineOwner(offlinePlayer);
        ClanOfflineDisbandEvent clanOfflineDisbandEvent = new ClanOfflineDisbandEvent(offlinePlayer, clanByOfflineOwner.getName());
        Bukkit.getPluginManager().callEvent(clanOfflineDisbandEvent);
    }

    private void fireClanTransferOwnershipEvent(Player originalClanOwner, Player newClanOwner, Clan newClan){
        ClanTransferOwnershipEvent clanTransferOwnershipEvent = new ClanTransferOwnershipEvent(originalClanOwner, originalClanOwner, newClanOwner, newClan);
        Bukkit.getPluginManager().callEvent(clanTransferOwnershipEvent);
    }
}
