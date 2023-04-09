package xyz.gamlin.clans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.ClansStorageUtil;
import xyz.gamlin.clans.utils.ColorUtils;

public class LeaveClanCommand implements CommandExecutor {
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();
    private static final String CLAN_PLACEHOLDER = "%CLAN%";
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (ClansStorageUtil.findClanByOwner(player) != null) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-clan-owner")));
                return true;
            }
            Clan targetClan = ClansStorageUtil.findClanByPlayer(player);
            if (targetClan != null) {
                if (targetClan.removeClanMember(player.getUniqueId().toString())) {
                    String leaveMessage = ColorUtils.translateColorCodes(messagesConfig.getString("clan-leave-successful")).replace(CLAN_PLACEHOLDER, targetClan.getClanFinalName());
                    player.sendMessage(leaveMessage);
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-leave-failed")));
                }
            }
            return true;

        }
        return false;
    }
}
