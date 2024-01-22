package me.loving11ish.clans.commands.clanChestLockSubCommands;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Chest;
import me.loving11ish.clans.models.ClanPlayer;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.UserMapStorageUtil;

import java.util.List;

public class ChestAccessListSubCommand {

    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PLAYER_PLACEHOLDER = "%PLAYER%";

    public boolean chestAccessListSubCommand(CommandSender sender) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            Block block = player.getTargetBlockExact(5);

            if (block != null) {
                if (block.getType().equals(Material.CHEST)) {
                    Location location = block.getLocation();

                    if (ClansStorageUtil.isChestLocked(location)) {
                        Chest chest = ClansStorageUtil.getChestByLocation(location);

                        if (chest != null) {
                            if (ClansStorageUtil.hasAccessToLockedChest(offlinePlayer, chest)) {

                                List<OfflinePlayer> offlinePlayersWithAccess = ClansStorageUtil.getOfflinePlayersWithChestAccessByChest(chest);
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(messagesConfig.getString("players-with-access-list.header"));

                                for (OfflinePlayer offlinePlayerWithAccess : offlinePlayersWithAccess) {
                                    ClanPlayer clanPlayer = UserMapStorageUtil.getClanPlayerByBukkitOfflinePlayer(offlinePlayerWithAccess);
                                    if (clanPlayer != null) {
                                        String playerName = clanPlayer.getLastPlayerName();
                                        stringBuilder.append(messagesConfig.getString("players-with-access-list.player-entry")
                                                .replace(PLAYER_PLACEHOLDER, playerName));
                                    }
                                }

                                stringBuilder.append(messagesConfig.getString("players-with-access-list.footer"));
                                MessageUtils.sendPlayer(player, stringBuilder.toString());
                                return true;

                            } else {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("chest-owned-by-another-clan-name-unknown"));
                            }
                            return true;
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-chest-not-protected"));
                        return true;
                    }

                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("block-targeted-incorrect-material"));
                    return true;
                }

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("block-targeted-incorrect-material"));
                return true;
            }
        }
        return true;
    }
}
