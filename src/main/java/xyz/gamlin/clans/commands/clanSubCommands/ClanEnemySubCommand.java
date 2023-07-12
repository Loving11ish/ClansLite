package xyz.gamlin.clans.commands.clanSubCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.api.ClanEnemyAddEvent;
import xyz.gamlin.clans.api.ClanEnemyRemoveEvent;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractClasses.StorageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class ClanEnemySubCommand {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = Clans.getPlugin().getLogger();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    private static final String ENEMY_CLAN = "%ENEMYCLAN%";
    private static final String ENEMY_OWNER = "%ENEMYOWNER%";
    private static final String CLAN_OWNER = "%CLANOWNER%";

    public boolean clanEnemySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 2){
                if (args[1].equalsIgnoreCase("add")){
                    if (args[2].length() > 1){
                        if (storageUtils.isClanOwner(player)){
                            if (storageUtils.findClanByOwner(player) != null){
                                Clan clan = storageUtils.findClanByOwner(player);
                                Player enemyClanOwner = Bukkit.getPlayer(args[2]);
                                if (enemyClanOwner != null){
                                    if (storageUtils.findClanByOwner(enemyClanOwner) != null){
                                        if (storageUtils.findClanByOwner(player) != storageUtils.findClanByOwner(enemyClanOwner)){
                                            Clan enemyClan = storageUtils.findClanByOwner(enemyClanOwner);
                                            String enemyOwnerUUIDString = enemyClan.getClanOwner();
                                            if (storageUtils.findClanByOwner(player).getClanEnemies().size() >= clansConfig.getInt("max-clan-enemies")){
                                                int maxSize = clansConfig.getInt("max-clan-enemies");
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-enemy-max-amount-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                                                return true;
                                            }
                                            if (clan.getClanAllies().contains(enemyOwnerUUIDString)){
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-enemy-allied-clan")));
                                                return true;
                                            }
                                            if (clan.getClanEnemies().contains(enemyOwnerUUIDString)){
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-clan-already-your-enemy")));
                                                return true;
                                            }else {
                                                storageUtils.addClanEnemy(player, enemyClanOwner);
                                                fireClanEnemyAddEvent(player, clan, enemyClanOwner, enemyClan);
                                                if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanEnemyAddEvent"));
                                                }
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("added-clan-to-your-enemies").replace(ENEMY_CLAN, enemyClan.getClanFinalName())));
                                                String titleMain = ColorUtils.translateColorCodes(messagesConfig.getString("added-enemy-clan-to-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                                String titleAux = ColorUtils.translateColorCodes(messagesConfig.getString("added-enemy-clan-to-your-enemies-title-2").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                                player.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                ArrayList<String> playerClanMembers = storageUtils.findClanByOwner(player).getClanMembers();
                                                for (String playerClanMember : playerClanMembers){
                                                    if (playerClanMember != null){
                                                        UUID memberUUID = UUID.fromString(playerClanMember);
                                                        Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (playerClanPlayer != null){
                                                            playerClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }
                                            if (enemyClanOwner.isOnline()){
                                                enemyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-added-to-other-enemies").replace(CLAN_OWNER, player.getName())));
                                                String titleMainEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-added-to-other-enemies-title-1").replace(CLAN_OWNER, player.getName()));
                                                String titleAuxEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-added-to-other-enemies-title-2").replace(CLAN_OWNER, player.getName()));
                                                enemyClanOwner.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);
                                                ArrayList<String> enemyClanMembers = enemyClan.getClanMembers();
                                                for (String enemyClanMember : enemyClanMembers){
                                                    if (enemyClanMember != null) {
                                                        UUID memberUUID = UUID.fromString(enemyClanMember);
                                                        Player enemyClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (enemyClanPlayer != null) {
                                                            enemyClanPlayer.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }else {
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-add-clan-to-enemies").replace(ENEMY_OWNER, args[2])));
                                            }
                                        }else {
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-enemy-your-own-clan")));
                                        }
                                    }else {
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-enemy-player-not-clan-owner").replace(ENEMY_OWNER, args[2])));
                                    }
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("enemy-clan-add-owner-offline").replace(ENEMY_OWNER, args[2])));
                                }
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-must-be-owner")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-clan-enemy-command-usage")));
                    }
                    return true;
                }else if (args[1].equalsIgnoreCase("remove")){
                    if (args[2].length() > 1){
                        if (storageUtils.isClanOwner(player)){
                            if (storageUtils.findClanByOwner(player) != null){
                                Player enemyClanOwner = Bukkit.getPlayer(args[2]);
                                if (enemyClanOwner != null){
                                    if (storageUtils.findClanByOwner(enemyClanOwner) != null){
                                        Clan enemyClan = storageUtils.findClanByOwner(enemyClanOwner);
                                        List<String> enemyClans = storageUtils.findClanByOwner(player).getClanEnemies();
                                        UUID enemyClanOwnerUUID = enemyClanOwner.getUniqueId();
                                        String enemyClanOwnerString = enemyClanOwnerUUID.toString();
                                        if (enemyClans.contains(enemyClanOwnerString)){
                                            fireClanEnemyRemoveEvent(player, enemyClanOwner, enemyClan);
                                            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanEnemyRemoveEvent"));
                                            }
                                            storageUtils.removeClanEnemy(player, enemyClanOwner);
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("removed-clan-from-your-enemies").replace(ENEMY_CLAN, enemyClan.getClanFinalName())));
                                            String titleMain = ColorUtils.translateColorCodes(messagesConfig.getString("removed-enemy-clan-from-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                            String titleAux = ColorUtils.translateColorCodes(messagesConfig.getString("removed-enemy-clan-from-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                            player.sendTitle(titleMain, titleAux, 10, 70, 20);
                                            ArrayList<String> playerClanMembers = storageUtils.findClanByOwner(player).getClanMembers();
                                            for (String playerClanMember : playerClanMembers){
                                                if (playerClanMember != null){
                                                    UUID memberUUID = UUID.fromString(playerClanMember);
                                                    Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                                    if (playerClanPlayer != null){
                                                        playerClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                    }
                                                }
                                            }
                                            if (enemyClanOwner.isOnline()){
                                                enemyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-removed-from-other-enemies").replace(ENEMY_OWNER, player.getName())));
                                                String titleMainEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-removed-from-other-enemies-title-1").replace(CLAN_OWNER, player.getName()));
                                                String titleAuxEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-removed-from-other-enemies-title-2").replace(CLAN_OWNER, player.getName()));
                                                enemyClanOwner.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);
                                                ArrayList<String> enemyClanMembers = enemyClan.getClanMembers();
                                                for (String enemyClanMember : enemyClanMembers){
                                                    if (enemyClanMember != null) {
                                                        UUID memberUUID = UUID.fromString(enemyClanMember);
                                                        Player enemyClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (enemyClanPlayer != null) {
                                                            enemyClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }
                                        }else {
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-remove-clan-from-enemies").replace(ENEMY_OWNER, args[2])));
                                        }
                                    }else {
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-enemy-player-not-clan-owner").replace(ENEMY_OWNER, args[2])));
                                    }
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("enemy-clan-remove-owner-offline").replace(ENEMY_OWNER, args[2])));
                                }
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-must-be-owner")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-clan-enemy-command-usage")));
                    }
                }
                return true;
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-clan-enemy-command-usage")));
            }

        }
        return false;
    }

    private void fireClanEnemyRemoveEvent(Player player, Player enemyClanOwner, Clan enemyClan) {
        ClanEnemyRemoveEvent clanEnemyRemoveEvent = new ClanEnemyRemoveEvent(player, storageUtils.findClanByPlayer(player), enemyClan, enemyClanOwner);
        Bukkit.getPluginManager().callEvent(clanEnemyRemoveEvent);
    }
    private void fireClanEnemyAddEvent(Player player, Clan clan, Player enemyClanOwner, Clan enemyClan) {
        ClanEnemyAddEvent clanEnemyAddEvent = new ClanEnemyAddEvent(player, clan, enemyClan, enemyClanOwner);
        Bukkit.getPluginManager().callEvent(clanEnemyAddEvent);
    }
}
