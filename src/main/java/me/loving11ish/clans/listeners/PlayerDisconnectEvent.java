package me.loving11ish.clans.listeners;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import me.loving11ish.clans.Clans;

public class PlayerDisconnectEvent implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Clans.connectedPlayers.remove(player);
        MessageUtils.sendDebugConsole("Player removed from connected players list");
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onBedrockPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (Clans.getFloodgateApi() != null){
            if (Clans.bedrockPlayers.containsKey(player)){
                Clans.bedrockPlayers.remove(player);
                MessageUtils.sendDebugConsole("Bedrock player removed from bedrock players list");
            }
        }
    }
}
