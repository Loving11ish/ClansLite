package me.loving11ish.clans.listeners;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncPlayerPointsAwardedEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.UserMapStorageUtil;

public class PlayerKillEvent implements Listener {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private final Integer nonEnemyPointValue = clansConfig.getInt("points.player-points.non-enemy-clan-point-amount-on-kill");
    private final Integer enemyPointValue = clansConfig.getInt("points.player-points.enemy-clan-point-amount-on-kill");

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (!clansConfig.getBoolean("points.player-points.enabled")) {
            return;
        }

        if (event.getDamager() instanceof Player) {
            Player killer = (Player) event.getDamager();
            if (event.getEntity() instanceof Player) {
                Player victim = (Player) event.getEntity();
                if (victim.getLastDamage() >= victim.getHealth()) {

                    if (ClansStorageUtil.findClanByPlayer(killer) == null && ClansStorageUtil.findClanByPlayer(victim) == null) {
                        MessageUtils.sendPlayer(killer, messagesConfig.getString("player-points-killer-non-enemy-received-success")
                                .replace("%PLAYER%", victim.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString()));

                        if (clansConfig.getBoolean("points.player-points.take-points-from-victim")) {
                            if (UserMapStorageUtil.withdrawPoints(victim, nonEnemyPointValue)) {
                                MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-non-enemy-withdrawn-success")
                                        .replace("%KILLER%", killer.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString()));
                            } else {
                                MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-withdraw-failed"));
                                MessageUtils.sendConsole(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                        .replace("%VICTIM%", victim.getName()));
                            }
                        }

                        UserMapStorageUtil.addPointsToOnlinePlayer(killer, nonEnemyPointValue);

                        foliaLib.getImpl().runAsync((task) -> {
                            fireAsyncPlayerPointsAwardedEvent(killer, killer, victim, null, null, nonEnemyPointValue, false);
                            MessageUtils.sendDebugConsole("Fired AsyncPlayerPointsAwardedEvent");
                        });


                    } else if (ClansStorageUtil.findClanByOwner(killer) != null && ClansStorageUtil.findClanByOwner(victim) != null) {
                        Clan killerClanOwner = ClansStorageUtil.findClanByOwner(killer);
                        Clan victimClanOwner = ClansStorageUtil.findClanByOwner(victim);

                        if (killerClanOwner != null && victimClanOwner != null) {
                            if (killerClanOwner.getClanEnemies().contains(victimClanOwner.getClanOwner()) || victimClanOwner.getClanEnemies().contains(killerClanOwner.getClanOwner())) {
                                MessageUtils.sendPlayer(killer, messagesConfig.getString("player-points-killer-enemy-received-success")
                                        .replace("%PLAYER%", victim.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString()));

                                if (clansConfig.getBoolean("points.player-points.take-points-from-victim")) {
                                    if (UserMapStorageUtil.withdrawPoints(victim, enemyPointValue)) {
                                        MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-enemy-withdrawn-success")
                                                .replace("%KILLER%", killer.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString()));
                                    } else {
                                        MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-withdraw-failed"));
                                        MessageUtils.sendConsole(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                                .replace("%VICTIM%", victim.getName()));
                                    }
                                }

                                UserMapStorageUtil.addPointsToOnlinePlayer(killer, enemyPointValue);

                                foliaLib.getImpl().runAsync((task) -> {
                                    fireAsyncPlayerPointsAwardedEvent(killer, killer, victim, killerClanOwner, victimClanOwner, enemyPointValue, true);
                                    MessageUtils.sendDebugConsole("Fired AsyncPlayerPointsAwardedEvent");
                                });
                            }

                        } else {
                            MessageUtils.sendPlayer(killer, messagesConfig.getString("player-points-killer-non-enemy-received-success")
                                    .replace("%PLAYER%", victim.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString()));

                            if (clansConfig.getBoolean("points.player-points.take-points-from-victim")) {
                                if (UserMapStorageUtil.withdrawPoints(victim, nonEnemyPointValue)) {
                                    MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-non-enemy-withdrawn-success")
                                            .replace("%KILLER%", killer.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString()));
                                } else {
                                    MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-withdraw-failed"));
                                    MessageUtils.sendConsole(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                            .replace("%VICTIM%", victim.getName()));
                                }
                            }

                            UserMapStorageUtil.addPointsToOnlinePlayer(killer, nonEnemyPointValue);

                            foliaLib.getImpl().runAsync((task) -> {
                                fireAsyncPlayerPointsAwardedEvent(killer, killer, victim, killerClanOwner, victimClanOwner, nonEnemyPointValue, false);
                                MessageUtils.sendDebugConsole("Fired AsyncPlayerPointsAwardedEvent");
                            });
                        }

                    } else {
                        if (ClansStorageUtil.findClanByPlayer(killer) != null && ClansStorageUtil.findClanByPlayer(victim) != null) {
                            Clan killerClan = ClansStorageUtil.findClanByPlayer(killer);
                            Clan victimClan = ClansStorageUtil.findClanByPlayer(victim);

                            if (killerClan != null && victimClan != null) {
                                if (killerClan.getClanEnemies() != null && !killerClan.getClanEnemies().isEmpty() || victimClan.getClanEnemies() != null && !victimClan.getClanEnemies().isEmpty()) {
                                    if (killerClan.getClanEnemies().contains(victimClan.getClanOwner()) || victimClan.getClanEnemies().contains(killerClan.getClanOwner())) {
                                        MessageUtils.sendPlayer(killer, messagesConfig.getString("player-points-killer-enemy-received-success")
                                                .replace("%PLAYER%", victim.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString()));

                                        if (clansConfig.getBoolean("points.player-points.take-points-from-victim")) {
                                            if (UserMapStorageUtil.withdrawPoints(victim, enemyPointValue)) {
                                                MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-enemy-withdrawn-success")
                                                        .replace("%KILLER%", killer.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString()));
                                            } else {
                                                MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-withdraw-failed"));
                                                MessageUtils.sendConsole(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                                        .replace("%VICTIM%", victim.getName()));
                                            }
                                        }

                                        UserMapStorageUtil.addPointsToOnlinePlayer(killer, enemyPointValue);

                                        foliaLib.getImpl().runAsync((task) -> {
                                            fireAsyncPlayerPointsAwardedEvent(killer, killer, victim, killerClan, victimClan, enemyPointValue, true);
                                            MessageUtils.sendDebugConsole("Fired AsyncPlayerPointsAwardedEvent");
                                        });

                                    } else {
                                        MessageUtils.sendPlayer(killer, messagesConfig.getString("player-points-killer-non-enemy-received-success")
                                                .replace("%PLAYER%", victim.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString()));

                                        if (clansConfig.getBoolean("points.player-points.take-points-from-victim")) {
                                            if (UserMapStorageUtil.withdrawPoints(victim, nonEnemyPointValue)) {
                                                MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-non-enemy-withdrawn-success")
                                                        .replace("%KILLER%", killer.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString()));
                                            } else {
                                                MessageUtils.sendPlayer(victim, messagesConfig.getString("player-points-victim-withdraw-failed"));
                                                MessageUtils.sendConsole(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                                        .replace("%VICTIM%", victim.getName()));
                                            }
                                        }

                                        UserMapStorageUtil.addPointsToOnlinePlayer(killer, nonEnemyPointValue);

                                        foliaLib.getImpl().runAsync((task) -> {
                                            fireAsyncPlayerPointsAwardedEvent(killer, killer, victim, killerClan, victimClan, nonEnemyPointValue, false);
                                            MessageUtils.sendDebugConsole("Fired AsyncPlayerPointsAwardedEvent");
                                        });

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void fireAsyncPlayerPointsAwardedEvent(Player createdBy, Player killer, Player victim, @Nullable Clan killerClan, @Nullable Clan victimClan, int pointValue, boolean isEnemyPointReward) {
        AsyncPlayerPointsAwardedEvent playerPointsAwardedEvent = new AsyncPlayerPointsAwardedEvent(true, createdBy, killer, victim, killerClan, victimClan, pointValue, isEnemyPointReward);
        Bukkit.getPluginManager().callEvent(playerPointsAwardedEvent);
    }
}
