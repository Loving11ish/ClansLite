package me.loving11ish.clans.listeners;

import me.loving11ish.clans.Clans;
import me.loving11ish.clans.utils.MessageUtils;
import me.loving11ish.clans.websocketutils.MojangAPIRequestUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;
import java.util.UUID;

public class PlayerPreConnectionEvent implements Listener {

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();

    private boolean firstPlayerConnected = true;

    @EventHandler
    public void onPlayerPreConnect(AsyncPlayerPreLoginEvent event) {
        if (firstPlayerConnected) {
            try {
                UUID uuid = event.getUniqueId();

                if (MojangAPIRequestUtils.canGetOfflinePlayerData(uuid.toString(), event.getName())) {

                    Clans.getPlugin().setOnlineMode(true);
                    if (Clans.getVersionCheckerUtils().getVersion() >= 14) {
                        Clans.getPlugin().setGUIEnabled(clansConfig.getBoolean("use-global-GUI-system"));
                        Clans.getPlugin().setChestsEnabled(clansConfig.getBoolean("protections.chests.enabled"));
                        if (Clans.getPlugin().isGUIEnabled()) {
                            MessageUtils.sendConsole("&3Global GUI system enabled!");
                        } else {
                            MessageUtils.sendConsole("&c&lGlobal GUI system disabled!");
                        }
                        if (Clans.getPlugin().isChestsEnabled()) {
                            MessageUtils.sendConsole("&3Chest protection system enabled!");
                        } else {
                            MessageUtils.sendConsole("&c&lChest protection system disabled!");
                        }

                    } else {
                        Clans.getPlugin().setGUIEnabled(false);
                        Clans.getPlugin().setChestsEnabled(false);
                        MessageUtils.sendConsole("&cYour current server version does not support PersistentDataContainers!");
                        MessageUtils.sendConsole("&c&lChest protection system disabled!");
                        MessageUtils.sendConsole("&c&lGlobal GUI system disabled!");
                    }

                } else if (!MojangAPIRequestUtils.canGetOfflinePlayerData(uuid.toString(), event.getName())) {

                    Clans.getPlugin().setOnlineMode(false);
                    if (Clans.getVersionCheckerUtils().getVersion() >= 14) {
                        Clans.getPlugin().setGUIEnabled(clansConfig.getBoolean("use-global-GUI-system"));
                        Clans.getPlugin().setChestsEnabled(clansConfig.getBoolean("protections.chests.enabled"));
                        if (Clans.getPlugin().isGUIEnabled()) {
                            MessageUtils.sendConsole("&3Global GUI system enabled!");
                        } else {
                            MessageUtils.sendConsole("&c&lGlobal GUI system disabled!");
                        }
                        if (Clans.getPlugin().isChestsEnabled()) {
                            MessageUtils.sendConsole("&3Chest protection system enabled!");
                        } else {
                            MessageUtils.sendConsole("&c&lChest protection system disabled!");
                        }
                    } else {
                        Clans.getPlugin().setGUIEnabled(false);
                        Clans.getPlugin().setChestsEnabled(false);
                        MessageUtils.sendConsole("&cYour current server version does not support PersistentDataContainers!");
                        MessageUtils.sendConsole("&c&lChest protection system disabled!");
                        MessageUtils.sendConsole("&c&lGlobal GUI system disabled!");
                    }

                    MessageUtils.sendConsole("&4-------------------------------------------");
                    MessageUtils.sendConsole("&4This plugin is only officially supported on online servers or servers running in an online network situation!");
                    MessageUtils.sendConsole("&4Some features may behave incorrectly or may be broken completely!");
                    MessageUtils.sendConsole("&4Player skins will not be loaded correctly within the GUI system!");
                    MessageUtils.sendConsole("&4Please set &e'online-mode=true' &4in &e'server.properties'");
                    MessageUtils.sendConsole("&4Or ensure your proxy setup is correct and your proxy is set to online mode!");
                    MessageUtils.sendConsole("&4This will then ensure correct functionality of the plugin!");
                    MessageUtils.sendConsole("&4-------------------------------------------");
                }

            } catch (IOException e) {
                MessageUtils.sendConsole("&4-------------------------------------------");
                MessageUtils.sendConsole("&4Unable to reach Mojang player database!");
                MessageUtils.sendConsole("&4See stacktrace below for more details.");
                e.printStackTrace();
                MessageUtils.sendConsole("&4-------------------------------------------");
            }
            firstPlayerConnected = false;
        }
    }
}
