package me.loving11ish.clans.commands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.UserMapStorageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClanAdmin implements CommandExecutor {

    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private final ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
    private static final String PLAYER_TO_KICK = "%KICKEDPLAYER%";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("save")) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("saving-clans-start"));
                    try {
                        if (!ClansStorageUtil.getRawClansList().isEmpty()) {
                            ClansStorageUtil.saveClans();
                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("save-failed-no-clans"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        MessageUtils.sendPlayer(player, messagesConfig.getString("clans-save-error-1"));
                        MessageUtils.sendPlayer(player, messagesConfig.getString("clans-save-error-2"));
                    }
                    MessageUtils.sendPlayer(player, messagesConfig.getString("save-completed"));
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("reload")) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("plugin-reload-begin"));
                    for (Player p : onlinePlayers) {
                        if (p.getName().equalsIgnoreCase(player.getName())) {
                            continue;
                        }
                        if (!onlinePlayers.isEmpty()) {
                            MessageUtils.sendPlayer(p, messagesConfig.getString("plugin-reload-broadcast-start"));
                        }
                    }
                    FoliaLib foliaLib = Clans.getFoliaLib();
                    Clans.getPlugin().onDisable();
                    foliaLib.getImpl().runLater(() ->
                            Bukkit.getPluginManager().getPlugin("ClansLite").onEnable(), 5L, TimeUnit.SECONDS);
                    foliaLib.getImpl().runLater(() -> {
                        Clans.getPlugin().reloadConfig();
                        ClanCommand.updateBannedTagsList();
                        Clans.getPlugin().messagesFileManager.reloadMessagesConfig();
                        Clans.getPlugin().clanGUIFileManager.reloadClanGUIConfig();
                        MessageUtils.sendPlayer(player, messagesConfig.getString("plugin-reload-successful"));
                        for (Player p : onlinePlayers) {
                            if (p.getName().equalsIgnoreCase(player.getName())) {
                                continue;
                            }
                            if (!onlinePlayers.isEmpty()) {
                                MessageUtils.sendPlayer(p, messagesConfig.getString("plugin-reload-successful"));
                            }
                        }
                    }, 5L, TimeUnit.SECONDS);
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("disband")) {
                    if (args.length == 2) {
                        if (args[1].length() > 1) {
                            Player onlinePlayerOwner = Bukkit.getPlayer(args[1]);
                            OfflinePlayer offlinePlayerOwner = UserMapStorageUtil.getBukkitOfflinePlayerByName(args[1]);
                            if (onlinePlayerOwner != null) {
                                try {
                                    if (ClansStorageUtil.deleteClan(onlinePlayerOwner)) {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-successfully-disbanded"));
                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-admin-disband-failure"));
                                    }
                                } catch (IOException e) {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("clans-update-error-1"));
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("clans-update-error-2"));
                                    e.printStackTrace();
                                }
                            } else if (offlinePlayerOwner != null) {
                                try {
                                    if (ClansStorageUtil.deleteOfflineClan(offlinePlayerOwner)) {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-successfully-disbanded"));
                                    } else {
                                        MessageUtils.sendPlayer(player, messagesConfig.getString("clan-admin-disband-failure"));
                                    }
                                } catch (IOException e) {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("clans-update-error-1"));
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("clans-update-error-2"));
                                    e.printStackTrace();
                                }
                            } else {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("could-not-find-specified-player").replace(PLAYER_TO_KICK, args[1]));
                            }
                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("incorrect-disband-command-usage"));
                        }
                    }
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("about")) {
                    MessageUtils.sendPlayer(player, "&3~~~~~~~~~~ &6&nClansLite&r &3~~~~~~~~~~");
                    MessageUtils.sendPlayer(player, "&3Version: &6" + Clans.getPlugin().getDescription().getVersion());
                    MessageUtils.sendPlayer(player, "&3Update available: &6" + Clans.getPlugin().isUpdateAvailable());
                    MessageUtils.sendPlayer(player, "&3Authors: &6" + Clans.getPlugin().getDescription().getAuthors());
                    MessageUtils.sendPlayer(player, "&3Description: &6" + Clans.getPlugin().getDescription().getDescription());
                    MessageUtils.sendPlayer(player, "&3Website: ");
                    MessageUtils.sendPlayer(player, "&6" + Clans.getPlugin().getDescription().getWebsite());
                    MessageUtils.sendPlayer(player, "&3Discord:");
                    MessageUtils.sendPlayer(player, "&6https://discord.gg/crapticraft");
                    MessageUtils.sendPlayer(player, "&3~~~~~~~~~~ &6&nClansLite&r &3~~~~~~~~~~");
                }

//----------------------------------------------------------------------------------------------------------------------
            } else {
                MessageUtils.sendPlayer(player, sendUsageMessage());
            }
            return true;
        }

//----------------------------------------------------------------------------------------------------------------------


