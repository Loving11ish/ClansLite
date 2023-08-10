package me.loving11ish.clans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.abstractClasses.UsermapUtils;

public class ClanChatSpyCommand implements CommandExecutor {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private UsermapUtils usermapUtils = Clans.getPlugin().usermapUtils;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")){
                if (player.hasPermission("clanslite.chat.spy")||player.hasPermission("clanslite.*")
                        ||player.hasPermission("clanslite.admin")||player.isOp()){
                    if (usermapUtils.toggleChatSpy(player)){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chatspy-toggle-on")));
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chatspy-toggle-off")));
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("no-permission")));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            }
            return true;
        }
        return true;
    }
}
