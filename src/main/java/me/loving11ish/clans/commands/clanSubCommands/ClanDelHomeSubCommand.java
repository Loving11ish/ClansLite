package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanHomeDeleteEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;

public class ClanDelHomeSubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    public boolean deleteClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (clansConfig.getBoolean("clan-home.enabled")) {
                if (ClansStorageUtil.findClanByOwner(player) != null) {

                    Clan clanByOwner = ClansStorageUtil.findClanByOwner(player);

                    if (ClansStorageUtil.isHomeSet(clanByOwner)) {
                        Location homeLocation = new Location(Bukkit.getWorld(clanByOwner.getClanHomeWorld()),
                                clanByOwner.getClanHomeX(),
                                clanByOwner.getClanHomeY(),
                                clanByOwner.getClanHomeZ(),
                                clanByOwner.getClanHomeYaw(),
                                clanByOwner.getClanHomePitch());

                        foliaLib.getImpl().runAsync((task) -> {
                            fireAsyncClanHomeDeleteEvent(player, clanByOwner, homeLocation);
                            MessageUtils.sendDebugConsole("Fired AsyncClanHomeDeleteEvent");
                        });

                        ClansStorageUtil.deleteHome(clanByOwner);
                        MessageUtils.sendPlayer(player, messagesConfig.getString("successfully-deleted-clan-home"));

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-no-home-set"));
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

    private void fireAsyncClanHomeDeleteEvent(Player player, Clan clan, Location homeLocation) {
        AsyncClanHomeDeleteEvent asyncClanHomeDeleteEvent = new AsyncClanHomeDeleteEvent(true, player, clan, homeLocation);
        Bukkit.getPluginManager().callEvent(asyncClanHomeDeleteEvent);
    }
}