//----------------------------------------------------------------------------------------------------------------------
        if (sender instanceof ConsoleCommandSender) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("save")) {
                    MessageUtils.sendConsole(messagesConfig.getString("saving-clans-start"));
                    try {
                        if (!ClansStorageUtil.getRawClansList().isEmpty()) {
                            ClansStorageUtil.saveClans();
                        } else {
                            MessageUtils.sendConsole(messagesConfig.getString("save-failed-no-clans"));
                        }
                    } catch (IOException e) {
                        MessageUtils.sendConsole(messagesConfig.getString("clans-save-error-1"));
                        MessageUtils.sendConsole(messagesConfig.getString("clans-save-error-2"));
                        e.printStackTrace();
                    }
                    MessageUtils.sendConsole(messagesConfig.getString("save-completed"));
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("reload")) {
                    MessageUtils.sendConsole(messagesConfig.getString("plugin-reload-begin"));
                    for (Player p : onlinePlayers) {
                        if (!onlinePlayers.isEmpty()) {
                            MessageUtils.sendPlayer(p, messagesConfig.getString("plugin-reload-broadcast-start"));
                        }
                    }
                    FoliaLib foliaLib = Clans.getFoliaLib();
                    Clans.getPlugin().onDisable();
                    foliaLib.getImpl().runLater(() ->
                            Bukkit.getPluginManager().getPlugin("ClansLite").onEnable(), 5L, TimeUnit.SECONDS);
                    foliaLib.getImpl().runLater(() -> {
                        Clans.getPlugin().reloadConfig();
                        ClanCommand.updateBannedTagsList();
                        Clans.getPlugin().messagesFileManager.reloadMessagesConfig();
                        Clans.getPlugin().clanGUIFileManager.reloadClanGUIConfig();
                        MessageUtils.sendConsole(messagesConfig.getString("plugin-reload-successful"));
                        for (Player p : onlinePlayers) {
                            if (!onlinePlayers.isEmpty()) {
                                MessageUtils.sendPlayer(p, messagesConfig.getString("plugin-reload-successful"));

                            }
                        }
                    }, 5L, TimeUnit.SECONDS);
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("disband")) {
                    if (args.length == 2) {
                        if (args[1].length() > 1) {
                            Player onlinePlayerOwner = Bukkit.getPlayer(args[1]);
                            OfflinePlayer offlinePlayerOwner = UserMapStorageUtil.getBukkitOfflinePlayerByName(args[1]);
                            if (onlinePlayerOwner != null) {
                                try {
                                    if (ClansStorageUtil.deleteClan(onlinePlayerOwner)) {
                                        MessageUtils.sendConsole(messagesConfig.getString("clan-successfully-disbanded"));
                                    } else {
                                        MessageUtils.sendConsole(messagesConfig.getString("clan-admin-disband-failure"));
                                    }
                                } catch (IOException e) {
                                    MessageUtils.sendConsole(messagesConfig.getString("clans-update-error-1"));
                                    MessageUtils.sendConsole(messagesConfig.getString("clans-update-error-2"));
                                    e.printStackTrace();
                                }
                            } else if (offlinePlayerOwner != null) {
                                try {
                                    if (ClansStorageUtil.deleteOfflineClan(offlinePlayerOwner)) {
                                        MessageUtils.sendConsole(messagesConfig.getString("clan-successfully-disbanded"));
                                    } else {
                                        MessageUtils.sendConsole(messagesConfig.getString("clan-admin-disband-failure"));
                                    }
                                } catch (IOException e) {
                                    MessageUtils.sendConsole(messagesConfig.getString("clans-update-error-1"));
                                    MessageUtils.sendConsole(messagesConfig.getString("clans-update-error-2"));
                                    e.printStackTrace();
                                }
                            } else {
                                MessageUtils.sendConsole(messagesConfig.getString("could-not-find-specified-player")
                                        .replace(PLAYER_TO_KICK, args[1]));
                            }
                        } else {
                            MessageUtils.sendConsole(messagesConfig.getString("incorrect-disband-command-usage"));
                        }
                    }
                }

//----------------------------------------------------------------------------------------------------------------------
                if (args[0].equalsIgnoreCase("about")) {
                    MessageUtils.sendConsole("&3~~~~~~~~~~ &6ClansLite &3~~~~~~~~~~");
                    MessageUtils.sendConsole("&3Version: &6" + Clans.getPlugin().getDescription().getVersion());
                    MessageUtils.sendConsole("&3Update available: &6" + Clans.getPlugin().isUpdateAvailable());
                    MessageUtils.sendConsole("&3Authors: &6" + Clans.getPlugin().getDescription().getAuthors());
                    MessageUtils.sendConsole("&3Description: &6" + Clans.getPlugin().getDescription().getDescription());
                    MessageUtils.sendConsole("&3Website: ");
                    MessageUtils.sendConsole("&6" + Clans.getPlugin().getDescription().getWebsite());
                    MessageUtils.sendConsole("&3Discord:");
                    MessageUtils.sendConsole("&6https://discord.gg/crapticraft");
                    MessageUtils.sendConsole("&3~~~~~~~~~~ &6ClansLite &3~~~~~~~~~~");
                }

//----------------------------------------------------------------------------------------------------------------------
            } else {
                MessageUtils.sendConsole(sendUsageMessage());
            }
        }
        return true;
    }

    private String sendUsageMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> configStringList = messagesConfig.getStringList("clanadmin-command-incorrect-usage");
        for (String string : configStringList) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
