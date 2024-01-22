package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.events.AsyncClanEnemyAddEvent;
import me.loving11ish.clans.api.events.AsyncClanEnemyRemoveEvent;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClanEnemySubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String ENEMY_CLAN = "%ENEMYCLAN%";
    private static final String ENEMY_OWNER = "%ENEMYOWNER%";
    private static final String CLAN_OWNER = "%CLANOWNER%";

    public boolean clanEnemySubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 2) {
                if (args[1].equalsIgnoreCase("add")) {
                    if (args[2].length() > 1) {
                        if (ClansStorageUtil.isClanOwner(player)) {
                            if (ClansStorageUtil.findClanByOwner(player) != null) {
                                Clan clan = ClansStorageUtil.findClanByOwner(player);
                                Player enemyClanOwner = Bukkit.getPlayer(args[2]);

                                if (enemyClanOwner != null) {
                                    if (ClansStorageUtil.findClanByOwner(enemyClanOwner) != null) {
                                        if (ClansStorageUtil.findClanByOwner(player) != ClansStorageUtil.findClanByOwner(enemyClanOwner)) {
                                            Clan enemyClan = ClansStorageUtil.findClanByOwner(enemyClanOwner);
                                            String enemyOwnerUUIDString = enemyClan.getClanOwner();

                                            if (ClansStorageUtil.findClanByOwner(player).getClanEnemies().size() >= clansConfig.getInt("max-clan-enemies")) {
                                                int maxSize = clansConfig.getInt("max-clan-enemies");
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("clan-enemy-max-amount-reached")
                                                        .replace("%LIMIT%", String.valueOf(maxSize)));
                                                return true;
                                            }

                                            if (clan.getClanAllies().contains(enemyOwnerUUIDString)) {
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-cannot-enemy-allied-clan"));
                                                return true;
                                            }

                                            if (clan.getClanEnemies().contains(enemyOwnerUUIDString)) {
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-clan-already-your-enemy"));
                                                return true;

                                            } else {
                                                ClansStorageUtil.addClanEnemy(player, enemyClanOwner);

                                                foliaLib.getImpl().runAsync((task) -> {
                                                    fireAsyncClanEnemyAddEvent(player, clan, enemyClanOwner, enemyClan);
                                                    MessageUtils.sendDebugConsole("Fired AsyncClanEnemyAddEvent");
                                                });

                                                MessageUtils.sendPlayer(player, messagesConfig.getString("added-clan-to-your-enemies").replace(ENEMY_CLAN, enemyClan.getClanFinalName()));

                                                String titleMain = ColorUtils.translateColorCodes(messagesConfig.getString("added-enemy-clan-to-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                                String titleAux = ColorUtils.translateColorCodes(messagesConfig.getString("added-enemy-clan-to-your-enemies-title-2").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                                player.sendTitle(titleMain, titleAux, 10, 70, 20);

                                                ArrayList<String> playerClanMembers = ClansStorageUtil.findClanByOwner(player).getClanMembers();

                                                for (String playerClanMember : playerClanMembers) {
                                                    if (playerClanMember != null) {
                                                        UUID memberUUID = UUID.fromString(playerClanMember);
                                                        Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (playerClanPlayer != null) {
                                                            playerClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }

                                            if (enemyClanOwner.isOnline()) {
                                                MessageUtils.sendPlayer(enemyClanOwner, messagesConfig.getString("clan-added-to-other-enemies").replace(CLAN_OWNER, player.getName()));

                                                String titleMainEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-added-to-other-enemies-title-1").replace(CLAN_OWNER, player.getName()));
                                                String titleAuxEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-added-to-other-enemies-title-2").replace(CLAN_OWNER, player.getName()));
                                                enemyClanOwner.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);

                                                ArrayList<String> enemyClanMembers = enemyClan.getClanMembers();

                                                for (String enemyClanMember : enemyClanMembers) {
                                                    if (enemyClanMember != null) {
                                                        UUID memberUUID = UUID.fromString(enemyClanMember);
                                                        Player enemyClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (enemyClanPlayer != null) {
                                                            enemyClanPlayer.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);
                                                        }
                                                    }
                                                }

                                            } else {
                                                MessageUtils.sendPlayer(player, messagesConfig.getString("ailed-to-add-clan-to-enemies").replace(ENEMY_OWNER, args[2]));
                                            }

                                        } else {
                                            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-cannot-enemy-your-own-clan"));
                                        }

                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-enemy-player-not-clan-owner").replace(ENEMY_OWNER, args[2]));
                                    }

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("enemy-clan-add-owner-offline").replace(ENEMY_OWNER, args[2]));
                                }
                            }

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("clan-must-be-owner"));
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-clan-enemy-command-usage"));
                    }
                    return true;

                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args[2].length() > 1) {
                        if (ClansStorageUtil.isClanOwner(player)) {
                            if (ClansStorageUtil.findClanByOwner(player) != null) {
                                Player enemyClanOwner = Bukkit.getPlayer(args[2]);

                                if (enemyClanOwner != null) {
                                    if (ClansStorageUtil.findClanByOwner(enemyClanOwner) != null) {
                                        Clan enemyClan = ClansStorageUtil.findClanByOwner(enemyClanOwner);
                                        List<String> enemyClans = ClansStorageUtil.findClanByOwner(player).getClanEnemies();
                                        UUID enemyClanOwnerUUID = enemyClanOwner.getUniqueId();
                                        String enemyClanOwnerString = enemyClanOwnerUUID.toString();

                                        if (enemyClans.contains(enemyClanOwnerString)) {

                                            foliaLib.getImpl().runAsync((task) -> {
                                                fireAsyncClanEnemyRemoveEvent(player, enemyClanOwner, enemyClan);
                                                MessageUtils.sendDebugConsole("Fired AsyncClanEnemyRemoveEvent");
                                            });

                                            ClansStorageUtil.removeClanEnemy(player, enemyClanOwner);

                                            MessageUtils.sendPlayer(player, messagesConfig.getString("removed-clan-from-your-enemies").replace(ENEMY_CLAN, enemyClan.getClanFinalName()));

                                            String titleMain = ColorUtils.translateColorCodes(messagesConfig.getString("removed-enemy-clan-from-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                            String titleAux = ColorUtils.translateColorCodes(messagesConfig.getString("removed-enemy-clan-from-your-enemies-title-1").replace(CLAN_OWNER, enemyClanOwner.getName()));
                                            player.sendTitle(titleMain, titleAux, 10, 70, 20);

                                            ArrayList<String> playerClanMembers = ClansStorageUtil.findClanByOwner(player).getClanMembers();

                                            for (String playerClanMember : playerClanMembers) {
                                                if (playerClanMember != null) {
                                                    UUID memberUUID = UUID.fromString(playerClanMember);
                                                    Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                                    if (playerClanPlayer != null) {
                                                        playerClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                    }
                                                }
                                            }

                                            if (enemyClanOwner.isOnline()) {
                                                MessageUtils.sendPlayer(enemyClanOwner, messagesConfig.getString("clan-removed-from-other-enemies").replace(ENEMY_OWNER, player.getName()));

                                                String titleMainEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-removed-from-other-enemies-title-1").replace(CLAN_OWNER, player.getName()));
                                                String titleAuxEnemy = ColorUtils.translateColorCodes(messagesConfig.getString("clan-removed-from-other-enemies-title-2").replace(CLAN_OWNER, player.getName()));
                                                enemyClanOwner.sendTitle(titleMainEnemy, titleAuxEnemy, 10, 70, 20);

                                                ArrayList<String> enemyClanMembers = enemyClan.getClanMembers();

                                                for (String enemyClanMember : enemyClanMembers) {
                                                    if (enemyClanMember != null) {
                                                        UUID memberUUID = UUID.fromString(enemyClanMember);
                                                        Player enemyClanPlayer = Bukkit.getPlayer(memberUUID);
                                                        if (enemyClanPlayer != null) {
                                                            enemyClanPlayer.sendTitle(titleMain, titleAux, 10, 70, 20);
                                                        }
                                                    }
                                                }
                                            }

                                        } else {
                                            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-to-remove-clan-from-enemies").replace(ENEMY_OWNER, args[2]));
                                        }

                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-enemy-player-not-clan-owner").replace(ENEMY_OWNER, args[2]));
                                    }

                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("enemy-clan-remove-owner-offline").replace(ENEMY_OWNER, args[2]));
                                }
                            }

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("clan-must-be-owner"));
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-clan-enemy-command-usage"));
                    }
                }
                return true;

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-clan-enemy-command-usage"));
            }

        }
        return false;
    }

    private static void fireAsyncClanEnemyRemoveEvent(Player player, Player enemyClanOwner, Clan enemyClan) {
        AsyncClanEnemyRemoveEvent asyncClanEnemyRemoveEvent = new AsyncClanEnemyRemoveEvent(true, player, ClansStorageUtil.findClanByPlayer(player), enemyClanOwner, enemyClan);
        Bukkit.getPluginManager().callEvent(asyncClanEnemyRemoveEvent);
    }

    private static void fireAsyncClanEnemyAddEvent(Player player, Clan clan, Player enemyClanOwner, Clan enemyClan) {
        AsyncClanEnemyAddEvent asyncClanEnemyAddEvent = new AsyncClanEnemyAddEvent(true, player, clan, enemyClanOwner, enemyClan);
        Bukkit.getPluginManager().callEvent(asyncClanEnemyAddEvent);
    }
}
