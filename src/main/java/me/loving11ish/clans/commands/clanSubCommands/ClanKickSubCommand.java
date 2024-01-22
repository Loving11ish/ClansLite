package me.loving11ish.clans.commands.clanSubCommands;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.UserMapStorageUtil;

public class ClanKickSubCommand {

    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String CLAN_PLACEHOLDER = "%CLAN%";
    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    public boolean clanKickSubCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 2) {
                if (args[1].length() > 1) {

                    Clan targetClan = ClansStorageUtil.findClanByOwner(player);
                    if (ClansStorageUtil.findClanByOwner(player) != null) {
                        Player playerToKick = Bukkit.getPlayer(args[1]);
                        OfflinePlayer offlinePlayerToKick = UserMapStorageUtil.getBukkitOfflinePlayerByName(args[1]);

                        if (playerToKick != null) {
                            if (!player.getName().equalsIgnoreCase(args[1])) {
                                Clan playerClan = ClansStorageUtil.findClanByPlayer(playerToKick);

                                if (targetClan.equals(playerClan)) {
                                    if (ClansStorageUtil.removeClanMember(targetClan, playerToKick)) {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-member-kick-successful")
                                                .replace(PLAYER_TO_KICK, args[1]));

                                        if (playerToKick.isOnline()) {
                                            MessageUtils.sendPlayer(playerToKick, messagesConfig.getString("clan-kicked-player-message")
                                                    .replace(CLAN_PLACEHOLDER, targetClan.getClanFinalName()));
                                            return true;
                                        }
                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("targeted-player-is-not-in-your-clan")
                                                .replace(PLAYER_TO_KICK, args[1]));
                                        return true;
                                    }

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("targeted-player-is-not-in-your-clan")
                                            .replace(PLAYER_TO_KICK, args[1]));
                                    return true;
                                }

                            } else {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-cannot-kick-yourself"));
                                return true;
                            }

                        } else if (offlinePlayerToKick != null) {
                            if (!player.getName().equalsIgnoreCase(args[1])) {
                                Clan offlinePlayerClan = ClansStorageUtil.findClanByOfflinePlayer(offlinePlayerToKick);

                                if (targetClan.equals(offlinePlayerClan)) {
                                    if (ClansStorageUtil.removeOfflineClanMember(targetClan, offlinePlayerToKick)) {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-member-kick-successful")
                                                .replace(PLAYER_TO_KICK, args[1]));
                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("targeted-player-is-not-in-your-clan")
                                                .replace(PLAYER_TO_KICK, args[1]));
                                    }
                                    return true;

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("targeted-player-is-not-in-your-clan")
                                            .replace(PLAYER_TO_KICK, args[1]));
                                    return true;
                                }

                            } else {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-cannot-kick-yourself"));
                                return true;
                            }

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("could-not-find-specified-player")
                                    .replace(PLAYER_TO_KICK, args[1]));
                            return true;
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("must-be-owner-to-kick"));
                        return true;
                    }

                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-kick-command-usage"));
                    return true;
                }
            }
            return true;

        }
        return false;
    }
}
