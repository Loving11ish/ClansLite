package me.loving11ish.clans.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.abstractClasses.UsermapUtils;

import java.util.UUID;

public class PlayerConnectionEvent implements Listener {
    
    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();

    private UsermapUtils usermapUtils = Clans.getPlugin().usermapUtils;

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Clans.connectedPlayers.put(player, player.getName());
        if (!(usermapUtils.isUserExisting(player))){
            usermapUtils.addToUsermap(player);
            return;
        }
        if (usermapUtils.hasPlayerNameChanged(player)){
            usermapUtils.updatePlayerName(player);
            if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aUpdated player name"));
            }
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onBedrockPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (Clans.getFloodgateApi() != null){
            if (Clans.getFloodgateApi().isFloodgatePlayer(uuid)){
                if (!(usermapUtils.isUserExisting(player))){
                    usermapUtils.addBedrockPlayerToUsermap(player);
                    return;
                }
                if (usermapUtils.hasPlayerNameChanged(player)){
                    usermapUtils.updatePlayerName(player);
                    if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aUpdated bedrock player name"));
                    }
                }
                if (usermapUtils.hasBedrockPlayerJavaUUIDChanged(player)){
                    usermapUtils.updateBedrockPlayerJavaUUID(player);
                    if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aUpdated bedrock player Java UUID"));
                    }
                }
                Clans.bedrockPlayers.put(player, Clans.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString());
                if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aAdded bedrock player to connected bedrock players hashmap"));
                }
            }
        }
    }
}
