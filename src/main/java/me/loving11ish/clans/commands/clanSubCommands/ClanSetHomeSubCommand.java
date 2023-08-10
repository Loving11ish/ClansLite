package me.loving11ish.clans.commands.clanSubCommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.api.ClanHomeCreateEvent;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.abstractClasses.StorageUtils;

public class ClanSetHomeSubCommand {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private StorageUtils storageUtils = Clans.getPlugin().storageUtils;

    public boolean setClanHomeSubCommand(CommandSender sender) {
        if (sender instanceof Player player) {
            if (clansConfig.getBoolean("clan-home.enabled")){
                if (storageUtils.isClanOwner(player)){
                    if (storageUtils.findClanByOwner(player) != null){
                        Clan clan = storageUtils.findClanByOwner(player);
                        Location location = player.getLocation();
                        fireClanHomeSetEvent(player, clan, location);
                        if (clansConfig.getBoolean("general.developer-debug-mode.enabled")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aFired ClanHomeSetEvent"));
                        }
                        clan.setClanHomeWorld(player.getLocation().getWorld().getName());
                        clan.setClanHomeX(player.getLocation().getX());
                        clan.setClanHomeY(player.getLocation().getY());
                        clan.setClanHomeZ(player.getLocation().getZ());
                        clan.setClanHomeYaw(player.getLocation().getYaw());
                        clan.setClanHomePitch(player.getLocation().getPitch());
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("successfully-set-clan-home")));
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-must-be-owner")));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("function-disabled")));
            }
            return true;

        }
        return false;
    }

    private static void fireClanHomeSetEvent(Player player, Clan clan, Location homeLocation) {
        ClanHomeCreateEvent clanHomeCreateEvent = new ClanHomeCreateEvent(player, clan, homeLocation);
        Bukkit.getPluginManager().callEvent(clanHomeCreateEvent);
    }
}
