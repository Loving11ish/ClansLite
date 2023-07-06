package xyz.gamlin.clans.commands.clanSubCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.api.ClanAllyAddEvent;
import xyz.gamlin.clans.api.ClanAllyRemoveEvent;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractUtils.StorageUtils;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class ClanAllySubCommand {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = Clans.getPlugin().getLogger();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    private static final String ALLY_CLAN = "%ALLYCLAN%";
    private static final String ALLY_OWNER = "%ALLYOWNER%";
    private static final String CLAN_OWNER = "%CLANOWNER%";

    public boolean clanAllySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 2){
                if (args[1].equalsIgnoreCase("add")){
                    if (args[2].length() > 1){
                        if (storageUtils.isClanOwner(player)){
                            if (storageUtils.findClanByOwner(player) != null) {
                                Clan clan = storageUtils.findClanByOwner(player);
                                Player allyClanOwner = Bukkit.getPlayer(args[2]);
                                if (allyClanOwner != null){
                                    if (storageUtils.findClanByOwner(allyClanOwner) != null){
                                        if (storageUtils.findClanByOwner(player) != storageUtils.findClanByOwner(allyClanOwner)){
                                            Clan allyClan = storageUtils.findClanByOwner(allyClanOwner);
                                            String allyOwnerUUIDString = allyClan.getClanOwner();
                                            if (storageUtils.findClanByOwner(player).getClanAllies().size() >= clansConfig.getInt("max-clan-allies")){
                                                int maxSize = clansConfig.getInt("max-clan-allies");
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-ally-max-amount-reached")).replace("%LIMIT%", String.valueOf(maxSize)));
                                                return true;
                                            }
                                            if (clan.getClanEnemies().contains(allyOwnerUUIDString)){
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-ally-enemy-clan")));
                                                return true;
                                            }
                                            if (clan.getClanAllies().contains(allyOwnerUUIDString)){
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-clan-already-your-ally")));
                                                return true;
                                            }else {
                                                storageUtils.addClanAlly(player, allyClanOwner);
                                                fireClanAllyAddEvent(player, clan, allyClanOwner, allyClan);
                                                if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanAllyAddEvent"));
                                                }
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("added-clan-to-your-allies").replace(ALLY_CLAN, allyClan.getClanFinalName())));
                                            }
                                            if (allyClanOwner.isOnline()){
                                                allyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-added-to-other-allies").replace(CLAN_OWNER, player.getName())));
                                            }else {
                                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-add-clan-to-allies").replace(ALLY_OWNER, args[2])));
                                            }
                                        }else {
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-cannot-ally-your-own-clan")));
                                        }
                                    }else {
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-player-not-clan-owner").replace(ALLY_OWNER, args[2])));
                                    }
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("ally-clan-add-owner-offline").replace(ALLY_OWNER, args[2])));
                                }
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-must-be-owner")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-clan-ally-command-usage")));
                    }
                    return true;
                }else if (args[1].equalsIgnoreCase("remove")){
                    if (args[2].length() > 1){
                        if (storageUtils.isClanOwner(player)){
                            if (storageUtils.findClanByOwner(player) != null){
                                Player allyClanOwner = Bukkit.getPlayer(args[2]);
                                if (allyClanOwner != null){
                                    if (storageUtils.findClanByOwner(allyClanOwner) != null){
                                        Clan allyClan = storageUtils.findClanByOwner(allyClanOwner);
                                        List<String> alliedClans = storageUtils.findClanByOwner(player).getClanAllies();
                                        UUID allyClanOwnerUUID = allyClanOwner.getUniqueId();
                                        String allyClanOwnerString = allyClanOwnerUUID.toString();
                                        if (alliedClans.contains(allyClanOwnerString)){
                                            fireClanAllyRemoveEvent(player, allyClanOwner, allyClan);
                                            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanAllyRemoveEvent"));
                                            }
                                            storageUtils.removeClanAlly(player, allyClanOwner);
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("removed-clan-from-your-allies").replace(ALLY_CLAN, allyClan.getClanFinalName())));
                                            if (allyClanOwner.isOnline()){
                                                allyClanOwner.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-removed-from-other-allies").replace(CLAN_OWNER, player.getName())));
                                            }
                                        }else {
                                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-to-remove-clan-from-allies").replace(ALLY_OWNER, args[2])));
                                        }
                                    }else {
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-player-not-clan-owner").replace(ALLY_OWNER, args[2])));
                                    }
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("ally-clan-remove-owner-offline").replace(ALLY_OWNER, args[2])));
                                }
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-must-be-owner")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-clan-ally-command-usage")));
                    }
                }
                return true;
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-clan-ally-command-usage")));
            }
            return true;

        }
        return false;
    }
    private void fireClanAllyRemoveEvent(Player player, Player allyClanOwner, Clan allyClan) {
        ClanAllyRemoveEvent clanAllyRemoveEvent = new ClanAllyRemoveEvent(player, storageUtils.findClanByOwner(player), allyClan, allyClanOwner);
        Bukkit.getPluginManager().callEvent(clanAllyRemoveEvent);
    }

    private void fireClanAllyAddEvent(Player player, Clan clan, Player allyClanOwner, Clan allyClan) {
        ClanAllyAddEvent clanAllyAddEvent = new ClanAllyAddEvent(player, clan, allyClan, allyClanOwner);
        Bukkit.getPluginManager().callEvent(clanAllyAddEvent);
    }
}
