package me.loving11ish.clans.commands.clanSubCommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;

import java.util.*;

public class ClanInfoSubCommand {

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String CLAN_PLACEHOLDER = "%CLAN%";
    private static final String OWNER = "%OWNER%";
    private static final String CLAN_MEMBER = "%MEMBER%";
    private static final String ALLY_CLAN = "%ALLYCLAN%";
    private static final String ENEMY_CLAN = "%ENEMYCLAN%";
    private static final String POINTS_PLACEHOLDER = "%POINTS%";
    private static final String CHEST_PLACEHOLDER = "%CHESTS%";
    private static final String TOTAL_CHEST_ALLOWED = "%MAXALLOWED%";

    public boolean clanInfoSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Clan clanByOwner = ClansStorageUtil.findClanByOwner(player);
            Clan clanByPlayer = ClansStorageUtil.findClanByPlayer(player);
            if (clanByOwner != null) {
                ArrayList<String> clanMembers = clanByOwner.getMembers();
                ArrayList<String> clanAllies = clanByOwner.getAllies();
                ArrayList<String> clanEnemies = clanByOwner.getClanEnemies();
                StringBuilder clanInfo = new StringBuilder(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-header"))
                        .replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(clanByOwner.getName()))
                        .replace("%CLANPREFIX%", ColorUtils.translateColorCodes(clanByOwner.getPrefix())));
                UUID clanOwnerUUID = UUID.fromString(clanByOwner.getId());
                Player clanOwner = Bukkit.getPlayer(clanOwnerUUID);
                if (clanOwner != null) {
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-owner-online")).replace(OWNER, clanOwner.getName()));
                }else {
                    UUID uuid = UUID.fromString(clanByOwner.getId());
                    String offlineOwner = Bukkit.getOfflinePlayer(uuid).getName();
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-owner-offline")).replace(OWNER, offlineOwner));
                }
                if (clanMembers.size() > 0) {
                    int clanMembersSize = clanMembers.size();
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-members-header")
                            .replace("%NUMBER%", ColorUtils.translateColorCodes(String.valueOf(clanMembersSize)))));
                    for (String clanMember : clanMembers) {
                        if (clanMember != null) {
                            UUID memberUUID = UUID.fromString(clanMember);
                            Player clanPlayer = Bukkit.getPlayer(memberUUID);
                            if (clanPlayer != null) {
                                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-members-online") + "\n").replace(CLAN_MEMBER, clanPlayer.getName()));
                            } else {
                                UUID uuid = UUID.fromString(clanMember);
                                String offlinePlayer = Bukkit.getOfflinePlayer(uuid).getName();
                                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-members-offline") + "\n").replace(CLAN_MEMBER, offlinePlayer));
                            }
                        }

                    }
                }
                if (clanAllies.size() > 0){
                    clanInfo.append(" ");
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-allies-header")));
                    for (String clanAlly : clanAllies){
                        if (clanAlly != null){
                            Player allyOwner = Bukkit.getPlayer(clanAlly);
                            if (allyOwner != null){
                                Clan allyClan = ClansStorageUtil.findClanByOwner(allyOwner);
                                if (allyClan != null){
                                    String clanAllyName = allyClan.getName();
                                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-ally-members").replace(ALLY_CLAN, clanAllyName)));
                                }
                            }else {
                                UUID uuid = UUID.fromString(clanAlly);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Clan offlineAllyClan = ClansStorageUtil.findClanByOfflineOwner(offlineOwnerPlayer);
                                if (offlineAllyClan != null){
                                    String offlineAllyName = offlineAllyClan.getName();
                                    if (offlineAllyName != null){
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-ally-members").replace(ALLY_CLAN, offlineAllyName)));
                                    }else {
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-ally-members-not-found")));
                                    }
                                }
                            }
                        }
                    }
                }
                if (clanEnemies.size() > 0){
                    clanInfo.append(" ");
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-enemies-header")));
                    for (String clanEnemy : clanEnemies){
                        if (clanEnemy != null){
                            Player enemyOwner = Bukkit.getPlayer(clanEnemy);
                            if (enemyOwner != null){
                                Clan enemyClan = ClansStorageUtil.findClanByOwner(enemyOwner);
                                if (enemyClan != null){
                                    String clanEnemyName = enemyClan.getName();
                                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-enemy-members").replace(ENEMY_CLAN, clanEnemyName)));
                                }
                            }else {
                                UUID uuid = UUID.fromString(clanEnemy);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Clan offlineEnemyClan = ClansStorageUtil.findClanByOfflineOwner(offlineOwnerPlayer);
                                if (offlineEnemyClan != null){
                                    String offlineEnemyName = offlineEnemyClan.getName();
                                    if (offlineEnemyName != null){
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-enemy-members").replace(ENEMY_CLAN, offlineEnemyName)));
                                    }else {
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-enemy-members-not-found")));
                                    }
                                }
                            }
                        }
                    }
                }
                clanInfo.append(" ");
                if (clanByOwner.isFriendlyFire()){
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-pvp-status-enabled")));
                }else {
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-pvp-status-disabled")));
                }
                if (ClansStorageUtil.isHomeSet(clanByOwner)){
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-home-set-true")));
                }else {
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-home-set-false")));
                }
                clanInfo.append(" ");
                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-points-value").replace(POINTS_PLACEHOLDER, String.valueOf(clanByOwner.getPoints()))));
                clanInfo.append(" ");
                if (clansConfig.getBoolean("protections.chests.enabled")){
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-chest-amount").replace(CHEST_PLACEHOLDER, String.valueOf(clanByOwner.getProtectedChests().size()))));
                    clanInfo.append(" ");
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-total-allowed-chests").replace(TOTAL_CHEST_ALLOWED, String.valueOf(clanByOwner.getMaxAllowedProtectedChests()))));
                    clanInfo.append(" ");
                }
                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-footer")));
                player.sendMessage(clanInfo.toString());

            }else if (clanByPlayer != null){
                ArrayList<String> clanMembers = clanByPlayer.getMembers();
                ArrayList<String> clanAllies = clanByPlayer.getAllies();
                ArrayList<String> clanEnemies = clanByPlayer.getClanEnemies();
                StringBuilder clanInfo = new StringBuilder(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-header"))
                        .replace(CLAN_PLACEHOLDER, ColorUtils.translateColorCodes(clanByPlayer.getName()))
                        .replace("%CLANPREFIX%", ColorUtils.translateColorCodes(clanByPlayer.getPrefix())));
                UUID clanOwnerUUID = UUID.fromString(clanByPlayer.getId());
                Player clanOwner = Bukkit.getPlayer(clanOwnerUUID);
                if (clanOwner != null) {
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-owner-online")).replace(OWNER, clanOwner.getName()));
                } else {
                    UUID uuid = UUID.fromString(clanByPlayer.getId());
                    String offlineOwner = Bukkit.getOfflinePlayer(uuid).getName();
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-owner-offline")).replace(OWNER, offlineOwner));
                }
                if (clanMembers.size() > 0) {
                    int clanMembersSize = clanMembers.size();
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-members-header")
                            .replace("%NUMBER%", ColorUtils.translateColorCodes(String.valueOf(clanMembersSize)))));
                    for (String clanMember : clanMembers) {
                        if (clanMember != null) {
                            UUID memberUUID = UUID.fromString(clanMember);
                            Player clanPlayer = Bukkit.getPlayer(memberUUID);
                            if (clanPlayer != null) {
                                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-members-online") + "\n").replace(CLAN_MEMBER, clanPlayer.getName()));
                            } else {
                                UUID uuid = UUID.fromString(clanMember);
                                String offlinePlayer = Bukkit.getOfflinePlayer(uuid).getName();
                                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-members-offline") + "\n").replace(CLAN_MEMBER, offlinePlayer));
                            }
                        }

                    }
                }
                if (clanAllies.size() > 0){
                    clanInfo.append(" ");
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-allies-header")));
                    for (String clanAlly : clanAllies){
                        if (clanAlly != null){
                            Player allyOwner = Bukkit.getPlayer(clanAlly);
                            if (allyOwner != null){
                                Clan allyClan = ClansStorageUtil.findClanByOwner(allyOwner);
                                if (allyClan != null){
                                    String clanAllyName = allyClan.getName();
                                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-ally-members").replace(ALLY_CLAN, clanAllyName)));
                                }
                            }else {
                                UUID uuid = UUID.fromString(clanAlly);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Clan offlineAllyClan = ClansStorageUtil.findClanByOfflineOwner(offlineOwnerPlayer);
                                if (offlineAllyClan != null){
                                    String offlineAllyName = offlineAllyClan.getName();
                                    if (offlineAllyName != null){
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-ally-members").replace(ALLY_CLAN, offlineAllyName)));
                                    }else {
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-ally-members-not-found")));
                                    }
                                }
                            }
                        }
                    }
                }
                if (clanEnemies.size() > 0){
                    clanInfo.append(" ");
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-enemies-header")));
                    for (String clanEnemy : clanEnemies){
                        if (clanEnemy != null){
                            Player enemyOwner = Bukkit.getPlayer(clanEnemy);
                            if (enemyOwner != null){
                                Clan enemyClan = ClansStorageUtil.findClanByOwner(enemyOwner);
                                if (enemyClan != null){
                                    String clanEnemyName = enemyClan.getName();
                                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-enemy-members").replace(ENEMY_CLAN, clanEnemyName)));
                                }
                            }else {
                                UUID uuid = UUID.fromString(clanEnemy);
                                OfflinePlayer offlineOwnerPlayer = Bukkit.getOfflinePlayer(uuid);
                                Clan offlineEnemyClan = ClansStorageUtil.findClanByOfflineOwner(offlineOwnerPlayer);
                                if (offlineEnemyClan != null){
                                    String offlineEnemyName = offlineEnemyClan.getName();
                                    if (offlineEnemyName != null){
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-enemy-members").replace(ENEMY_CLAN, offlineEnemyName)));
                                    }else {
                                        clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-enemy-members-not-found")));
                                    }
                                }
                            }
                        }
                    }
                }
                clanInfo.append(" ");
                if (clanByPlayer.isFriendlyFire()){
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-pvp-status-enabled")));
                }else {
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-pvp-status-disabled")));
                }
                if (ClansStorageUtil.isHomeSet(clanByPlayer)){
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-home-set-true")));
                }else {
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-home-set-false")));
                }
                clanInfo.append(" ");
                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-points-value").replace(POINTS_PLACEHOLDER, String.valueOf(clanByPlayer.getPoints()))));
                clanInfo.append(" ");
                if (clansConfig.getBoolean("protections.chests.enabled")){
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-chest-amount").replace(CHEST_PLACEHOLDER, String.valueOf(clanByPlayer.getProtectedChests().size()))));
                    clanInfo.append(" ");
                    clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-total-allowed-chests").replace(TOTAL_CHEST_ALLOWED, String.valueOf(clanByPlayer.getMaxAllowedProtectedChests()))));
                    clanInfo.append(" ");
                }
                clanInfo.append(ColorUtils.translateColorCodes(messagesConfig.getString("clan-info-footer")));
                player.sendMessage(clanInfo.toString());
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("not-in-clan")));
            }
            return true;

        }
        return false;
    }
}
