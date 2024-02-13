package me.loving11ish.clans.commands.clanSubCommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanPrefixChangeEvent;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;

import java.util.*;

public class ClanPrefixSubCommand {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    int MIN_CHAR_LIMIT = clansConfig.getInt("clan-tags.min-character-limit");
    int MAX_CHAR_LIMIT = clansConfig.getInt("clan-tags.max-character-limit");

    private final Set<Map.Entry<UUID, Clan>> clans = ClansStorageUtil.getClans();
    private final ArrayList<String> clansPrefixList = new ArrayList<>();

    public boolean clanPrefixSubCommand(CommandSender sender, String[] args, List<String> bannedTags) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            clans.forEach((clans) ->
                    clansPrefixList.add(clans.getValue().getClanPrefix()));

            if (args.length == 2) {

                String prefixRaw = args[1];
                String prefixColorStripped = ClansStorageUtil.stripClanPrefixColorCodes(prefixRaw);

                if (bannedTags.contains(prefixRaw)) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-prefix-is-banned").replace("%CLANPREFIX%", prefixRaw));
                    return true;
                }

                if (clansPrefixList.contains(prefixRaw)) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-prefix-already-taken").replace("%CLANPREFIX%", prefixRaw));
                    return true;
                }

                if ((prefixRaw.contains("&") || prefixRaw.contains("#")) && !player.hasPermission("clanslite.clan.prefixcolors")) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-prefix-no-colours-permission"));
                    return true;
                }

                if (ClansStorageUtil.isClanOwner(player)) {

                    if (prefixColorStripped.length() > MAX_CHAR_LIMIT) {
                        int maxCharLimit = clansConfig.getInt("clan-tags.max-character-limit");
                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-prefix-too-long").replace("%CHARMAX%", String.valueOf(maxCharLimit)));
                        clansPrefixList.clear();
                        return true;
                    }

                    else if (prefixColorStripped.length() < MIN_CHAR_LIMIT) {
                        int minCharLimit = clansConfig.getInt("clan-tags.min-character-limit");
                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-prefix-too-short").replace("%CHARMIN%", String.valueOf(minCharLimit)));
                        clansPrefixList.clear();
                        return true;
                    }

                    else {
                        Clan playerClan = ClansStorageUtil.findClanByOwner(player);

                        foliaLib.getImpl().runAsync((task) -> {
                            fireAsyncClanPrefixChangeEvent(player, playerClan, playerClan.getClanPrefix(), prefixRaw);
                            MessageUtils.sendDebugConsole("Fired AsyncClanPrefixChangeEvent");
                        });

                        ClansStorageUtil.updatePrefix(player, prefixRaw);
                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-prefix-change-successful").replace("%CLANPREFIX%", prefixRaw));

                        clansPrefixList.clear();
                        return true;

                    }

                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("must-be-owner-to-change-prefix"));
                    clansPrefixList.clear();
                    return true;
                }

            } else {
                MessageUtils.sendPlayer(player, messagesConfig.getString("clan-invalid-prefix"));
                clansPrefixList.clear();
            }
            return true;

        }
        return false;
    }

    private void fireAsyncClanPrefixChangeEvent(Player createdBy, Clan clan, String oldPrefix, String newPrefix) {
        AsyncClanPrefixChangeEvent event = new AsyncClanPrefixChangeEvent(true, createdBy, clan, oldPrefix, newPrefix);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
