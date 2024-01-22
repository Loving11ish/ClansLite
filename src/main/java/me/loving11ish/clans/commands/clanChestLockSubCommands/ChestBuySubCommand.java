package me.loving11ish.clans.commands.clanChestLockSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncChestBuyEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;

public class ChestBuySubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private final int purchasePrice = clansConfig.getInt("protections.chests.clan-points-purchase-value");
    private static final String AMOUNT_PLACEHOLDER = "%AMOUNT%";

    public boolean chestBuySubCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 1) {
                if (args[1] != null) {

                    int amountOfChests = Integer.parseInt(args[1]);

                    if (amountOfChests != 0) {
                        Clan clanByOwner = ClansStorageUtil.findClanByOwner(player);
                        Clan clanByPlayer = ClansStorageUtil.findClanByPlayer(player);

                        if (clanByOwner != null) {
                            addNewChestLock(clanByOwner, player, amountOfChests);
                            return true;
                        } else if (clanByPlayer != null) {
                            addNewChestLock(clanByPlayer, player, amountOfChests);
                            return true;
                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-in-clan"));
                            return true;
                        }
                    }
                }
            } else {

                if (args.length == 1) {
                    Clan clanByOwner = ClansStorageUtil.findClanByOwner(player);
                    Clan clanByPlayer = ClansStorageUtil.findClanByPlayer(player);

                    if (clanByOwner != null) {
                        addNewChestLock(clanByOwner, player);
                        return true;
                    } else if (clanByPlayer != null) {
                        addNewChestLock(clanByPlayer, player);
                        return true;
                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-in-clan"));
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private void addNewChestLock(Clan clan, Player player, int amountOfChests) {
        int maxAllowedChests = clan.getMaxAllowedProtectedChests();

        if (ClansStorageUtil.hasEnoughPoints(clan, purchasePrice * maxAllowedChests)) {
            if (ClansStorageUtil.withdrawPoints(clan, purchasePrice * maxAllowedChests)) {

                clan.setMaxAllowedProtectedChests(maxAllowedChests + amountOfChests);

                MessageUtils.sendPlayer(player, messagesConfig.getString("chest-purchased-successfully")
                        .replace(AMOUNT_PLACEHOLDER, String.valueOf(amountOfChests)));

                foliaLib.getImpl().runAsync((task) -> {
                    fireAsyncChestBuyEvent(player, clan, maxAllowedChests, maxAllowedChests + amountOfChests);
                    MessageUtils.sendDebugConsole("&aFired AsyncChestBuyEvent");
                });

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-enough-points")
                        .replace(AMOUNT_PLACEHOLDER, String.valueOf(amountOfChests)));
            }
        } else {
            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-enough-points")
                    .replace(AMOUNT_PLACEHOLDER, String.valueOf(amountOfChests)));
        }
    }

    private void addNewChestLock(Clan clan, Player player) {
        int maxAllowedChests = clan.getMaxAllowedProtectedChests();

        if (ClansStorageUtil.hasEnoughPoints(clan, purchasePrice * maxAllowedChests)) {
            if (ClansStorageUtil.withdrawPoints(clan, purchasePrice * maxAllowedChests)) {

                clan.setMaxAllowedProtectedChests(maxAllowedChests + 1);

                MessageUtils.sendPlayer(player, messagesConfig.getString("chest-purchased-successfully")
                        .replace(AMOUNT_PLACEHOLDER, String.valueOf(1)));

                foliaLib.getImpl().runAsync((task) -> {
                    fireAsyncChestBuyEvent(player, clan, maxAllowedChests, maxAllowedChests + 1);
                    MessageUtils.sendDebugConsole("&aFired AsyncChestBuyEvent");
                });

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-enough-points")
                        .replace(AMOUNT_PLACEHOLDER, String.valueOf(1)));
            }
        } else {
            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-not-enough-points")
                    .replace(AMOUNT_PLACEHOLDER, String.valueOf(1)));
        }
    }

    private void fireAsyncChestBuyEvent(Player player, Clan clan, int oldClanMaxAllowedChests, int newChestCount) {
        AsyncChestBuyEvent asyncChestBuyEvent = new AsyncChestBuyEvent(true, player, clan, oldClanMaxAllowedChests, newChestCount);
        Bukkit.getPluginManager().callEvent(asyncChestBuyEvent);
    }
}
