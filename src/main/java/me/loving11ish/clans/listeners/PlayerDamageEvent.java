package me.loving11ish.clans.listeners;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanFriendlyFireAttackEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;

import java.util.*;

public class PlayerDamageEvent implements Listener {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {

            Player hurtPlayer = (Player) event.getEntity();
            String hurtUUID = hurtPlayer.getUniqueId().toString();

            if (event.getDamager() instanceof Player) {
                Player attackingPlayer = (Player) event.getDamager();
                attackingPlayer.setInvulnerable(false);
                Clan attackingClan = ClansStorageUtil.findClanByOwner(attackingPlayer);
                Clan victimClan = ClansStorageUtil.findClanByOwner(hurtPlayer);

                if (attackingClan != null) {
                    ArrayList<String> attackingClanMembers = attackingClan.getClanMembers();
                    if (attackingClanMembers.contains(hurtUUID) || attackingClan.getClanOwner().equals(hurtUUID)) {
                        if (clansConfig.getBoolean("protections.pvp.pvp-command-enabled")) {
                            if (!attackingClan.isFriendlyFireAllowed()) {
                                if (clansConfig.getBoolean("protections.pvp.enable-bypass-permission")) {
                                    if (attackingPlayer.hasPermission("clanslite.bypass.pvp")
                                            || attackingPlayer.hasPermission("clanslite.bypass.*")
                                            || attackingPlayer.hasPermission("clanslite.*")
                                            || attackingPlayer.isOp()) {
                                        return;
                                    }
                                }

                                event.setCancelled(true);

                                foliaLib.getImpl().runAsync((task) -> {
                                    fireAsyncClanFriendlyFireAttackEvent(hurtPlayer, attackingPlayer, hurtPlayer, attackingClan, victimClan);
                                    MessageUtils.sendDebugConsole("Fired AsyncClanFriendlyFireAttackEvent");
                                });

                                MessageUtils.sendPlayer(attackingPlayer, messagesConfig.getString("friendly-fire-is-disabled"));
                            }
                        } else {
                            event.setCancelled(false);
                        }
                    }

                } else {
                    Clan attackingClanByPlayer = ClansStorageUtil.findClanByPlayer(attackingPlayer);
                    Clan victimClanByPlayer = ClansStorageUtil.findClanByPlayer(hurtPlayer);

                    if (attackingClanByPlayer != null) {
                        ArrayList<String> attackingMembers = attackingClanByPlayer.getClanMembers();
                        if (attackingMembers.contains(hurtUUID) || attackingClanByPlayer.getClanOwner().equals(hurtUUID)) {
                            if (clansConfig.getBoolean("protections.pvp.pvp-command-enabled")) {
                                if (!attackingClanByPlayer.isFriendlyFireAllowed()) {
                                    if (clansConfig.getBoolean("protections.pvp.enable-bypass-permission")) {
                                        if (attackingPlayer.hasPermission("clanslite.bypass.pvp")
                                                || attackingPlayer.hasPermission("clanslite.bypass.*")
                                                || attackingPlayer.hasPermission("clanslite.bypass")
                                                || attackingPlayer.hasPermission("clanslite.*")
                                                || attackingPlayer.isOp()) {
                                            return;
                                        }
                                    }

                                    event.setCancelled(true);

                                    foliaLib.getImpl().runAsync((task) -> {
                                       fireAsyncClanFriendlyFireAttackEvent(hurtPlayer, attackingPlayer, hurtPlayer, attackingClanByPlayer, victimClanByPlayer);
                                       MessageUtils.sendDebugConsole("Fired AsyncClanFriendlyFireAttackEvent");
                                    });

                                    MessageUtils.sendPlayer(attackingPlayer, messagesConfig.getString("friendly-fire-is-disabled"));
                                }

                            } else {
                                event.setCancelled(false);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void fireAsyncClanFriendlyFireAttackEvent(Player createdBy, Player attackingPlayer, Player victimPlayer, Clan attackingClan, Clan victimClan) {
        AsyncClanFriendlyFireAttackEvent asyncClanFriendlyFireAttackEvent = new AsyncClanFriendlyFireAttackEvent(true, createdBy, attackingPlayer, victimPlayer, attackingClan, victimClan);
        Bukkit.getPluginManager().callEvent(asyncClanFriendlyFireAttackEvent);
    }
}