package xyz.gamlin.clans.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractUtils.UsermapUtils;

import java.util.UUID;
import java.util.logging.Logger;

public class PlayerConnectionEvent implements Listener {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    Logger logger = Clans.getPlugin().getLogger();

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
                logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aUpdated player name"));
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
                        logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aUpdated bedrock player name"));
                    }
                }
                if (usermapUtils.hasBedrockPlayerJavaUUIDChanged(player)){
                    usermapUtils.updateBedrockPlayerJavaUUID(player);
                    if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aUpdated bedrock player Java UUID"));
                    }
                }
                Clans.bedrockPlayers.put(player, Clans.getFloodgateApi().getPlayer(uuid).getJavaUniqueId().toString());
                if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aAdded bedrock player to connected bedrock players hashmap"));
                }
            }
        }
    }
}
