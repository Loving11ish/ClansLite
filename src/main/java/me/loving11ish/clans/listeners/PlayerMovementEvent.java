package me.loving11ish.clans.listeners;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import me.loving11ish.clans.Clans;

import java.util.UUID;

public class PlayerMovementEvent implements Listener {

    private final FileConfiguration config = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!config.getBoolean("clan-home.delay-before-teleport.cancel-teleport-on-move")) {
            return;
        }

        if (event.getFrom().getX() != event.getTo().getX() && event.getFrom().getY() != event.getTo().getY() && event.getFrom().getZ() != event.getTo().getZ()) {
            if (Clans.getPlugin().teleportQueue.containsKey(uuid)) {
                MessageUtils.sendDebugConsole("Player " + player.getName() + " has a pending teleport");

                try {
                    WrappedTask wrappedTask = Clans.getPlugin().teleportQueue.get(uuid);
                    MessageUtils.sendDebugConsole("Wrapped task: " + wrappedTask.toString());

                    wrappedTask.cancel();

                    MessageUtils.sendDebugConsole("Wrapped task canceled");

                    Clans.getPlugin().teleportQueue.remove(uuid);

                    MessageUtils.sendDebugConsole("Player " + player.getName() + " has had teleport canceled and removed from queue");

                    MessageUtils.sendPlayer(player, messagesConfig.getString("timed-teleport-failed-player-moved"));

                } catch (Exception e) {
                    MessageUtils.sendConsole(messagesConfig.getString("move-event-cancel-failed"));
                    e.printStackTrace();
                }
            }
        }
    }
}
