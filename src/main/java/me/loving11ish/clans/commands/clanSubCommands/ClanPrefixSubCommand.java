package me.loving11ish.clans.commands.clanSubCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;

import java.util.*;

public class ClanPrefixSubCommand {

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    int MIN_CHAR_LIMIT = clansConfig.getInt("clan-tags.min-character-limit");
    int MAX_CHAR_LIMIT = clansConfig.getInt("clan-tags.max-character-limit");

    private final Set<Map.Entry<UUID, Clan>> clans = ClansStorageUtil.getClans();
    private final ArrayList<String> clansPrefixList = new ArrayList<>();

    public boolean clanPrefixSubCommand(CommandSender sender, String[] args, List<String> bannedTags) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            clans.forEach((clans) -> clansPrefixList.add(clans.getValue().getClanPrefix()));

            if (args.length == 2) {
                String prefixRaw = args[1];
                String prefixColorStripped = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefixRaw));

                // Check banned
                if (bannedTags.contains(prefixRaw)){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-prefix-is-banned").replace("%CLANPREFIX%", prefixRaw)));
                    return true;
                }

                // Check taken
                if (clansPrefixList.contains(prefixRaw)){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-prefix-already-taken").replace("%CLANPREFIX%", prefixRaw)));
                    return true;
                }

                // Check colors
                if (prefixRaw.contains("&")|| prefixRaw.contains("#") && !player.hasPermission("clanslite.clan.prefixcolors")){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-prefix-no-colours-permission")));
                    return true;
                }

                // Check owner
                if (ClansStorageUtil.isClanOwner(player)) {
                    // Check max length
                    if (prefixColorStripped.length() > MAX_CHAR_LIMIT) {
                        int maxCharLimit = clansConfig.getInt("clan-tags.max-character-limit");
                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-prefix-too-long").replace("%CHARMAX%", String.valueOf(maxCharLimit))));
                        clansPrefixList.clear();
                        return true;
                    }
                    // Check min length
                    else if (prefixColorStripped.length() < MIN_CHAR_LIMIT) {
                        int minCharLimit = clansConfig.getInt("clan-tags.min-character-limit");
                        sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-prefix-too-short").replace("%CHARMIN%", String.valueOf(minCharLimit))));
                        clansPrefixList.clear();
                        return true;
                    }
                    // Update prefix
                    else {
                        Clan playerClan = ClansStorageUtil.findClanByOwner(player);
                        ClansStorageUtil.updatePrefix(player, prefixRaw);
                        String prefixConfirmation = ColorUtils.translateColorCodes(messagesConfig.getString("clan-prefix-change-successful")).replace("%CLANPREFIX%", playerClan.getClanPrefix());
                        sender.sendMessage(prefixConfirmation);
                        clansPrefixList.clear();
                        return true;
                    }
                }
                else {
                    sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("must-be-owner-to-change-prefix")));
                    clansPrefixList.clear();
                    return true;
                }
            }
            else {
                sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("clan-invalid-prefix")));
                clansPrefixList.clear();
            }
            return true;

        }
        return false;
    }
}
