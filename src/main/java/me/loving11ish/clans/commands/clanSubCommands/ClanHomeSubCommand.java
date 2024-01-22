package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanHomePreTeleportEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.TeleportUtils;

import java.util.HashMap;
import java.util.UUID;

public class ClanHomeSubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String TIME_LEFT = "%TIMELEFT%";

    private static AsyncClanHomePreTeleportEvent asyncHomePreTeleportEvent = null;

    private final HashMap<UUID, Long> homeCoolDownTimer = new HashMap<>();

    public boolean tpClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (clansConfig.getBoolean("clan-home.enabled")) {

                UUID uuid = player.getUniqueId();
                if (ClansStorageUtil.findClanByOwner(player) != null) {
                    Clan clanByOwner = ClansStorageUtil.findClanByOwner(player);

                    if (clanByOwner.getClanHomeWorld() != null) {

                        foliaLib.getImpl().runAsync((task) -> {
                            fireAsyncClanHomePreTPEvent(player, clanByOwner);
                            MessageUtils.sendDebugConsole("Fired AsyncClanHomePreTPEvent");
                        });

                        if (asyncHomePreTeleportEvent == null) {
                            MessageUtils.sendDebugConsole("AsyncClanHomePreTPEvent is null");
                            return true;
                        }

                        if (asyncHomePreTeleportEvent.isCancelled()) {
                            MessageUtils.sendDebugConsole("AsyncClanHomePreTPEvent is cancelled by external source");
                            return true;
                        }

                        World world = Bukkit.getWorld(clanByOwner.getClanHomeWorld());
                        double x = clanByOwner.getClanHomeX();
                        double y = clanByOwner.getClanHomeY() + 0.2;
                        double z = clanByOwner.getClanHomeZ();
                        float yaw = clanByOwner.getClanHomeYaw();
                        float pitch = clanByOwner.getClanHomePitch();

                        if (clansConfig.getBoolean("clan-home.cool-down.enabled")) {
                            if (homeCoolDownTimer.containsKey(uuid)) {

                                if (!(player.hasPermission("clanslite.bypass.homecooldown") || player.hasPermission("clanslite.bypass.*")
                                        || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {

                                    if (homeCoolDownTimer.get(uuid) > System.currentTimeMillis()) {
                                        long timeLeft = (homeCoolDownTimer.get(uuid) - System.currentTimeMillis()) / 1000;
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("home-cool-down-timer-wait")
                                                .replace(TIME_LEFT, Long.toString(timeLeft)));

                                    } else {
                                        homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (clansConfig.getLong("clan-home.cool-down.time") * 1000));
                                        Location location = new Location(world, x, y, z, yaw, pitch);

                                        if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                            if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                                    || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                                TeleportUtils teleportUtils = new TeleportUtils();
                                                teleportUtils.teleportAsyncTimed(player, clanByOwner, location);
                                            } else {
                                                TeleportUtils teleportUtils = new TeleportUtils();
                                                teleportUtils.teleportAsync(player, clanByOwner, location);
                                            }
                                        } else {
                                            TeleportUtils teleportUtils = new TeleportUtils();
                                            teleportUtils.teleportAsync(player, clanByOwner, location);
                                        }
                                    }

                                } else {
                                    Location location = new Location(world, x, y, z, yaw, pitch);

                                    if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                        if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                                || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                            TeleportUtils teleportUtils = new TeleportUtils();
                                            teleportUtils.teleportAsyncTimed(player, clanByOwner, location);
                                        } else {
                                            TeleportUtils teleportUtils = new TeleportUtils();
                                            teleportUtils.teleportAsync(player, clanByOwner, location);
                                        }
                                    } else {
                                        TeleportUtils teleportUtils = new TeleportUtils();
                                        teleportUtils.teleportAsync(player, clanByOwner, location);
                                    }
                                }
                            } else {
                                homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (clansConfig.getLong("clan-home.cool-down.time") * 1000));
                                Location location = new Location(world, x, y, z, yaw, pitch);

                                if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                    if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                            || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                        TeleportUtils teleportUtils = new TeleportUtils();
                                        teleportUtils.teleportAsyncTimed(player, clanByOwner, location);
                                    } else {
                                        TeleportUtils teleportUtils = new TeleportUtils();
                                        teleportUtils.teleportAsync(player, clanByOwner, location);
                                    }
                                } else {
                                    TeleportUtils teleportUtils = new TeleportUtils();
                                    teleportUtils.teleportAsync(player, clanByOwner, location);
                                }
                            }

                        } else {
                            Location location = new Location(world, x, y, z, yaw, pitch);

                            if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                        || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                    TeleportUtils teleportUtils = new TeleportUtils();
                                    teleportUtils.teleportAsyncTimed(player, clanByOwner, location);
                                } else {
                                    TeleportUtils teleportUtils = new TeleportUtils();
                                    teleportUtils.teleportAsync(player, clanByOwner, location);
                                }
                            } else {
                                TeleportUtils teleportUtils = new TeleportUtils();
                                teleportUtils.teleportAsync(player, clanByOwner, location);
                            }
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-no-home-set"));
                    }

                } else if (ClansStorageUtil.findClanByPlayer(player) != null) {
                    Clan clanByPlayer = ClansStorageUtil.findClanByPlayer(player);

                    foliaLib.getImpl().runAsync((task) -> {
                        fireAsyncClanHomePreTPEvent(player, clanByPlayer);
                        MessageUtils.sendDebugConsole("Fired AsyncClanHomePreTPEvent");
                    });

                    if (asyncHomePreTeleportEvent == null) {
                        MessageUtils.sendDebugConsole("AsyncClanHomePreTPEvent is null");
                        return true;
                    }

                    if (asyncHomePreTeleportEvent.isCancelled()) {
                        MessageUtils.sendDebugConsole("AsyncClanHomePreTPEvent is cancelled by external source");
                        return true;
                    }

                    if (clanByPlayer.getClanHomeWorld() != null) {

                        World world = Bukkit.getWorld(clanByPlayer.getClanHomeWorld());
                        double x = clanByPlayer.getClanHomeX();
                        double y = clanByPlayer.getClanHomeY() + 0.2;
                        double z = clanByPlayer.getClanHomeZ();
                        float yaw = clanByPlayer.getClanHomeYaw();
                        float pitch = clanByPlayer.getClanHomePitch();

                        if (clansConfig.getBoolean("clan-home.cool-down.enabled")) {
                            if (homeCoolDownTimer.containsKey(uuid)) {

                                if (!(player.hasPermission("clanslite.bypass.homecooldown") || player.hasPermission("clanslite.bypass.*")
                                        || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {

                                    if (homeCoolDownTimer.get(uuid) > System.currentTimeMillis()) {
                                        long timeLeft = (homeCoolDownTimer.get(uuid) - System.currentTimeMillis()) / 1000;
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("home-cool-down-timer-wait")
                                                .replace(TIME_LEFT, Long.toString(timeLeft)));

                                    } else {
                                        homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (clansConfig.getLong("clan-home.cool-down.time") * 1000));
                                        Location location = new Location(world, x, y, z, yaw, pitch);

                                        if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                            if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                                    || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                                TeleportUtils teleportUtils = new TeleportUtils();
                                                teleportUtils.teleportAsyncTimed(player, clanByPlayer, location);
                                            } else {
                                                TeleportUtils teleportUtils = new TeleportUtils();
                                                teleportUtils.teleportAsync(player, clanByPlayer, location);
                                            }
                                        } else {
                                            TeleportUtils teleportUtils = new TeleportUtils();
                                            teleportUtils.teleportAsync(player, clanByPlayer, location);
                                        }
                                    }

                                } else {
                                    Location location = new Location(world, x, y, z, yaw, pitch);

                                    if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                        if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                                || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                            TeleportUtils teleportUtils = new TeleportUtils();
                                            teleportUtils.teleportAsyncTimed(player, clanByPlayer, location);
                                        } else {
                                            TeleportUtils teleportUtils = new TeleportUtils();
                                            teleportUtils.teleportAsync(player, clanByPlayer, location);
                                        }
                                    } else {
                                        TeleportUtils teleportUtils = new TeleportUtils();
                                        teleportUtils.teleportAsync(player, clanByPlayer, location);
                                    }
                                }

                            } else {
                                homeCoolDownTimer.put(uuid, System.currentTimeMillis() + (clansConfig.getLong("clan-home.cool-down.time") * 1000));
                                Location location = new Location(world, x, y, z, yaw, pitch);

                                if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                    if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                            || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                        TeleportUtils teleportUtils = new TeleportUtils();
                                        teleportUtils.teleportAsyncTimed(player, clanByPlayer, location);
                                    } else {
                                        TeleportUtils teleportUtils = new TeleportUtils();
                                        teleportUtils.teleportAsync(player, clanByPlayer, location);
                                    }
                                } else {
                                    TeleportUtils teleportUtils = new TeleportUtils();
                                    teleportUtils.teleportAsync(player, clanByPlayer, location);
                                }
                            }

                        } else {
                            Location location = new Location(world, x, y, z, yaw, pitch);

                            if (clansConfig.getBoolean("clan-home.delay-before-teleport.enabled")) {
                                if (!(player.hasPermission("clanslite.bypass.homedelay") || player.hasPermission("clanslite.bypass.*")
                                        || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                    TeleportUtils teleportUtils = new TeleportUtils();
                                    teleportUtils.teleportAsyncTimed(player, clanByPlayer, location);
                                } else {
                                    TeleportUtils teleportUtils = new TeleportUtils();
                                    teleportUtils.teleportAsync(player, clanByPlayer, location);
                                }
                            } else {
                                TeleportUtils teleportUtils = new TeleportUtils();
                                teleportUtils.teleportAsync(player, clanByPlayer, location);
                            }
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-no-home-set"));
                    }

                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("failed-tp-not-in-clan"));
                }

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("function-disabled"));
            }
            return true;

        }
        return false;
    }

    private void fireAsyncClanHomePreTPEvent(Player player, Clan clan) {
        AsyncClanHomePreTeleportEvent asyncClanHomePreTeleportEvent = new AsyncClanHomePreTeleportEvent(true, player, clan);
        Bukkit.getPluginManager().callEvent(asyncClanHomePreTeleportEvent);
        asyncHomePreTeleportEvent = asyncClanHomePreTeleportEvent;
    }
}
