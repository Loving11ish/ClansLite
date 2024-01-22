package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanHomeCreateEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;

public class ClanSetHomeSubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    public boolean setClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (clansConfig.getBoolean("clan-home.enabled")) {
                if (ClansStorageUtil.isClanOwner(player)) {
                    if (ClansStorageUtil.findClanByOwner(player) != null) {

                        Clan clan = ClansStorageUtil.findClanByOwner(player);
                        Location location = player.getLocation();

                        foliaLib.getImpl().runAsync((task) -> {
                            fireAsyncClanHomeSetEvent(player, clan, location);
                            MessageUtils.sendDebugConsole("Fired AsyncClanHomeSetEvent");
                        });

                        clan.setClanHomeWorld(player.getLocation().getWorld().getName());
                        clan.setClanHomeX(player.getLocation().getX());
                        clan.setClanHomeY(player.getLocation().getY());
                        clan.setClanHomeZ(player.getLocation().getZ());
                        clan.setClanHomeYaw(player.getLocation().getYaw());
                        clan.setClanHomePitch(player.getLocation().getPitch());

                        MessageUtils.sendPlayer(player, messagesConfig.getString("successfully-set-clan-home"));
                    }

                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-must-be-owner"));
                }

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("function-disabled"));
            }
            return true;

        }
        return false;
    }

    private static void fireAsyncClanHomeSetEvent(Player player, Clan clan, Location homeLocation) {
        AsyncClanHomeCreateEvent asyncClanHomeCreateEvent = new AsyncClanHomeCreateEvent(true, player, clan, homeLocation);
        Bukkit.getPluginManager().callEvent(asyncClanHomeCreateEvent);
    }
}
