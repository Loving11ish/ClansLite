package xyz.gamlin.clans.commands.clanChestLockSubCommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.gamlin.clans.Clans;
import xyz.gamlin.clans.models.Chest;
import xyz.gamlin.clans.models.ClanPlayer;
import xyz.gamlin.clans.utils.*;
import xyz.gamlin.clans.utils.abstractUtils.StorageUtils;
import xyz.gamlin.clans.utils.abstractUtils.UsermapUtils;

import java.util.List;

public class ChestAccessListSubCommand {

    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;
    private UsermapUtils usermapUtils = Clans.getPlugin().usermapUtils;

    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";

    public boolean chestAccessListSubCommand(CommandSender sender){

        if (sender instanceof Player player){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            Block block = player.getTargetBlockExact(5);
            if (block != null){
                if (block.getType().equals(Material.CHEST)){
                    Location location = block.getLocation();
                    if (storageUtils.isChestLocked(location)){
                        Chest chest = storageUtils.getChestByLocation(location);
                        if (chest != null){
                            if (storageUtils.hasAccessToLockedChest(offlinePlayer, chest)){
                                List<OfflinePlayer> offlinePlayersWithAccess = storageUtils.getOfflinePlayersWithChestAccessByChest(chest);
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(messagesConfig.getString("players-with-access-list.header"));
                                for (OfflinePlayer offlinePlayerWithAccess : offlinePlayersWithAccess){
                                    ClanPlayer clanPlayer = usermapUtils.getClanPlayerByBukkitOfflinePlayer(offlinePlayerWithAccess);
                                    if (clanPlayer != null){
                                        String playerName = clanPlayer.getLastPlayerName();
                                        stringBuilder.append(messagesConfig.getString("players-with-access-list.player-entry")
                                                .replace(PLAYER_PLACEHOLDER, playerName));
                                    }
                                }
                                stringBuilder.append(messagesConfig.getString("players-with-access-list.footer"));
                                player.sendMessage(ColorUtils.translateColorCodes(stringBuilder.toString()));
                                return true;
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("chest-owned-by-another-clan-name-unknown")));
                            }
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
}
