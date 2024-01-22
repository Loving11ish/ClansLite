package me.loving11ish.clans.commands.clanChestLockSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.events.AsyncChestLockEvent;
import me.loving11ish.clans.models.Chest;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;

public class ChestLockSubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String LIMIT_PLACEHOLDER = "%LIMIT%";
    private static final String X_PLACEHOLDER = "%X%";
    private static final String Y_PLACEHOLDER = "%Y%";
    private static final String Z_PLACEHOLDER = "%Z%";

    public boolean clanChestLockSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Block block = player.getTargetBlockExact(5);
            Clan clanByOwner = ClansStorageUtil.findClanByOwner(player);
            Clan clanByPlayer = ClansStorageUtil.findClanByPlayer(player);

            if (clanByOwner != null) {
                if (block != null) {
                    if (block.getType().equals(Material.CHEST)) {

                        int maxAllowedChests = clanByOwner.getMaxAllowedProtectedChests();

                        if (ClansStorageUtil.getAllProtectedChestsByClan(clanByOwner).size() >= maxAllowedChests) {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("chest-max-amount-reached")
                                    .replace(LIMIT_PLACEHOLDER, String.valueOf(maxAllowedChests)));
                            return true;
                        }

                        lockTargetedChest(clanByOwner, block, player);

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("block-targeted-incorrect-material"));
                        return true;
                    }

                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("block-targeted-incorrect-material"));
                    return true;
                }

            } else {
                if (clanByPlayer != null) {
                    if (block != null) {
                        if (block.getType().equals(Material.CHEST)) {

                            int maxAllowedChests = clanByPlayer.getMaxAllowedProtectedChests();
                            if (ClansStorageUtil.getAllProtectedChestsByClan(clanByPlayer).size() >= maxAllowedChests) {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("chest-max-amount-reached")
                                        .replace(LIMIT_PLACEHOLDER, String.valueOf(maxAllowedChests)));
                                return true;
                            }

                            lockTargetedChest(clanByPlayer, block, player);

                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("block-targeted-incorrect-material"));
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("block-targeted-incorrect-material"));
                    }
                    return true;
                }
            }

            if (clanByOwner == null || clanByPlayer == null) {
                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-in-clan"));
                return true;
            }
        }
        return true;
    }

    private void lockTargetedChest(Clan clan, Block block, Player player) {
        Location location = block.getLocation();

        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());

        if (ClansStorageUtil.addProtectedChest(clan, location, player)) {

            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chest-protected-successfully")
                    .replace(X_PLACEHOLDER, String.valueOf(x))
                    .replace(Y_PLACEHOLDER, String.valueOf(y))
                    .replace(Z_PLACEHOLDER, String.valueOf(z))));

            TileState tileState = (TileState) block.getState();
            PersistentDataContainer container = tileState.getPersistentDataContainer();

            container.set(new NamespacedKey(Clans.getPlugin(), "owningClanName"), PersistentDataType.STRING, clan.getClanFinalName());
            container.set(new NamespacedKey(Clans.getPlugin(), "owningClanOwnerUUID"), PersistentDataType.STRING, clan.getClanOwner());

            tileState.update();

            foliaLib.getImpl().runAsync((task) -> {
                fireAsyncChestLockEvent(player, clan, ClansStorageUtil.getChestByLocation(location), location);
                MessageUtils.sendDebugConsole("Fired AsyncChestLockEvent");
            });
        }
    }

    private static void fireAsyncChestLockEvent(Player player, Clan clan, Chest chest, Location chestLocation) {
        AsyncChestLockEvent asyncChestLockEvent = new AsyncChestLockEvent(true, player, clan, chest, chestLocation);
        Bukkit.getPluginManager().callEvent(asyncChestLockEvent);
    }
}
