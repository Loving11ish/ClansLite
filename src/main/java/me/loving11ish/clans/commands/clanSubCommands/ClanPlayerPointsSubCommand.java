package me.loving11ish.clans.commands.clanSubCommands;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.ClanPlayer;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.UserMapStorageUtil;

import java.util.*;

public class ClanPlayerPointsSubCommand {

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String POINT_PLACEHOLDER = "%POINTVALUE%";

    public boolean clanPlayerPointsSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!clansConfig.getBoolean("points.player-points.enabled")) {
                MessageUtils.sendPlayer(player, messagesConfig.getString("function-disabled"));
                return true;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("listall")) {
                    if (player.hasPermission("clanslite.points.listall") || player.hasPermission("clanslite.admin")
                            || player.hasPermission("clanslite.*") || player.isOp()) {
                        return listAllPoints(sender);
                    }
                }
            } else {
                ClanPlayer clanPlayer = UserMapStorageUtil.getClanPlayerByBukkitPlayer(player);
                if (clanPlayer != null) {
                    int playerPointValue = clanPlayer.getPointBalance();
                    MessageUtils.sendPlayer(player, messagesConfig.getString("player-points-list-command")
                            .replace(POINT_PLACEHOLDER, String.valueOf(playerPointValue)));
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("listall")) {
                    return listAllPoints(sender);
                }

            } else {
                MessageUtils.sendConsole(messagesConfig.getString("player-only-command"));
            }
        }
        return true;
    }

    private boolean listAllPoints(CommandSender sender) {
        HashMap<UUID, ClanPlayer> allUsersMap = new HashMap<>(UserMapStorageUtil.getUserMap());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ColorUtils.translateColorCodes(messagesConfig.getString("all-points-list-header")));

        for (Map.Entry<UUID, ClanPlayer> entry : allUsersMap.entrySet()) {
            String clanPlayerName = entry.getValue().getLastPlayerName();
            String clanPlayerPointBalance = String.valueOf(entry.getValue().getPointBalance());
            stringBuilder.append(ColorUtils.translateColorCodes(messagesConfig.getString("all-points-list-entry")
                    .replace("%PLAYER%", clanPlayerName).replace(POINT_PLACEHOLDER, clanPlayerPointBalance)));
        }

        stringBuilder.append(ColorUtils.translateColorCodes(messagesConfig.getString("all-points-list-footer")));
        sender.sendMessage(ColorUtils.translateColorCodes(stringBuilder.toString()));
        return true;
    }
}
