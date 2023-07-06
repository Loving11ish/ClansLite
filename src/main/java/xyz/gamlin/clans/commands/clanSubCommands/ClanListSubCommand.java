package xyz.gamlin.clans.commands.clanSubCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractUtils.StorageUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClanListSubCommand {

    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    public boolean clanListSubCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            Set<Map.Entry<UUID, Clan>> clans = storageUtils.getClans();
            StringBuilder clansString = new StringBuilder();
            if (clans.size() == 0) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("no-clans-to-list")));
            } else {
                clansString.append(ColorUtils.translateColorCodes(messagesConfig.getString("clans-list-header") + "\n"));
                clans.forEach((clan) ->
                        clansString.append(ColorUtils.translateColorCodes(clan.getValue().getClanFinalName() + "\n")));
                clansString.append(" ");
                clansString.append(ColorUtils.translateColorCodes(messagesConfig.getString("clans-list-footer")));
                player.sendMessage(clansString.toString());
            }
            return true;

        }
        return false;
    }
}
