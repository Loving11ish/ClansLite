package me.loving11ish.clans;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.papermc.lib.PaperLib;
import me.loving11ish.clans.externalhooks.FloodgateAPI;
import me.loving11ish.clans.externalhooks.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import me.loving11ish.clans.commands.*;
import me.loving11ish.clans.commands.commandTabCompleters.ChestCommandTabCompleter;
import me.loving11ish.clans.commands.commandTabCompleters.ClanAdminTabCompleter;
import me.loving11ish.clans.commands.commandTabCompleters.ClanCommandTabCompleter;
import me.loving11ish.clans.expansions.PlaceholderAPIClanExpansion;
import me.loving11ish.clans.files.ClanGUIFileManager;
import me.loving11ish.clans.files.ClansFileManager;
import me.loving11ish.clans.files.MessagesFileManager;
import me.loving11ish.clans.files.UserMapFileManager;
import me.loving11ish.clans.listeners.*;
import me.loving11ish.clans.menusystem.PlayerMenuUtility;
import me.loving11ish.clans.menusystem.paginatedMenu.ClanListGUI;
import me.loving11ish.clans.updatesystem.JoinEvent;
import me.loving11ish.clans.updatesystem.UpdateChecker;
import me.loving11ish.clans.utils.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Clans extends JavaPlugin {

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();

    // Variables
    private static Clans plugin;
    private static FoliaLib foliaLib;
    private static FloodgateApi floodgateApi;
    private static VersionCheckerUtils versionCheckerUtils;
    private static PlaceholderAPIClanExpansion placeholderAPIClanExpansion = null;

    // Booleans
    private boolean chestsEnabled = false;
    private boolean GUIEnabled = false;
    private boolean onlineMode = false;
    private boolean debugMode = false;
    private boolean isPluginEnabled = false;
    private boolean updateAvailable = false;

    // File Managers
    public MessagesFileManager messagesFileManager;
    public ClansFileManager clansFileManager;
    public ClanGUIFileManager clanGUIFileManager;
    public UserMapFileManager userMapFileManager;

    // Hashmaps
    public HashMap<UUID, WrappedTask> teleportQueue = new HashMap<>();
    public static HashMap<Player, String> connectedPlayers = new HashMap<>();
    public static HashMap<Player, String> bedrockPlayers = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onLoad() {
        // Plugin startup logic
        plugin = this;
        foliaLib = new FoliaLib(this);

        // Load the plugin configs
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.GUIEnabled = getConfig().getBoolean("use-global-GUI-system", true);
        this.chestsEnabled = getConfig().getBoolean("protections.chests.enabled", true);
        this.setDebugMode(getConfig().getBoolean("general.developer-debug-mode.enabled", false));

        // Load messages.yml
        this.messagesFileManager = new MessagesFileManager();
        messagesFileManager.MessagesFileManager(this);
        MessageUtils.setDebug(this.isDebugMode());

        // Load clangui.yml
        this.clanGUIFileManager = new ClanGUIFileManager();
        clanGUIFileManager.ClanGUIFileManager(this);

        // Load usermap.yml
        this.userMapFileManager = new UserMapFileManager();
        userMapFileManager.UserMapFileManager(this);

        // Load clans.yml
        this.clansFileManager = new ClansFileManager();
        clansFileManager.ClansFileManager(this);

        // Check server version and set it
        versionCheckerUtils = new VersionCheckerUtils();
        versionCheckerUtils.setVersion();

        // Server version compatibility check
        if (versionCheckerUtils.getVersion() < 13 || versionCheckerUtils.getVersion() > 20) {
            MessageUtils.sendConsole("severe", "&4-------------------------------------------");
            MessageUtils.sendConsole("severe", "&4Your server version is: &d" + Bukkit.getServer().getVersion());
            MessageUtils.sendConsole("severe", "&4This plugin is only supported on the Minecraft versions listed below:");
            MessageUtils.sendConsole("severe", "&41.13.x");
            MessageUtils.sendConsole("severe", "&41.14.x");
            MessageUtils.sendConsole("severe", "&41.15.x");
            MessageUtils.sendConsole("severe", "&41.16.x");
            MessageUtils.sendConsole("severe", "&41.17.x");
            MessageUtils.sendConsole("severe", "&41.18.x");
            MessageUtils.sendConsole("severe", "&41.19.x");
            MessageUtils.sendConsole("severe", "&41.20.x");
            MessageUtils.sendConsole("severe", "&4Is now disabling!");
            MessageUtils.sendConsole("severe", "&4-------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            MessageUtils.sendConsole("&a-------------------------------------------");
            MessageUtils.sendConsole("&aA supported Minecraft version has been detected");
            MessageUtils.sendConsole("&aYour server version is: &d" + Bukkit.getServer().getVersion());
            MessageUtils.sendConsole("&6Continuing plugin startup");
            MessageUtils.sendConsole("&a-------------------------------------------");
        }

        if (foliaLib.isUnsupported()) {
            MessageUtils.sendConsole("severe", "&4-------------------------------------------");
            MessageUtils.sendConsole("severe", "&4Your server appears to running a version other than Spigot based!");
            MessageUtils.sendConsole("severe", "&4This plugin uses features that your server most likely doesn't have!");
            MessageUtils.sendConsole("severe", "&4Is now disabling!");
            MessageUtils.sendConsole("severe", "&4-------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Suggest PaperMC if not using
        if (foliaLib.isSpigot()) {
            PaperLib.suggestPaper(this);
        }
    }

    @Override
    public void onEnable() {
        // Load clan data from clans.yml
        if (clansFileManager != null) {
            if (clansFileManager.getClansConfig().contains("clans.data")) {
                try {
                    ClansStorageUtil.restoreClans();
                } catch (IOException e) {
                    MessageUtils.sendConsole("severe", "&4Failed to load data from clans.yml!");
                    MessageUtils.sendConsole("severe", "&4See below for errors!");
                    MessageUtils.sendConsole("severe", "&4Disabling Plugin!");
                    e.printStackTrace();
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }
        } else {
            MessageUtils.sendConsole("severe", "&4Failed to load data from clans.yml!");
            MessageUtils.sendConsole("severe", "&4Disabling Plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load user data from usermap.yml
        if (userMapFileManager != null) {
            if (userMapFileManager.getUserMapConfig().contains("users.data")) {
                try {
                    UserMapStorageUtil.restoreUserMap();
                } catch (IOException e) {
                    MessageUtils.sendConsole("severe", "&4Failed to load data from usermap.yml!");
                    MessageUtils.sendConsole("severe", "&4See below for errors!");
                    MessageUtils.sendConsole("severe", "&4Disabling Plugin!");
                    e.printStackTrace();
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }
        } else {
            MessageUtils.sendConsole("severe", "&4Failed to load data from usermap.yml!");
            MessageUtils.sendConsole("severe", "&4Disabling Plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register the plugin commands
        this.getCommand("clan").setExecutor(new ClanCommand());
        this.getCommand("clanadmin").setExecutor(new ClanAdmin());
        this.getCommand("clanchat").setExecutor(new ClanChatCommand());
        this.getCommand("clanchatspy").setExecutor(new ClanChatSpyCommand());
        this.getCommand("clanchest").setExecutor(new ClanChestCommand());

        // Register the command tab completers
        this.getCommand("clan").setTabCompleter(new ClanCommandTabCompleter());
        this.getCommand("clanchest").setTabCompleter(new ChestCommandTabCompleter());
        this.getCommand("clanadmin").setTabCompleter(new ClanAdminTabCompleter());

        // Register the plugin events
        this.getServer().getPluginManager().registerEvents(new PlayerPreConnectionEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMovementEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMessageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerKillEvent(), this);
        this.getServer().getPluginManager().registerEvents(new JoinEvent(), this);

        if (versionCheckerUtils.getVersion() >= 14) {
            this.getServer().getPluginManager().registerEvents(new ChestBreakEvent(), this);
            this.getServer().getPluginManager().registerEvents(new ChestOpenEvent(), this);
            this.getServer().getPluginManager().registerEvents(new MenuEvent(), this);
            if (this.GUIEnabled) {
                MessageUtils.sendConsole("&3Global GUI system enabled!");
            } else {
                MessageUtils.sendConsole("&c&lGlobal GUI system disabled!");
            }
            if (this.chestsEnabled) {
                MessageUtils.sendConsole("&3Chest protection system enabled!");
            } else {
                MessageUtils.sendConsole("&c&lChest protection system disabled!");
            }
        } else {
            MessageUtils.sendConsole("&cYour current server version does not support PersistentDataContainers!");
            MessageUtils.sendConsole("&c&lChest protection system disabled!");
            MessageUtils.sendConsole("&c&lGlobal GUI system disabled!");
        }

        // Update banned tags list
        ClanCommand.updateBannedTagsList();

        // Register PlaceHolderAPI hooks
        if (PlaceholderAPI.isPlaceholderAPIEnabled()) {
            placeholderAPIClanExpansion = new PlaceholderAPIClanExpansion();
            placeholderAPIClanExpansion.register();
            MessageUtils.sendConsole("-------------------------------------------");
            MessageUtils.sendConsole("&3PlaceholderAPI found!");
            MessageUtils.sendConsole("&3External placeholders enabled!");
            MessageUtils.sendConsole("-------------------------------------------");
        } else {
            MessageUtils.sendConsole("-------------------------------------------");
            MessageUtils.sendConsole("&cPlaceholderAPI not found!");
            MessageUtils.sendConsole("&cExternal placeholders disabled!");
            MessageUtils.sendConsole("-------------------------------------------");
        }

        // Register FloodgateApi hooks
        if (FloodgateAPI.isFloodgateEnabled()) {
            floodgateApi = FloodgateApi.getInstance();
            MessageUtils.sendConsole("-------------------------------------------");
            MessageUtils.sendConsole("&3FloodgateApi found!");
            MessageUtils.sendConsole("&3Full Bedrock support enabled!");
            MessageUtils.sendConsole("-------------------------------------------");
        } else {
            MessageUtils.sendConsole("-------------------------------------------");
            MessageUtils.sendConsole("&3FloodgateApi not found!");
            MessageUtils.sendConsole("&3Bedrock support may not function!");
            MessageUtils.sendConsole("-------------------------------------------");
        }

        // Plugin startup message
        MessageUtils.sendConsole("-------------------------------------------");
        MessageUtils.sendConsole("&3Plugin by: &b&lLoving11ish");
        MessageUtils.sendConsole("&3has been loaded successfully");
        MessageUtils.sendConsole("&3Plugin Version: &d&l" + pluginVersion);
        MessageUtils.sendDebugConsole("&aDeveloper debug mode enabled!");
        MessageUtils.sendDebugConsole("&aThis WILL fill the console");
        MessageUtils.sendDebugConsole("&awith additional ClansLite information!");
        MessageUtils.sendDebugConsole("&aThis setting is not intended for ");
        MessageUtils.sendDebugConsole("&acontinous use!");
        MessageUtils.sendConsole("-------------------------------------------");

        // Set plugin enabled to true
        this.setPluginEnabled(true);

        // Check for available updates
        new UpdateChecker(97163).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("no-update-available.1"));
                MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("no-update-available.2"));
                MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("no-update-available.3"));
                this.setUpdateAvailable(false);
            } else {
                MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("update-available.1"));
                MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("update-available.2"));
                MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("update-available.3"));
                this.setUpdateAvailable(true);
            }
        });

        // Start auto save task
        foliaLib.getImpl().runLaterAsync(() -> {
            TaskTimerUtils.runClansAutoSave();
            MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("auto-save-started"));
        }, 5L, TimeUnit.SECONDS);

        // Start auto invite clear task
        foliaLib.getImpl().runLaterAsync(() -> {
            TaskTimerUtils.runClanInviteClear();
            MessageUtils.sendConsole(messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-started"));
        }, 5L, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.setPluginEnabled(false);

        // Unregister plugin listeners
        HandlerList.unregisterAll(this);

        // Unregister placeholderAPI hooks
        if (placeholderAPIClanExpansion != null) {
            placeholderAPIClanExpansion.unregister();
        }

        // Safely stop the background tasks if running
        MessageUtils.sendConsole("-------------------------------------------");
        MessageUtils.sendConsole("&3Plugin by: &b&lLoving11ish");
        try {
            if (!teleportQueue.isEmpty()) {
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry : teleportQueue.entrySet()) {
                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    MessageUtils.sendDebugConsole("&aWrapped task: " + wrappedTask.toString());
                    MessageUtils.sendDebugConsole("&aTimed teleport task canceled successfully");
                    teleportQueue.remove(wrappedTaskEntry.getKey());
                }
            }
            if (TaskTimerUtils.autoSaveTask != null && !TaskTimerUtils.autoSaveTask.isCancelled()) {
                MessageUtils.sendDebugConsole("&aWrapped task: " + TaskTimerUtils.autoSaveTask.toString());
                MessageUtils.sendDebugConsole("&aTimed task autoSaveTask canceled successfully");
                TaskTimerUtils.autoSaveTask.cancel();
            }
            if (TaskTimerUtils.inviteClearTask != null && !TaskTimerUtils.inviteClearTask.isCancelled()) {
                MessageUtils.sendDebugConsole("&aWrapped task: " + TaskTimerUtils.inviteClearTask.toString());
                MessageUtils.sendDebugConsole("&aTimed task inviteClearTask canceled successfully");
                TaskTimerUtils.inviteClearTask.cancel();
            }
            if (ClanListGUI.autoGUIRefreshTask != null && !ClanListGUI.autoGUIRefreshTask.isCancelled()) {
                MessageUtils.sendDebugConsole("&aWrapped task: " + ClanListGUI.autoGUIRefreshTask.toString());
                MessageUtils.sendDebugConsole("&aTimed task autoGUIRefreshTask canceled successfully");
                ClanListGUI.autoGUIRefreshTask.cancel();
            }
            if (foliaLib.isUnsupported()) {
                Bukkit.getScheduler().cancelTasks(this);
                MessageUtils.sendDebugConsole("&aBukkit scheduler tasks canceled successfully");
            }
            MessageUtils.sendConsole("&3Background tasks have disabled successfully!");
        } catch (Exception e) {
            MessageUtils.sendConsole("&3Background tasks have disabled successfully!");
        }

        // Save clansList HashMap to clans.yml
        if (clansFileManager != null) {
            if (!ClansStorageUtil.getRawClansList().isEmpty()) {
                try {
                    ClansStorageUtil.saveClans();
                    MessageUtils.sendConsole("&3All clans saved to clans.yml successfully!");
                } catch (IOException e) {
                    MessageUtils.sendConsole("severe", "&4Failed to save clans to clans.yml!");
                    MessageUtils.sendConsole("severe", "&4See below error for reason!");
                    e.printStackTrace();
                }
            }
        } else {
            MessageUtils.sendConsole("&4Failed to save clans to clans.yml!");
        }

        // Saver usermap to usermap.yml
        if (userMapFileManager != null) {
            if (!UserMapStorageUtil.getRawUserMapList().isEmpty()) {
                try {
                    UserMapStorageUtil.saveUserMap();
                    MessageUtils.sendConsole("&3All users saved to usermap.yml successfully!");
                } catch (IOException e) {
                    MessageUtils.sendConsole("severe", "&4Failed to save usermap to usermap.yml!");
                    MessageUtils.sendConsole("severe", "&4See below error for reason!");
                    e.printStackTrace();
                }
            }
        } else {
            MessageUtils.sendConsole("&4Failed to save usermap to usermap.yml!");
        }

        // Final plugin shutdown message
        MessageUtils.sendConsole("&3Plugin Version: &d&l" + pluginVersion);
        MessageUtils.sendConsole("&3Has been shutdown successfully");
        MessageUtils.sendConsole("&3Goodbye!");
        MessageUtils.sendConsole("-------------------------------------------");

        plugin = null;
        floodgateApi = null;
        versionCheckerUtils = null;
        placeholderAPIClanExpansion = null;
        messagesFileManager = null;
        clansFileManager = null;
        clanGUIFileManager = null;
        userMapFileManager = null;
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(player))) {
            playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(player);
        }
    }

    public static Clans getPlugin() {
        return plugin;
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public static FloodgateApi getFloodgateApi() {
        return floodgateApi;
    }

    public static VersionCheckerUtils getVersionCheckerUtils() {
        return versionCheckerUtils;
    }

    public boolean isChestsEnabled() {
        return chestsEnabled;
    }

    public void setChestsEnabled(boolean chestsEnabled) {
        this.chestsEnabled = chestsEnabled;
    }

    public boolean isGUIEnabled() {
        return GUIEnabled;
    }

    public void setGUIEnabled(boolean GUIEnabled) {
        this.GUIEnabled = GUIEnabled;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public void setOnlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isPluginEnabled() {
        return isPluginEnabled;
    }

    public void setPluginEnabled(boolean pluginEnabled) {
        isPluginEnabled = pluginEnabled;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }
}
