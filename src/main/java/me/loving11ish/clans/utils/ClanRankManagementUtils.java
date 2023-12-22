package me.loving11ish.clans.utils;

import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ClanRankManagementUtils {

    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();
    private static final FileConfiguration clansConfig = Clans.getPlugin().getConfig();

    public static boolean isPlayerClanManager(Clan clan, Player player) {
        return clan.getClanManager().equalsIgnoreCase(player.getName());
    }

    public static boolean isPlayerClanOfficer(Clan clan, Player player) {
        return clan.getClanOfficers().contains(player.getName());
    }

    public static boolean setClanManager(Clan clan, Player player) {
        if (isPlayerClanManager(clan, player)) {
            return false;
        }else {
            clan.setClanManager(player.getUniqueId().toString());
            return true;
        }
    }

    public static boolean addToClanOfficers(Clan clan, Player player) {
        if (isPlayerClanOfficer(clan, player)) {
            return false;
        }else {
            clan.getClanOfficers().add(player.getUniqueId().toString());
            return true;
        }
    }
}
