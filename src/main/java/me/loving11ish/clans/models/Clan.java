package me.loving11ish.clans.models;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.*;

public class Clan {

    private final UUID clanId;
    private String clanName;
    private String clanPrefix;
    private List<ClanMember> clanMembers;
    private List<UUID> clanAllies;
    private List<UUID> clanEnemies;
    private boolean friendlyFire;
    private int clanPoints;
    private Location clanHome;
    private int maxAllowedProtectedChests;
    private List<ProtectedChest> protectedChests = new ArrayList<>();

    public Clan(UUID clanId, String clanName) {
        this.clanId = clanId;
        this.clanName = clanName;
        clanPrefix = this.clanName;
        clanMembers = new ArrayList<>();
        clanAllies = new ArrayList<>();
        clanEnemies = new ArrayList<>();
        friendlyFire = true;
        clanPoints = 0;
        clanHome = null;
        maxAllowedProtectedChests = 0;
    }

    public Clan(UUID clanId, String clanName, String clanPrefix, List<ClanMember> clanMembers, List<UUID> clanAllies,
                List<UUID> clanEnemies, boolean friendlyFire, int clanPoints, Location clanHome,
                int maxAllowedProtectedChests, List<ProtectedChest> protectedChests) {
        this.clanId = clanId;
        this.clanName = clanName;
        this.clanPrefix = clanPrefix;
        this.clanMembers = new ArrayList<>(clanMembers);
        this.clanAllies = new ArrayList<>(clanAllies);
        this.clanEnemies = new ArrayList<>(clanEnemies);
        this.friendlyFire = friendlyFire;
        this.clanPoints = clanPoints;
        this.clanHome = clanHome;
        this.maxAllowedProtectedChests = maxAllowedProtectedChests;
        this.protectedChests = protectedChests;
    }

    public UUID getId() {
        return clanId;
    }

    public String getName() {
        return clanName;
    }

    public void setName(String newClanFinalName) {
        clanName = newClanFinalName;
    }

    public String getPrefix() {
        return clanPrefix;
    }

    public void setPrefix(String newClanPrefix) {
        clanPrefix = newClanPrefix;
    }

    public ImmutableList<ClanMember> getMembers() {
        return ImmutableList.copyOf(clanMembers);
    }

    public void addMember(ClanMember clanMember) {
        clanMembers.add(clanMember);
    }

    public boolean removeMember(ClanMember clanMember) {
        return clanMembers.remove(clanMember);
    }

    public ImmutableList<UUID> getAllies() {
        return ImmutableList.copyOf(clanAllies);
    }

    public void addAlly(UUID ally) {
        clanAllies.add(ally);
    }

    public void removeAlly(UUID allyUUID) {
        clanAllies.remove(allyUUID);
    }

    public ImmutableList<UUID> getEnemies() {
        return ImmutableList.copyOf(clanEnemies);
    }

    public void addEnemy(UUID enemy) {
        clanEnemies.add(enemy);
    }

    public void removeEnemy(UUID enemyUUID) {
        clanEnemies.remove(enemyUUID);
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public int getPoints() {
        return clanPoints;
    }

    public void setPoints(int clanPoints) {
        this.clanPoints = clanPoints;
    }

    public Optional<Location> getHome() {
        return Optional.ofNullable(clanHome);
    }

    public int getMaxAllowedProtectedChests() {
        return maxAllowedProtectedChests;
    }

    public void setMaxAllowedProtectedChests(int maxAllowedProtectedChests) {
        this.maxAllowedProtectedChests = maxAllowedProtectedChests;
    }

    public ImmutableList<ProtectedChest> getProtectedChests() {
        return ImmutableList.copyOf(protectedChests);
    }

    public void addProtectedChest(ProtectedChest protectedChest) {
        protectedChests.add(protectedChest);
    }

    public void removeProtectedChest(ProtectedChest protectedChest) {
        protectedChests.remove(protectedChest);
    }
}
