package xyz.gamlin.clans.commands.clanChestLockSubCommands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.api.ChestUnlockEvent;
import xyz.gamlin.clans.models.Chest;
import xyz.gamlin.clans.utils.ColorUtils;
import xyz.gamlin.clans.utils.abstractClasses.StorageUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class ChestUnlockSubCommand {

    Logger logger = Clans.getPlugin().getLogger();

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    private static final String X_PLACEHOLDER = "%X%";
    private static final String Y_PLACEHOLDER = "%Y%";
    private static final String Z_PLACEHOLDER = "%Z%";

    public boolean chestUnlockSubCommand(CommandSender sender){

        if (sender instanceof Player player){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            Block block = player.getTargetBlockExact(5);
            if (block != null){
                if (block.getType().equals(Material.CHEST)){
                    Location location = block.getLocation();
                    int x = (int) Math.round(location.getX());
                    int y = (int) Math.round(location.getY());
                    int z = (int) Math.round(location.getZ());

                    if (storageUtils.isChestLocked(location)){
                        Chest chest = storageUtils.getChestByLocation(location);
                        if (storageUtils.hasAccessToLockedChest(offlinePlayer, chest)){

                            TileState tileState = (TileState) block.getState();
                            PersistentDataContainer container = tileState.getPersistentDataContainer();
                            String clanOwnerUUIDString = container.get(new NamespacedKey(Clans.getPlugin(), "owningClanOwnerUUID"), PersistentDataType.STRING);
                            if (clanOwnerUUIDString != null){

                                try {
                                    if (storageUtils.removeProtectedChest(clanOwnerUUIDString, location, player)){
                                        fireChestUnlockEvent(player, location);
                                        if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                                            logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ChestUnlockEvent"));
                                        }
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chest-protection-removed-successfully")
                                                .replace(X_PLACEHOLDER, String.valueOf(x))
                                                .replace(Y_PLACEHOLDER, String.valueOf(y))
                                                .replace(Z_PLACEHOLDER, String.valueOf(z))));
                                        container.remove(new NamespacedKey(Clans.getPlugin(), "owningClanName"));
                                        container.remove(new NamespacedKey(Clans.getPlugin(), "owningClanOwnerUUID"));
                                        tileState.update();
                                        return true;
                                    }
                                }catch (IOException e){
                                    logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("clans-update-error-1")));
                                    logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("clans-update-error-2")));
                                    e.printStackTrace();
                                }
                                return true;
                            }

                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chest-owned-by-another-clan-name-unknown")));
                            return true;
                        }
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("failed-chest-not-protected")));
                        return true;
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("block-targeted-incorrect-material")));
                    return true;
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("block-targeted-incorrect-material")));
                return true;
            }
        }
        return true;
    }

    private static void fireChestUnlockEvent(Player player, Location removedLockLocation){
        ChestUnlockEvent chestUnlockEvent = new ChestUnlockEvent(player, removedLockLocation);
        Bukkit.getPluginManager().callEvent(chestUnlockEvent);
    }
}
