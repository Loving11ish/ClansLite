package xyz.gamlin.clans.commands.clanSubCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractUtils.StorageUtils;

public class ClanLeaveSubCommand {

    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    private static final String CLAN_PLACEHOLDER = "%CLAN%";

    public boolean clanLeaveSubCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            if (storageUtils.findClanByOwner(player) != null) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-clan-owner")));
                return true;
            }
            Clan targetClan = storageUtils.findClanByPlayer(player);
            if (targetClan != null) {
                if (storageUtils.removeClanMember(targetClan, player)){
                    String leaveMessage = ColorUtils.translateColorCodes(messagesConfig.getString("clan-leave-successful")).replace(CLAN_PLACEHOLDER, targetClan.getClanFinalName());
                    player.sendMessage(leaveMessage);
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-leave-failed")));
                }
            }
            return true;
        }
        return true;
    }
}
