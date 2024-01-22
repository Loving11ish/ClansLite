package me.loving11ish.clans.commands;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.commands.clanSubCommands.*;
import me.loving11ish.clans.menusystem.paginatedMenu.ClanListGUI;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ClanCommand implements CommandExecutor {

    public static WrappedTask bannedTaskUpdateTask;

    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static List<String> bannedTags;

    public static void updateBannedTagsList() {
        FoliaLib foliaLib = Clans.getFoliaLib();
        bannedTaskUpdateTask = foliaLib.getImpl().runLaterAsync(() ->
                bannedTags = Clans.getPlugin().getConfig().getStringList("clan-tags.disallowed-tags"), 1L, TimeUnit.SECONDS);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                if (Clans.getPlugin().isGUIEnabled()) {
                    new ClanListGUI(Clans.getPlayerMenuUtility(player)).open();
                    return true;
                }

                MessageUtils.sendPlayer(player, usageMessage());
                return true;

            } else {
                switch (args[0]) {

                    case "create":
                        return new ClanCreateSubCommand().createClanSubCommand(sender, args, bannedTags);

                    case "disband":
                        return new ClanDisbandSubCommand().disbandClanSubCommand(sender);

                    case "invite":
                        return new ClanInviteSubCommand().clanInviteSubCommand(sender, args);

                    case "prefix":
                        return new ClanPrefixSubCommand().clanPrefixSubCommand(sender, args, bannedTags);

                    case "transfer":
                        return new ClanTransferOwnerSubCommand().transferClanOwnerSubCommand(sender, args);

                    case "list":
                        return new ClanListSubCommand().clanListSubCommand(sender);

                    case "join":
                        return new ClanJoinSubCommand().clanJoinSubCommand(sender);

                    case "kick":
                        return new ClanKickSubCommand().clanKickSubCommand(sender, args);

                    case "info":
                        return new ClanInfoSubCommand().clanInfoSubCommand(sender);

                    case "leave":
                        return new ClanLeaveSubCommand().clanLeaveSubCommand(sender);

                    case "ally":
                        return new ClanAllySubCommand().clanAllySubCommand(sender, args);

                    case "enemy":
                        return new ClanEnemySubCommand().clanEnemySubCommand(sender, args);

                    case "pvp":
                        return new ClanPvpSubCommand().clanPvpSubCommand(sender);

                    case "sethome":
                        return new ClanSetHomeSubCommand().setClanHomeSubCommand(sender);

                    case "delhome":
                        return new ClanDelHomeSubCommand().deleteClanHomeSubCommand(sender);

                    case "home":
                        return new ClanHomeSubCommand().tpClanHomeSubCommand(sender);

                    case "points":
                        return new ClanPointSubCommand().clanPointSubCommand(sender, args);

                    case "playerpoints":
                        return new ClanPlayerPointsSubCommand().clanPlayerPointsSubCommand(sender, args);

                    default:
                        MessageUtils.sendPlayer(player, usageMessage());

                }
            }
            return true;
        }

//----------------------------------------------------------------------------------------------------------------------
        if (sender instanceof ConsoleCommandSender) {
            MessageUtils.sendConsole(messagesConfig.getString("player-only-command"));
        }

        return true;
    }

    private String usageMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> configStringList = messagesConfig.getStringList("clan-command-incorrect-usage");
        for (String string : configStringList) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
