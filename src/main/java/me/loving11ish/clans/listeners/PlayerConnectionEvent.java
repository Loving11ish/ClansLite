package me.loving11ish.clans.listeners;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.UserMapStorageUtil;

import java.util.UUID;

public class PlayerConnectionEvent implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Clans.connectedPlayers.put(player, player.getName());
        if (!(UserMapStorageUtil.isUserExisting(player))){
            UserMapStorageUtil.addToUserMap(player);
            return;
        }
        if (UserMapStorageUtil.hasPlayerNameChanged(player)){
            UserMapStorageUtil.updatePlayerName(player);
            MessageUtils.sendDebugConsole("Updated player name");
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onBedrockPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (Clans.getFloodgateApi() != null){
            if (Clans.getFloodgateApi().isFloodgatePlayer(uuid)){
                if (!(UserMapStorageUtil.isUserExisting(player))){
                    UserMapStorageUtil.addBedrockPlayerToUserMap(player);
                    return;
                }
                if (UserMapStorageUtil.hasPlayerNameChanged(player)){
                    UserMapStorageUtil.updatePlayerName(player);
                    MessageUtils.sendDebugConsole("Updated bedrock player name");
                }
                if (UserMapStorageUtil.hasBedrockPlayerJavaUUIDChanged(player)){
                    UserMapStorageUtil.updateBedrockPlayerJavaUUID(player);
                    MessageUtils.sendDebugConsole("Updated bedrock player Java UUID");
                }
                Clans.bedrockPlayers.put(player, Clans.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString());
                MessageUtils.sendDebugConsole("Added bedrock player to connected bedrock players hashmap");
            }
        }
    }
}
