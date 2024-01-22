package me.loving11ish.clans.commands;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.UserMapStorageUtil;

public class ClanChatSpyCommand implements CommandExecutor {

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                if (player.hasPermission("clanslite.chat.spy") || player.hasPermission("clanslite.*")
                        || player.hasPermission("clanslite.admin") || player.isOp()) {

                    if (UserMapStorageUtil.toggleChatSpy(player)) {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("chatspy-toggle-on"));
                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("chatspy-toggle-off"));
                    }

                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("no-permission"));
                }

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("function-disabled"));
            }
            return true;
        }
        return true;
    }
}
