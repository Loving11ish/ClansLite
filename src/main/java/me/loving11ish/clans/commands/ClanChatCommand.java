package me.loving11ish.clans.commands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.clans.api.events.AsyncClanChatMessageSendEvent;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.models.ClanPlayer;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.UserMapStorageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClanChatCommand implements CommandExecutor {

    private final FoliaLib foliaLib = Clans.getFoliaLib();

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String TIME_LEFT = "%TIMELEFT%";
    private static final String CLAN_PLACEHOLDER = "%CLAN%";

    private final HashMap<UUID, Long> chatCoolDownTimer = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (!(clansConfig.getBoolean("clan-chat.enabled"))) {
                MessageUtils.sendPlayer(player, messagesConfig.getString("function-disabled"));
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(ColorUtils.translateColorCodes(sendUsage()));
                return true;

            } else {
                ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
                ArrayList<Player> playersWithSpyPerms = new ArrayList<>();
                for (Player p : onlinePlayers) {
                    ClanPlayer clanPlayer = UserMapStorageUtil.getClanPlayerByBukkitPlayer(p);
                    if (clanPlayer.getCanChatSpy() && p.hasPermission("clanslite.chat.spy")) {
                        playersWithSpyPerms.add(p);
                    }
                }

                Clan clanByMember = ClansStorageUtil.findClanByPlayer(player);
                Clan clanByOwner = ClansStorageUtil.findClanByOwner(player);

                String chatSpyPrefix = clansConfig.getString("clan-chat.chat-spy.chat-spy-prefix");
                StringBuilder messageString = new StringBuilder();
                messageString.append(clansConfig.getString("clan-chat.chat-prefix")).append(" ");
                messageString.append("&d").append(player.getName()).append(":&r").append(" ");
                for (String arg : args) {
                    messageString.append(arg).append(" ");
                }

                if (clansConfig.getBoolean("clan-chat.cool-down.enabled")) {
                    if (chatCoolDownTimer.containsKey(uuid)) {
                        if (!(player.hasPermission("clanslite.bypass.chatcooldown") || player.hasPermission("clanslite.bypass.*")
                                || player.hasPermission("clanslite.bypass") || player.hasPermission("clanslite.*") || player.isOp())) {
                            if (chatCoolDownTimer.get(uuid) > System.currentTimeMillis()) {

                                //If player still has time left on cool down
                                Long timeLeft = (chatCoolDownTimer.get(uuid) - System.currentTimeMillis()) / 1000;
                                MessageUtils.sendPlayer(player, messagesConfig.getString("home-cool-down-timer-wait")
                                        .replace(TIME_LEFT, timeLeft.toString()));
                            } else {

                                //Add player to cool down and run message
                                chatCoolDownTimer.put(uuid, System.currentTimeMillis() + (clansConfig.getLong("clan-chat.cool-down.time") * 1000));
                                if (clanByMember != null) {
                                    ArrayList<String> playerClanMembers = clanByMember.getClanMembers();

                                    foliaLib.getImpl().runAsync((task) -> {
                                        fireAsyncClanChatMessageSendEvent(player, clanByMember, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                                        MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                                    });

                                    for (String playerClanMember : playerClanMembers) {
                                        if (playerClanMember != null) {

                                            UUID memberUUID = UUID.fromString(playerClanMember);
                                            UUID ownerUUID = UUID.fromString(clanByMember.getClanOwner());
                                            Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                            Player playerClanOwner = Bukkit.getPlayer(ownerUUID);

                                            if (playerClanPlayer != null) {

                                                if (playerClanOwner != null) {
                                                    MessageUtils.sendPlayer(playerClanOwner, ColorUtils.translateColorCodes(messageString.toString()).replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                                }

                                                MessageUtils.sendPlayer(playerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));

                                                if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                                    for (Player p : playersWithSpyPerms) {
                                                        MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                                    }
                                                }
                                                return true;
                                            }
                                        }
                                    }
                                }

                                if (clanByOwner != null) {
                                    ArrayList<String> ownerClanMembers = clanByOwner.getClanMembers();

                                    foliaLib.getImpl().runAsync((task) -> {
                                        fireAsyncClanChatMessageSendEvent(player, clanByOwner, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                                        MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                                    });

                                    for (String ownerClanMember : ownerClanMembers) {
                                        if (ownerClanMember != null) {
                                            UUID memberUUID = UUID.fromString(ownerClanMember);
                                            Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);

                                            if (ownerClanPlayer != null) {

                                                MessageUtils.sendPlayer(ownerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                                MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));

                                                if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                                    for (Player p : playersWithSpyPerms) {
                                                        MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                                    }
                                                }
                                                return true;
                                            }
                                        }
                                    }
                                    MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                } else {
                                    MessageUtils.sendPlayer(player, messagesConfig.getString("failed-must-be-in-clan"));
                                }
                            }
                        } else {

                            //If player has cool down bypass
                            if (clanByMember != null) {
                                ArrayList<String> playerClanMembers = clanByMember.getClanMembers();

                                foliaLib.getImpl().runAsync((task) -> {
                                    fireAsyncClanChatMessageSendEvent(player, clanByMember, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                                    MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                                });

                                for (String playerClanMember : playerClanMembers) {
                                    if (playerClanMember != null) {

                                        UUID memberUUID = UUID.fromString(playerClanMember);
                                        UUID ownerUUID = UUID.fromString(clanByMember.getClanOwner());
                                        Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                        Player playerClanOwner = Bukkit.getPlayer(ownerUUID);

                                        if (playerClanPlayer != null) {

                                            if (playerClanOwner != null) {
                                                MessageUtils.sendPlayer(playerClanOwner, messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                            }

                                            MessageUtils.sendPlayer(playerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));

                                            if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                                for (Player p : playersWithSpyPerms) {
                                                    MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                                }
                                            }
                                            return true;
                                        }
                                    }
                                }
                            }

                            if (clanByOwner != null) {
                                ArrayList<String> ownerClanMembers = clanByOwner.getClanMembers();

                                foliaLib.getImpl().runAsync((task) -> {
                                    fireAsyncClanChatMessageSendEvent(player, clanByOwner, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                                    MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                                });

                                for (String ownerClanMember : ownerClanMembers) {
                                    if (ownerClanMember != null) {
                                        UUID memberUUID = UUID.fromString(ownerClanMember);
                                        Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);

                                        if (ownerClanPlayer != null) {

                                            MessageUtils.sendPlayer(ownerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                            MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));

                                            if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                                for (Player p : playersWithSpyPerms) {
                                                    MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                                }
                                            }
                                            return true;
                                        }
                                    }
                                }
                                MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                            } else {
                                MessageUtils.sendPlayer(player, messagesConfig.getString("failed-must-be-in-clan"));
                            }
                        }
                    } else {

                        //If player not in cool down hashmap
                        chatCoolDownTimer.put(uuid, System.currentTimeMillis() + (clansConfig.getLong("clan-chat.cool-down.time") * 1000));
                        if (clanByMember != null) {
                            ArrayList<String> playerClanMembers = clanByMember.getClanMembers();

                            foliaLib.getImpl().runAsync((task) -> {
                                fireAsyncClanChatMessageSendEvent(player, clanByMember, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                                MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                            });

                            for (String playerClanMember : playerClanMembers) {
                                if (playerClanMember != null) {

                                    UUID memberUUID = UUID.fromString(playerClanMember);
                                    UUID ownerUUID = UUID.fromString(clanByMember.getClanOwner());
                                    Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                    Player playerClanOwner = Bukkit.getPlayer(ownerUUID);

                                    if (playerClanPlayer != null) {

                                        if (playerClanOwner != null) {
                                            MessageUtils.sendPlayer(playerClanOwner, messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                        }

                                        MessageUtils.sendPlayer(playerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));

                                        if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                            for (Player p : playersWithSpyPerms) {
                                                MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                        }

                        if (clanByOwner != null) {
                            ArrayList<String> ownerClanMembers = clanByOwner.getClanMembers();

                            foliaLib.getImpl().runAsync((task) -> {
                                fireAsyncClanChatMessageSendEvent(player, clanByOwner, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                                MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                            });

                            for (String ownerClanMember : ownerClanMembers) {
                                if (ownerClanMember != null) {
                                    UUID memberUUID = UUID.fromString(ownerClanMember);
                                    Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);

                                    if (ownerClanPlayer != null) {

                                        MessageUtils.sendPlayer(ownerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                        MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));

                                        if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                            for (Player p : playersWithSpyPerms) {
                                                MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                            MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                        } else {
                            MessageUtils.sendPlayer(player, messagesConfig.getString("failed-must-be-in-clan"));
                        }
                    }
                } else {

                    //If cool down disabled
                    if (clanByMember != null) {
                        ArrayList<String> playerClanMembers = clanByMember.getClanMembers();

                        foliaLib.getImpl().runAsync((task) -> {
                            fireAsyncClanChatMessageSendEvent(player, clanByMember, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), playerClanMembers);
                            MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                        });

                        for (String playerClanMember : playerClanMembers) {
                            if (playerClanMember != null) {

                                UUID memberUUID = UUID.fromString(playerClanMember);
                                UUID ownerUUID = UUID.fromString(clanByMember.getClanOwner());
                                Player playerClanPlayer = Bukkit.getPlayer(memberUUID);
                                Player playerClanOwner = Bukkit.getPlayer(ownerUUID);

                                if (playerClanPlayer != null) {

                                    if (playerClanOwner != null) {
                                        MessageUtils.sendPlayer(playerClanOwner, messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                    }

                                    MessageUtils.sendPlayer(playerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));

                                    if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                        for (Player p : playersWithSpyPerms) {
                                            MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByMember.getClanPrefix()));
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                    }

                    if (clanByOwner != null) {
                        ArrayList<String> ownerClanMembers = clanByOwner.getClanMembers();

                        foliaLib.getImpl().runAsync((task) -> {
                            fireAsyncClanChatMessageSendEvent(player, clanByOwner, clansConfig.getString("clan-chat.chat-prefix"), messageString.toString(), ownerClanMembers);
                            MessageUtils.sendDebugConsole("Fired AsyncClanChatMessageSendEvent");
                        });

                        for (String ownerClanMember : ownerClanMembers) {
                            if (ownerClanMember != null) {
                                UUID memberUUID = UUID.fromString(ownerClanMember);
                                Player ownerClanPlayer = Bukkit.getPlayer(memberUUID);

                                if (ownerClanPlayer != null) {

                                    MessageUtils.sendPlayer(ownerClanPlayer, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                    MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));

                                    if (clansConfig.getBoolean("clan-chat.chat-spy.enabled")) {
                                        for (Player p : playersWithSpyPerms) {
                                            MessageUtils.sendPlayer(p, chatSpyPrefix + " " + messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                        MessageUtils.sendPlayer(player, messageString.toString().replace(CLAN_PLACEHOLDER, clanByOwner.getClanPrefix()));
                    } else {
                        MessageUtils.sendPlayer(player, messagesConfig.getString("failed-must-be-in-clan"));
                    }
                }
            }
        } else {
            MessageUtils.sendConsole(messagesConfig.getString("player-only-command"));
        }
        return true;
    }

    private static void fireAsyncClanChatMessageSendEvent(Player player, Clan clan, String prefix, String message, ArrayList<String> recipients) {
        AsyncClanChatMessageSendEvent asyncClanChatMessageSendEvent = new AsyncClanChatMessageSendEvent(true, player, clan, prefix, message, recipients);
        Bukkit.getPluginManager().callEvent(asyncClanChatMessageSendEvent);
    }

    private String sendUsage() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> configStringList = messagesConfig.getStringList("clan-chat-command-incorrect-usage");
        for (String string : configStringList) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
