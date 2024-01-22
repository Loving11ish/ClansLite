package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanPvpToggleEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;

public class ClanPvpSubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    public boolean clanPvpSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (clansConfig.getBoolean("protections.pvp.pvp-command-enabled")) {
                if (ClansStorageUtil.isClanOwner(player)) {
                    if (ClansStorageUtil.findClanByOwner(player) != null) {

                        Clan clan = ClansStorageUtil.findClanByOwner(player);
                        if (clan.isFriendlyFireAllowed()) {
                            clan.setFriendlyFireAllowed(false);

                            foliaLib.getImpl().runAsync((task) -> {
                                fireAsyncClanPvpToggleEvent(player, clan, false);
                                MessageUtils.sendDebugConsole("Fired AsyncClanPvpToggleEvent. PvP toggle state: false");
                            });

                            MessageUtils.sendPlayer(player, messagesConfig.getString("disabled-friendly-fire"));
                        } else {
                            clan.setFriendlyFireAllowed(true);

                            foliaLib.getImpl().runAsync((task) -> {
                                fireAsyncClanPvpToggleEvent(player, clan, true);
                                MessageUtils.sendDebugConsole("Fired AsyncClanPvpToggleEvent. PvP toggle state: true");
                            });

                            MessageUtils.sendPlayer(player, messagesConfig.getString("enabled-friendly-fire"));
                        }

                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-in-clan"));
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

    private void fireAsyncClanPvpToggleEvent(Player player, Clan clan, boolean pvpToggleState) {
        AsyncClanPvpToggleEvent event = new AsyncClanPvpToggleEvent(true, player, clan, pvpToggleState);
        Bukkit.getPluginManager().callEvent(event);
    }
}
