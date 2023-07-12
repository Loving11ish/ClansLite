package xyz.gamlin.clans.commands.clanSubCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractClasses.StorageUtils;

public class ClanPvpSubCommand {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    public boolean clanPvpSubCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            if (clansConfig.getBoolean("protections.pvp.pvp-command-enabled")){
                if (storageUtils.isClanOwner(player)){
                    if (storageUtils.findClanByOwner(player) != null){
                        Clan clan = storageUtils.findClanByOwner(player);
                        if (clan.isFriendlyFireAllowed()){
                            clan.setFriendlyFireAllowed(false);
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("disabled-friendly-fire")));
                        }else {
                            clan.setFriendlyFireAllowed(true);
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("enabled-friendly-fire")));
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-not-in-clan")));
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
}
