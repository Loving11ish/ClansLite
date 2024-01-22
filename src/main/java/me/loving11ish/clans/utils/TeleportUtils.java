package me.loving11ish.clans.utils;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.papermc.lib.PaperLib;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.events.AsyncClanHomeTeleportEvent;
import me.loving11ish.clans.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TeleportUtils {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    public WrappedTask wrappedTask;

    private final FileConfiguration config = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    public void teleportAsync(Player player, Clan clan, Location location) {
        Location originLocation = player.getLocation();
        PaperLib.teleportAsync(player, location);

        foliaLib.getImpl().runAsync((task) -> {
            fireAsyncClanHomeTeleportEvent(player, clan, originLocation, location);
            MessageUtils.sendDebugConsole("Fired AsyncClanHomeTeleportEvent");
        });

        MessageUtils.sendPlayer(player, messagesConfig.getString("non-timed-teleporting-complete"));
    }

    public void teleportAsyncTimed(Player player, Clan clan, Location location) {
        Location originLocation = player.getLocation();
        MessageUtils.sendPlayer(player, messagesConfig.getString("timed-teleporting-begin-tp"));

        wrappedTask = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = config.getInt("clan-home.delay-before-teleport.time");
            @Override
            public void run() {
                if (!Clans.getPlugin().teleportQueue.containsKey(player.getUniqueId())){
                    Clans.getPlugin().teleportQueue.put(player.getUniqueId(), getWrappedTask());
                    MessageUtils.sendDebugConsole("Player " + player.getName() + " has been added to teleport queue");
                }

                if (time == 0) {
                    Clans.getPlugin().teleportQueue.remove(player.getUniqueId());
                    MessageUtils.sendDebugConsole("Player " + player.getName() + " has been removed from the teleport queue");

                    PaperLib.teleportAsync(player, location);

                    MessageUtils.sendPlayer(player, messagesConfig.getString("timed-teleporting-complete"));

                    foliaLib.getImpl().runAsync((task) -> {
                        fireAsyncClanHomeTeleportEvent(player, clan, originLocation, location);
                        MessageUtils.sendDebugConsole("Fired AsyncClanHomeTeleportEvent");
                    });

                    getWrappedTask().cancel();
                    MessageUtils.sendDebugConsole("Wrapped task: " + getWrappedTask().toString());
                    MessageUtils.sendDebugConsole("teleportPlayerAsyncTimed task canceled");

                    return;
                }else {
                    time --;
                    MessageUtils.sendDebugConsole("teleportPlayerAsyncTimed task running");
                    MessageUtils.sendDebugConsole("Wrapped task: " + getWrappedTask().toString());
                    MessageUtils.sendDebugConsole("Time: " + time);
                    MessageUtils.sendDebugConsole("Player name: " + player.getName());
                    MessageUtils.sendDebugConsole("Clan name: " + clan.getClanFinalName());
                    MessageUtils.sendDebugConsole("Home location: " + location.toString());
                }
            }
        }, 0, 1L, TimeUnit.SECONDS);
    }

    public WrappedTask getWrappedTask() {
        return wrappedTask;
    }

    private void fireAsyncClanHomeTeleportEvent(Player player, Clan clan, Location originLocation, Location homeLocation) {
        AsyncClanHomeTeleportEvent homeTeleportEvent = new AsyncClanHomeTeleportEvent(true, player, clan, originLocation, homeLocation);
        Bukkit.getPluginManager().callEvent(homeTeleportEvent);
    }
}
