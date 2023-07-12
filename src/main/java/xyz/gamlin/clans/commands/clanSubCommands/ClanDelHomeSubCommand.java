package xyz.gamlin.clans.commands.clanSubCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.api.ClanHomeDeleteEvent;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractClasses.StorageUtils;

import java.util.logging.Logger;

public class ClanDelHomeSubCommand {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = Clans.getPlugin().getLogger();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    public boolean deleteClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            if (clansConfig.getBoolean("clan-home.enabled")){
                if (storageUtils.findClanByOwner(player) != null){
                    Clan clanByOwner = storageUtils.findClanByOwner(player);
                    if (storageUtils.isHomeSet(clanByOwner)){
                        fireClanHomeDeleteEvent(player, clanByOwner);
                        if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanHomeDeleteEvent"));
                        }
                        storageUtils.deleteHome(clanByOwner);
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-deleted-clan-home")));
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-no-home-set")));
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-must-be-owner")));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            }
            return true;

        }
        return false;
    }

    private static void fireClanHomeDeleteEvent(Player player, Clan clan) {
        ClanHomeDeleteEvent clanHomeDeleteEvent = new ClanHomeDeleteEvent(player, clan);
        Bukkit.getPluginManager().callEvent(clanHomeDeleteEvent);
    }
}
