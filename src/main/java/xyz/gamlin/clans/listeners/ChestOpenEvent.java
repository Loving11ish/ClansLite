package xyz.gamlin.clans.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.models.Chest;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractUtils.StorageUtils;

public class ChestOpenEvent implements Listener {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    private static final String CLAN_PLACEHOLDER = "%CLAN%";

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event){
        if (!clansConfig.getBoolean("protections.chests.enabled")){
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Block block = event.getClickedBlock();
            if (block != null){
                if (block.getType().equals(Material.CHEST)){
                    Location chestLocation = block.getLocation();

                    if (!storageUtils.isChestLocked(chestLocation)){
                        return;
                    }

                    TileState tileState = (TileState) block.getState();
                    PersistentDataContainer container = tileState.getPersistentDataContainer();
                    String owningClanName = container.get(new NamespacedKey(Clans.getPlugin(), "owningClanName"), PersistentDataType.STRING);
                    Player player = event.getPlayer();
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());

                    Chest chest = storageUtils.getChestByLocation(chestLocation);

                    if (chest != null) {
                        if (!storageUtils.hasAccessToLockedChest(offlinePlayer, chest)) {
                            if (!(player.hasPermission("clanslite.bypass.chests") || player.hasPermission("clanslite.bypass.*")
                                    || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                                event.setCancelled(true);
                                if (owningClanName != null) {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chest-owned-by-another-clan")
                                            .replace(CLAN_PLACEHOLDER, owningClanName)));
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chest-owned-by-another-clan-name-unknown")));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
