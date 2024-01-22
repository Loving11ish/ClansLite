package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanAllyAddEvent;
import me.loving11ish.clans.api.events.AsyncClanAllyRemoveEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;

import java.util.List;
import java.util.UUID;

public class ClanAllySubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String ALLY_CLAN = "%ALLYCLAN%";
    private static final String ALLY_OWNER = "%ALLYOWNER%";
    private static final String CLAN_OWNER = "%CLANOWNER%";

    public boolean clanAllySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 2) {
                if (args[1].equalsIgnoreCase("add")) {
                    if (args[2].length() > 1) {
                        if (ClansStorageUtil.isClanOwner(player)) {
                            if (ClansStorageUtil.findClanByOwner(player) != null) {

                                Clan clan = ClansStorageUtil.findClanByOwner(player);
                                Player allyClanOwner = Bukkit.getPlayer(args[2]);

                                if (allyClanOwner != null) {
                                    if (ClansStorageUtil.findClanByOwner(allyClanOwner) != null) {
                                        if (ClansStorageUtil.findClanByOwner(player) != ClansStorageUtil.findClanByOwner(allyClanOwner)) {

                                            Clan allyClan = ClansStorageUtil.findClanByOwner(allyClanOwner);
                                            String allyOwnerUUIDString = allyClan.getClanOwner();

                                            if (ClansStorageUtil.findClanByOwner(player).getClanAllies().size() >= clansConfig.getInt("max-clan-allies")) {
                                                int maxSize = clansConfig.getInt("max-clan-allies");
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("clan-ally-max-amount-reached")
                                                        .replace("%LIMIT%", String.valueOf(maxSize)));
                                                return true;
                                            }

                                            if (clan.getClanEnemies().contains(allyOwnerUUIDString)) {
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-cannot-ally-enemy-clan"));
                                                return true;
                                            }

                                            if (clan.getClanAllies().contains(allyOwnerUUIDString)) {
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-clan-already-your-ally"));
                                                return true;

                                            } else {
                                                ClansStorageUtil.addClanAlly(player, allyClanOwner);

                                                foliaLib.getImpl().runAsync((task) -> {
                                                    fireAsyncClanAllyAddEvent(player, clan, allyClanOwner, allyClan);
                                                    MessageUtils.sendDebugConsole("Fired AsyncClanAllyAddEvent");
                                                });

                                                MessageUtils.sendPlayer(player, messagesConfig.getString("added-clan-to-your-allies")
                                                        .replace(ALLY_CLAN, allyClan.getClanFinalName()));
                                            }

                                            if (allyClanOwner.isOnline()) {
                                                MessageUtils.sendPlayer(allyClanOwner, messagesConfig.getString("clan-added-to-other-allies")
                                                        .replace(CLAN_OWNER, player.getName()));
                                            } else {
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-to-add-clan-to-allies")
                                                        .replace(ALLY_OWNER, args[2]));
                                            }

                                        } else {
                                            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-cannot-ally-your-own-clan"));
                                        }

                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-player-not-clan-owner")
                                                .replace(ALLY_OWNER, args[2]));
                                    }

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("ally-clan-add-owner-offline")
                                            .replace(ALLY_OWNER, args[2]));
                                }
                            }

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("clan-must-be-owner"));
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-clan-ally-command-usage"));
                    }
                    return true;

                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args[2].length() > 1) {
                        if (ClansStorageUtil.isClanOwner(player)) {
                            if (ClansStorageUtil.findClanByOwner(player) != null) {

                                Player allyClanOwner = Bukkit.getPlayer(args[2]);
                                if (allyClanOwner != null) {
                                    if (ClansStorageUtil.findClanByOwner(allyClanOwner) != null) {

                                        Clan allyClan = ClansStorageUtil.findClanByOwner(allyClanOwner);
                                        List<String> alliedClans = ClansStorageUtil.findClanByOwner(player).getClanAllies();
                                        UUID allyClanOwnerUUID = allyClanOwner.getUniqueId();
                                        String allyClanOwnerString = allyClanOwnerUUID.toString();

                                        if (alliedClans.contains(allyClanOwnerString)) {

                                            foliaLib.getImpl().runAsync((task) -> {
                                                fireAsyncClanAllyRemoveEvent(player, allyClanOwner, allyClan);
                                                MessageUtils.sendDebugConsole("Fired AsyncClanAllyRemoveEvent");
                                            });

                                            ClansStorageUtil.removeClanAlly(player, allyClanOwner);
                                            MessageUtils.sendPlayer(player, messagesConfig.getString("removed-clan-from-your-allies")
                                                    .replace(ALLY_CLAN, allyClan.getClanFinalName()));

                                            if (allyClanOwner.isOnline()) {
                                                MessageUtils.sendPlayer(allyClanOwner, messagesConfig.getString("clan-removed-from-other-allies")
                                                        .replace(CLAN_OWNER, player.getName()));
                                            }

                                        } else {
                                            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-to-remove-clan-from-allies")
                                                    .replace(ALLY_OWNER, args[2]));
                                        }

                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-player-not-clan-owner")
                                                .replace(ALLY_OWNER, args[2]));
                                    }

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("ally-clan-remove-owner-offline")
                                            .replace(ALLY_OWNER, args[2]));
                                }
                            }

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("clan-must-be-owner"));
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-clan-ally-command-usage"));
                    }
                }
                return true;

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-clan-ally-command-usage"));
            }
            return true;

        }
        return false;
    }

    private void fireAsyncClanAllyRemoveEvent(Player player, Player allyClanOwner, Clan allyClan) {
        AsyncClanAllyRemoveEvent asyncClanAllyRemoveEvent = new AsyncClanAllyRemoveEvent(true, player, ClansStorageUtil.findClanByOwner(player), allyClanOwner, allyClan);
        Bukkit.getPluginManager().callEvent(asyncClanAllyRemoveEvent);
    }

    private void fireAsyncClanAllyAddEvent(Player player, Clan clan, Player allyClanOwner, Clan allyClan) {
        AsyncClanAllyAddEvent asyncClanAllyAddEvent = new AsyncClanAllyAddEvent(true, player, clan, allyClanOwner, allyClan);
        Bukkit.getPluginManager().callEvent(asyncClanAllyAddEvent);
    }
}
