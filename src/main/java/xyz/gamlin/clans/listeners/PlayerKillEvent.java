package xyz.gamlin.clans.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.api.PlayerPointsAwardedEvent;
import xyz.gamlin.clans.models.Clan;
import xyz.gamlin.clans.utils.*;
import xyz.gamlin.clans.utils.abstractClasses.StorageUtils;
import xyz.gamlin.clans.utils.abstractClasses.UsermapUtils;

import java.util.logging.Logger;

public class PlayerKillEvent implements Listener {

    Logger logger = Clans.getPlugin().getLogger();

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;
    private UsermapUtils usermapUtils = Clans.getPlugin().usermapUtils;

    private Integer nonEnemyPointValue = clansConfig.getInt("points.player-points.non-enemy-clan-point-amount-on-kill");
    private Integer enemyPointValue = clansConfig.getInt("points.player-points.enemy-clan-point-amount-on-kill");

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent event){
        if (!clansConfig.getBoolean("points.player-points.enabled")){
            return;
        }
        if (event.getDamager() instanceof Player killer){
            if (event.getEntity() instanceof Player victim){
                if (victim.getLastDamage() >= victim.getHealth()){

                    if (storageUtils.findClanByPlayer(killer) == null || storageUtils.findClanByPlayer(victim) == null){
                        killer.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-killer-non-enemy-received-success")
                                .replace("%PLAYER%", victim.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString())));
                        if (clansConfig.getBoolean("points.player-points.take-points-from-victim")){
                            if (usermapUtils.withdrawPoints(victim, nonEnemyPointValue)){
                                victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-non-enemy-withdrawn-success")
                                        .replace("%KILLER%", killer.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString())));
                            }else {
                                victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-withdraw-failed")));
                                logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                        .replace("%VICTIM%", victim.getName())));
                            }
                        }
                        usermapUtils.addPointsToOnlinePlayer(killer, nonEnemyPointValue);
                        //TODO -> fix this non clan event firing!
//                        firePlayerPointsAwardedEvent(killer, killer, victim, null, null, nonEnemyPointValue, false);
//                        if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
//                            logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired PlayerPointsAwardedEvent"));
//                        }
                    }

                    else if (storageUtils.findClanByOwner(killer) != null || storageUtils.findClanByOwner(victim) != null){
                        Clan killerClanOwner = storageUtils.findClanByOwner(killer);
                        Clan victimClanOwner = storageUtils.findClanByOwner(victim);
                        if (killerClanOwner.getClanEnemies().contains(victimClanOwner.getClanOwner())||victimClanOwner.getClanEnemies().contains(killerClanOwner.getClanOwner())){
                            killer.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-killer-enemy-received-success")
                                    .replace("%PLAYER%", victim.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString())));
                            if (clansConfig.getBoolean("points.player-points.take-points-from-victim")){
                                if (usermapUtils.withdrawPoints(victim, enemyPointValue)){
                                    victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-enemy-withdrawn-success")
                                            .replace("%KILLER%", killer.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString())));
                                }else {
                                    victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-withdraw-failed")));
                                    logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                            .replace("%VICTIM%", victim.getName())));
                                }
                            }
                            usermapUtils.addPointsToOnlinePlayer(killer, enemyPointValue);
                            firePlayerPointsAwardedEvent(killer, killer, victim, killerClanOwner, victimClanOwner, enemyPointValue, true);
                            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired PlayerPointsAwardedEvent"));
                            }
                        }else {
                            killer.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-killer-non-enemy-received-success")
                                    .replace("%PLAYER%", victim.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString())));
                            if (clansConfig.getBoolean("points.player-points.take-points-from-victim")){
                                if (usermapUtils.withdrawPoints(victim, nonEnemyPointValue)){
                                    victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-non-enemy-withdrawn-success")
                                            .replace("%KILLER%", killer.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString())));
                                }else {
                                    victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-withdraw-failed")));
                                    logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                            .replace("%VICTIM%", victim.getName())));
                                }
                            }
                            usermapUtils.addPointsToOnlinePlayer(killer, nonEnemyPointValue);
                            firePlayerPointsAwardedEvent(killer, killer, victim, killerClanOwner, victimClanOwner, nonEnemyPointValue, false);
                            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired PlayerPointsAwardedEvent"));
                            }
                        }
                    }

                    else {
                        if (storageUtils.findClanByPlayer(killer) != null || storageUtils.findClanByPlayer(victim) != null){
                            Clan killerClan = storageUtils.findClanByPlayer(killer);
                            Clan victimClan = storageUtils.findClanByPlayer(victim);
                            if (killerClan.getClanEnemies() != null && !killerClan.getClanEnemies().isEmpty() || victimClan.getClanEnemies() != null && !victimClan.getClanEnemies().isEmpty()){
                                if (killerClan.getClanEnemies().contains(victimClan.getClanOwner()) || victimClan.getClanEnemies().contains(killerClan.getClanOwner())) {
                                    killer.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-killer-enemy-received-success")
                                            .replace("%PLAYER%", victim.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString())));
                                    if (clansConfig.getBoolean("points.player-points.take-points-from-victim")) {
                                        if (usermapUtils.withdrawPoints(victim, enemyPointValue)) {
                                            victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-enemy-withdrawn-success")
                                                    .replace("%KILLER%", killer.getName()).replace("%ENEMYPOINTVALUE%", enemyPointValue.toString())));
                                        }else {
                                            victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-withdraw-failed")));
                                            logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                                    .replace("%VICTIM%", victim.getName())));
                                        }
                                    }
                                    usermapUtils.addPointsToOnlinePlayer(killer, enemyPointValue);
                                    firePlayerPointsAwardedEvent(killer, killer, victim, killerClan, victimClan, enemyPointValue, true);
                                    if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                        logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired PlayerPointsAwardedEvent"));
                                    }
                                }else {
                                    killer.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-killer-non-enemy-received-success")
                                            .replace("%PLAYER%", victim.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString())));
                                    if (clansConfig.getBoolean("points.player-points.take-points-from-victim")){
                                        if (usermapUtils.withdrawPoints(victim, nonEnemyPointValue)){
                                            victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-non-enemy-withdrawn-success")
                                                    .replace("%KILLER%", killer.getName()).replace("%POINTVALUE%", nonEnemyPointValue.toString())));
                                        }else {
                                            victim.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-victim-withdraw-failed")));
                                            logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("player-points-console-victim-point-withdraw-failed")
                                                    .replace("%VICTIM%", victim.getName())));
                                        }
                                    }
                                    usermapUtils.addPointsToOnlinePlayer(killer, nonEnemyPointValue);
                                    firePlayerPointsAwardedEvent(killer, killer, victim, killerClan, victimClan, nonEnemyPointValue, false);
                                    if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                        logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired PlayerPointsAwardedEvent"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void firePlayerPointsAwardedEvent(Player createdBy, Player killer, Player victim, Clan killerClan, Clan victimClan, int pointValue, boolean isEnemyPointReward){
        PlayerPointsAwardedEvent playerPointsAwardedEvent = new PlayerPointsAwardedEvent(createdBy, killer, victim, killerClan, victimClan, pointValue, isEnemyPointReward);
        Bukkit.getPluginManager().callEvent(playerPointsAwardedEvent);
    }
}
