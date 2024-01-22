package me.loving11ish.clans.updatesystem;

import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinEvent implements Listener {

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();
    private final List<UUID> notifiedPlayerUUID = new ArrayList<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("clanslite.update")||player.hasPermission("clanslite.*")||player.isOp()) {
            if (clansConfig.getBoolean("plugin-update-notifications.enabled")){
                if (!notifiedPlayerUUID.contains(player.getUniqueId())){
                    new UpdateChecker(97163).getVersion(version -> {
                        try {
                            if (!(Clans.getPlugin().getDescription().getVersion().equalsIgnoreCase(version))) {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("update-available.1"));
                                MessageUtils.sendPlayer(player, messagesConfig.getString("update-available.2"));
                                MessageUtils.sendPlayer(player, messagesConfig.getString("update-available.3"));
                                notifiedPlayerUUID.add(player.getUniqueId());
                            }
                        }catch (NullPointerException e){
                            MessageUtils.sendPlayer(player, messagesConfig.getString("Update-check-failure"));
                        }
                    });
                }
            }
        }
    }
}
