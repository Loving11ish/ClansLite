package me.loving11ish.clans.commands.clanSubCommands;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;

public class ClanLeaveSubCommand {

    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String CLAN_PLACEHOLDER = "%CLAN%";

    public boolean clanLeaveSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (ClansStorageUtil.findClanByOwner(player) != null) {
                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-clan-owner"));
                return true;
            }

            Clan targetClan = ClansStorageUtil.findClanByPlayer(player);

            if (targetClan != null) {
                if (ClansStorageUtil.removeClanMember(targetClan, player)) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-leave-successful")
                            .replace(CLAN_PLACEHOLDER, targetClan.getClanFinalName()));
                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-leave-failed"));
                }
                return true;
            }
            return true;
        }
        return true;
    }
}
