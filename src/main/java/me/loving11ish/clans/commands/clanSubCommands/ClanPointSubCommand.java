package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanPointsAddedEvent;
import me.loving11ish.clans.api.events.AsyncClanPointsRemovedEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.models.ClanPlayer;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.UserMapStorageUtil;

public class ClanPointSubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String CLAN_PLACEHOLDER = "%CLAN%";
    private static final String POINT_PLACEHOLDER = "%POINTS%";

    public boolean clanPointSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (clansConfig.getBoolean("points.player-points.enabled")) {
                if (args.length > 2) {
                    if (args[1].equalsIgnoreCase("deposit")) {
                        if (args[2] != null) {
                            int depositValue = Integer.parseInt(args[2]);

                            if (depositValue != 0) {
                                Clan clan = ClansStorageUtil.findClanByPlayer(player);
                                ClanPlayer clanPlayer = UserMapStorageUtil.getClanPlayerByBukkitPlayer(player);
                                int previousClanPlayerPointValue = clanPlayer.getPointBalance();

                                if (clan != null) {
                                    int previousClanPointValue = clan.getClanPoints();

                                    if (UserMapStorageUtil.withdrawPoints(player, depositValue)) {
                                        ClansStorageUtil.addPoints(clan, depositValue);
                                        int newClanPlayerPointValue = clanPlayer.getPointBalance();
                                        int newClanPointValue = clan.getClanPoints();

                                        foliaLib.getImpl().runAsync((task) -> {
                                            fireAsyncClanPointsAddedEvent(player, clan, clanPlayer, previousClanPlayerPointValue, newClanPlayerPointValue, depositValue, previousClanPointValue, newClanPointValue);
                                            MessageUtils.sendDebugConsole("Fired AsyncClanPointsAddedEvent");
                                        });

                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-deposit-points-success")
                                                .replace(CLAN_PLACEHOLDER, clan.getClanFinalName()).replace(POINT_PLACEHOLDER, String.valueOf(depositValue)));

                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-deposit-points-failed")
                                                .replace(CLAN_PLACEHOLDER, clan.getClanFinalName()).replace(POINT_PLACEHOLDER, String.valueOf(depositValue)));
                                    }

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-points-failed-not-in-clan"));
                                }

                            } else {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("clan-deposit-points-invalid-point-amount"));
                            }

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("clan-deposit-points-incorrect-command"));
                        }
                        return true;

                    } else if (args[1].equalsIgnoreCase("withdraw")) {
                        if (args[2] != null) {
                            int withdrawValue = Integer.parseInt(args[2]);

                            if (withdrawValue != 0) {
                                Clan clan = ClansStorageUtil.findClanByPlayer(player);
                                ClanPlayer clanPlayer = UserMapStorageUtil.getClanPlayerByBukkitPlayer(player);
                                int previousClanPlayerPointValue = clanPlayer.getPointBalance();

                                if (clan != null) {
                                    int previousClanPointValue = clan.getClanPoints();

                                    if (ClansStorageUtil.withdrawPoints(clan, withdrawValue)) {
                                        UserMapStorageUtil.addPointsToOnlinePlayer(player, withdrawValue);
                                        int newClanPlayerPointValue = clanPlayer.getPointBalance();
                                        int newClanPointValue = clan.getClanPoints();

                                        foliaLib.getImpl().runAsync((task) -> {
                                            fireAsyncClanPointsRemovedEvent(player, clan, clanPlayer, previousClanPlayerPointValue, newClanPlayerPointValue, withdrawValue, previousClanPointValue, newClanPointValue);
                                            MessageUtils.sendDebugConsole("Fired AsyncClanPointsRemovedEvent");
                                        });

                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-withdraw-points-success")
                                                .replace(CLAN_PLACEHOLDER, clan.getClanFinalName()).replace(POINT_PLACEHOLDER, String.valueOf(withdrawValue)));

                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-withdraw-points-failed")
                                                .replace(CLAN_PLACEHOLDER, clan.getClanFinalName()).replace(POINT_PLACEHOLDER, String.valueOf(withdrawValue)));
                                    }

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-points-failed-not-in-clan"));
                                }

                            } else {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("clan-withdraw-points-invalid-point-amount"));
                            }

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("clan-withdraw-points-incorrect-command"));
                        }
                        return true;
                    }
                }
            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("function-disabled"));
            }
        }
        return true;
    }

    private void fireAsyncClanPointsAddedEvent(Player createdBy, Clan playerClan, ClanPlayer clanPlayer, int previousClanPlayerPointBalance, int newClanPlayerPointBalance, int depositPointValue, int previousClanPointBalance, int newClanPointBalance) {
        AsyncClanPointsAddedEvent asyncClanPointsAddedEvent = new AsyncClanPointsAddedEvent(true, createdBy, playerClan, clanPlayer, previousClanPlayerPointBalance, newClanPlayerPointBalance, depositPointValue, previousClanPointBalance, newClanPointBalance);
        Bukkit.getPluginManager().callEvent(asyncClanPointsAddedEvent);
    }

    private void fireAsyncClanPointsRemovedEvent(Player createdBy, Clan playerClan, ClanPlayer clanPlayer, int previousClanPlayerPointBalance, int newClanPlayerPointBalance, int withdrawPointValue, int previousClanPointBalance, int newClanPointBalance) {
        AsyncClanPointsRemovedEvent asyncClanPointsRemovedEvent = new AsyncClanPointsRemovedEvent(true, createdBy, playerClan, clanPlayer, previousClanPlayerPointBalance, newClanPlayerPointBalance, withdrawPointValue, previousClanPointBalance, newClanPointBalance);
        Bukkit.getPluginManager().callEvent(asyncClanPointsRemovedEvent);
    }

}
