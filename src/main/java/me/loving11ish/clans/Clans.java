package me.loving11ish.clans;

import com.rylinaux.plugman.api.PlugManAPI;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import me.loving11ish.clans.commands.*;
import me.loving11ish.clans.commands.commandTabCompleters.ChestCommandTabCompleter;
import me.loving11ish.clans.commands.commandTabCompleters.ClanAdminTabCompleter;
import me.loving11ish.clans.commands.commandTabCompleters.ClanCommandTabCompleter;
import me.loving11ish.clans.database.ConnectionUtils;
import me.loving11ish.clans.expansions.PlayerClanExpansion;
import me.loving11ish.clans.externalHooks.FloodgateAPI;
import me.loving11ish.clans.externalHooks.PlaceholderAPI;
import me.loving11ish.clans.externalHooks.PlugManXAPI;
import me.loving11ish.clans.files.ClanGUIFileManager;
import me.loving11ish.clans.files.ClansFileManager;
import me.loving11ish.clans.files.MessagesFileManager;
import me.loving11ish.clans.files.UsermapFileManager;
import me.loving11ish.clans.listeners.*;
import me.loving11ish.clans.menuSystem.PlayerMenuUtility;
import me.loving11ish.clans.menuSystem.paginatedMenu.ClanListGUI;
import me.loving11ish.clans.updateSystem.JoinEvent;
import me.loving11ish.clans.updateSystem.UpdateChecker;
import me.loving11ish.clans.utils.*;
import me.loving11ish.clans.utils.abstractClasses.StorageUtils;
import me.loving11ish.clans.utils.abstractClasses.UsermapUtils;
import me.loving11ish.clans.utils.storageUtils.flatFile.FlatFileClanStorageUtils;
import me.loving11ish.clans.utils.storageUtils.flatFile.FlatFileUsermapStorageUtils;
import me.loving11ish.clans.utils.storageUtils.mySQL.MySQLClanStorageUtils;
import me.loving11ish.clans.utils.storageUtils.mySQL.MySQLUsermapStorageUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Clans extends JavaPlugin {

    ConsoleCommandSender console = Bukkit.getConsoleSender();
    
    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();

    private static Clans plugin;
    private static FoliaLib foliaLib;
    private static FloodgateApi floodgateApi;

    private WrappedTask task6;
    private WrappedTask task7;

    public MessagesFileManager messagesFileManager;
    public ClansFileManager clansFileManager;
    public ClanGUIFileManager clanGUIFileManager;
    public UsermapFileManager usermapFileManager;

    public StorageUtils storageUtils;
    public UsermapUtils usermapUtils;
    public ConnectionUtils connectionUtils = null;
    public Connection connection = null;

    public HashMap<UUID, WrappedTask> teleportQueue = new HashMap<>();
    public static HashMap<Player, String> connectedPlayers = new HashMap<>();
    public static HashMap<Player, String> bedrockPlayers = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        //Plugin startup logic
        plugin = this;
        foliaLib = new FoliaLib(plugin);

        //Server version compatibility check
        if (!(Bukkit.getServer().getVersion().contains("1.13")||Bukkit.getServer().getVersion().contains("1.14")||
                Bukkit.getServer().getVersion().contains("1.15")||Bukkit.getServer().getVersion().contains("1.16")||
                Bukkit.getServer().getVersion().contains("1.17")||Bukkit.getServer().getVersion().contains("1.18")||
                Bukkit.getServer().getVersion().contains("1.19")||Bukkit.getServer().getVersion().contains("1.20"))){
            console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Your server version is: &d" + Bukkit.getServer().getVersion()));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4This plugin is only supported on the Minecraft versions listed below:"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.13.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.14.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.15.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.16.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.17.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.18.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.19.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &41.20.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Is now disabling!"));
            console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &aA supported Minecraft version has been detected"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &aYour server version is: &d" + Bukkit.getServer().getVersion()));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
            console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
        }

        //Suggest PaperMC if not using
        if (foliaLib.isUnsupported()||foliaLib.isSpigot()){
            PaperLib.suggestPaper(this);
        }

        //Check if PlugManX is enabled
        if (Bukkit.getPluginManager().isPluginEnabled("PlugManX")||PlugManXAPI.isPlugManXEnabled()){
            if (!PlugManAPI.iDoNotWantToBeUnOrReloaded("ClansLite")){
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&4sendMessage sendMessage sendMessage sendMessage!"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4You appear to be using an unsupported version of &d&lPlugManX"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please &4&lDO NOT USE PLUGMANX TO LOAD/UNLOAD/RELOAD THIS PLUGIN!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please &4&lFULLY RESTART YOUR SERVER!"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4This plugin &4&lHAS NOT &4been validated to use this version of PlugManX!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4&lNo official support will be given to you if you use this!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4&lUnless Loving11ish has explicitly agreed to help!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please add ClansLite to the ignored-plugins list in PlugManX's config.yml"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &aSuccessfully hooked into PlugManX"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &aSuccessfully added ClansLite to ignoredPlugins list."));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
                console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            }
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &cPlugManX not found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &cDisabling PlugManX hook loader"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Load the plugin configs
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Load messages.yml
        this.messagesFileManager = new MessagesFileManager();
        messagesFileManager.MessagesFileManager(this);

        //Load clangui.yml
        this.clanGUIFileManager = new ClanGUIFileManager();
        clanGUIFileManager.ClanGUIFileManager(this);

        //Load MySQL connection if enabled
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            this.connectionUtils = new ConnectionUtils();
            try {
                this.connection = connectionUtils.getConnection();
                if (this.connection != null){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &a&lSUCCESSFULLY &aconnected to MySQL database!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &aContinuing plugin startup!"));
                    console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                }
            }catch (SQLException e){
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to connect to the MySQL database!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please check you connection details in the config.yml"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                e.printStackTrace();
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Database connection failed!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling ClansLite!"));
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            try {
                if (this.connection != null){
                    connectionUtils.createTables();
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &a&lSUCCESSFULLY &acreated MySQL tables!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &aContinuing plugin startup!"));
                    console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                }
            }catch (SQLException e){
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to create the tables in your database!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                e.printStackTrace();
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Table creation failed!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling ClansLite!"));
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        //Load clans.yml
        this.clansFileManager = new ClansFileManager();
        clansFileManager.ClansFileManager(this);
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                this.storageUtils = new MySQLClanStorageUtils(connectionUtils, connection);
                try {
                    storageUtils.restoreClans();
                } catch (IOException e) {
                    console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from remote MySQL database!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                    e.printStackTrace();
                    console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to connect to remote MySQL database!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }else {
            this.storageUtils = new FlatFileClanStorageUtils();
            if (clansFileManager != null){
                if (clansFileManager.getClansConfig().contains("clans.data")){
                    try {
                        storageUtils.restoreClans();
                    } catch (IOException e) {
                        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from clans.yml!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                        e.printStackTrace();
                        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                        Bukkit.getPluginManager().disablePlugin(this);
                        return;
                    }
                }
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from clans.yml!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }


        //Load usermap.yml
        this.usermapFileManager = new UsermapFileManager();
        usermapFileManager.UsermapFileManager(this);
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                this.usermapUtils = new MySQLUsermapStorageUtils(connectionUtils, connection);
                try {
                    usermapUtils.restoreUsermap();
                } catch (IOException e) {
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from remote MySQL database!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                    e.printStackTrace();
                    console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to connect to remote MySQL database!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }else {
            this.usermapUtils = new FlatFileUsermapStorageUtils();
            if (usermapFileManager != null){
                if (usermapFileManager.getUsermapConfig().contains("users.data")){
                    try {
                        usermapUtils.restoreUsermap();
                    } catch (IOException e) {
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from usermap.yml!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                        e.printStackTrace();
                        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                        Bukkit.getPluginManager().disablePlugin(this);
                        return;
                    }
                }
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from usermap.yml!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }


        //Register the plugin commands
        this.getCommand("clan").setExecutor(new ClanCommand());
        this.getCommand("clanadmin").setExecutor(new ClanAdmin());
        this.getCommand("clanchat").setExecutor(new ClanChatCommand());
        this.getCommand("clanchatspy").setExecutor(new ClanChatSpyCommand());
        this.getCommand("clanchest").setExecutor(new ClanChestCommand());

        //Register the command tab completers
        this.getCommand("clan").setTabCompleter(new ClanCommandTabCompleter());
        this.getCommand("clanchest").setTabCompleter(new ChestCommandTabCompleter());
        this.getCommand("clanadmin").setTabCompleter(new ClanAdminTabCompleter());

        //Register the plugin events
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMessageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerKillEvent(), this);
        this.getServer().getPluginManager().registerEvents(new ChestBreakEvent(), this);
        this.getServer().getPluginManager().registerEvents(new ChestOpenEvent(), this);
        this.getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        this.getServer().getPluginManager().registerEvents(new MenuEvent(), this);

        //Update banned tags list
        ClanCommand.updateBannedTagsList();

        //Register PlaceHolderAPI hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")|| PlaceholderAPI.isPlaceholderAPIEnabled()){
            new PlayerClanExpansion().register();
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3PlaceholderAPI found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3External placeholders enabled!"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &cPlaceholderAPI not found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &cExternal placeholders disabled!"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Register FloodgateApi hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("floodgate")|| FloodgateAPI.isFloodgateEnabled()){
            floodgateApi = FloodgateApi.getInstance();
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3FloodgateApi found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Full Bedrock support enabled!"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3FloodgateApi not found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Bedrock support may not function!"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Plugin startup message
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin by: &b&lLoving11ish"));
        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3has been loaded successfully"));
        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin Version: &d&l" + pluginVersion));
        if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aDeveloper debug mode enabled!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aThis WILL fill the console"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &awith additional ClansLite information!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aThis setting is not intended for "));
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &acontinous use!"));
        }
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));

        //Check for available updates
        new UpdateChecker(97163).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.1")));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.2")));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.3")));
            }else {
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.1")));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.2")));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.3")));
            }
        });

        //Run auto MySQL connection refresh task
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            foliaLib.getImpl().runLaterAsync(new Runnable() {
                @Override
                public void run() {
                    runAutoConnectionRefreshOne();
                    console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("mysql-connection-task-started")));
                }
            }, 5L, TimeUnit.SECONDS);
        }

        //Start auto save task
        foliaLib.getImpl().runLaterAsync(new Runnable() {
            @Override
            public void run() {
                TaskTimerUtils.runClansAutoSaveOne();
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-save-started")));
            }
        }, 10L, TimeUnit.SECONDS);

        //Start auto invite clear task
        foliaLib.getImpl().runLaterAsync(new Runnable() {
            @Override
            public void run() {
                TaskTimerUtils.runClanInviteClearOne();
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-started")));
            }
        }, 10L, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        //Plugin shutdown logic

        //Unregister plugin listeners
        HandlerList.unregisterAll(this);

        //Safely stop the background tasks if running
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin by: &b&lLoving11ish"));
        try {
            if (!teleportQueue.isEmpty()){
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry: teleportQueue.entrySet()){
                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + wrappedTask.toString()));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed teleport task canceled successfully"));
                    }
                    teleportQueue.remove(wrappedTaskEntry.getKey());
                }
            }
            if (!TaskTimerUtils.task1.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task1.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 1 canceled successfully"));
                }
                TaskTimerUtils.task1.cancel();
            }
            if (!TaskTimerUtils.task2.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task2.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 2 canceled successfully"));
                }
                TaskTimerUtils.task2.cancel();
            }
            if (!TaskTimerUtils.task3.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task3.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 3 canceled successfully"));
                }
                TaskTimerUtils.task3.cancel();
            }
            if (!TaskTimerUtils.task4.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task4.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 4 canceled successfully"));
                }
                TaskTimerUtils.task4.cancel();
            }
            if (!ClanListGUI.task5.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + ClanListGUI.task5.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 5 canceled successfully"));
                }
                ClanListGUI.task5.cancel();
            }
            if (!this.task6.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + this.task6.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 6 canceled successfully"));
                }
                task6.cancel();
            }
            if (!this.task7.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + this.task7.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 7 canceled successfully"));
                }
                task6.cancel();
            }
            if (foliaLib.isUnsupported()){
                Bukkit.getScheduler().cancelTasks(this);
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aBukkit scheduler tasks canceled successfully"));
                }
            }
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Background tasks have disabled successfully!"));
        }catch (Exception e){
            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Background tasks have disabled successfully!"));
        }

        //Save clansList HashMap to MySQL or clans.yml
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                if (!storageUtils.getRawClansList().isEmpty()){
                    try {
                        storageUtils.saveClans();
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3All clans saved to remote database successfully!"));
                    } catch (IOException e) {
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save clans to remote database!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }
        }else {
            if (clansFileManager != null){
                if (!storageUtils.getRawClansList().isEmpty()){
                    try {
                        storageUtils.saveClans();
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3All clans saved to clans.yml successfully!"));
                    } catch (IOException e) {
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save clans to clans.yml!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save clans to clans.yml!"));
            }
        }

        //Saver usermap to MySQL or usermap.yml
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                if (!usermapUtils.getRawUsermapList().isEmpty()){
                    try {
                        usermapUtils.saveUsermap();
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3All users saved to remote database successfully!"));
                    } catch (IOException e) {
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save usermap to remote database!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }
        }else {
            if (usermapFileManager != null){
                if (!usermapUtils.getRawUsermapList().isEmpty()){
                    try {
                        usermapUtils.saveUsermap();
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3All users saved to usermap.yml successfully!"));
                    } catch (IOException e) {
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save usermap to usermap.yml!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save usermap to usermap.yml!"));
            }
        }

        //Close the MySQL connection if open
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                try {
                    this.connection.close();
                } catch (SQLException e) {
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst closing the remote database connection!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please report the below error to the developer!"));
                    e.printStackTrace();
                }
            }
        }

        //Final plugin shutdown message
        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin Version: &d&l" + pluginVersion));
        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Has been shutdown successfully"));
        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Goodbye!"));
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));

        plugin = null;
        floodgateApi = null;
        connection = null;
        messagesFileManager = null;
        clansFileManager = null;
        clanGUIFileManager = null;
        usermapFileManager = null;
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

    public void runAutoConnectionRefreshOne() {
        task6 = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 600;
            @Override
            public void run() {
                if (time == 1) {
                    if (connection != null) {
                        try {
                            if (connection.isClosed()) {
                                connection = connectionUtils.getConnection();
                            }
                            runAutoConnectionRefreshTwo();
                            task6.cancel();
                            return;
                        }catch (SQLException e) {
                            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to connect to the MySQL database!"));
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please check you connection details in the config.yml"));
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                            e.printStackTrace();
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Database connection failed!"));
                            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));

                            runAutoConnectionRefreshTwo();
                            task6.cancel();
                            return;
                        }
                    }
                }else {
                    time --;
                }
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    public void runAutoConnectionRefreshTwo() {
        task7 = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 600;
            @Override
            public void run() {
                if (time == 1) {
                    if (connection != null) {
                        try {
                            if (connection.isClosed()) {
                                connection = connectionUtils.getConnection();
                            }
                            runAutoConnectionRefreshOne();
                            task7.cancel();
                            return;
                        }catch (SQLException e) {
                            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to connect to the MySQL database!"));
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please check you connection details in the config.yml"));
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                            e.printStackTrace();
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Database connection failed!"));
                            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
                            runAutoConnectionRefreshOne();
                            task7.cancel();
                            return;
                        }
                    }
                }else {
                    time --;
                }
            }
        }, 1L, 1L, TimeUnit.SECONDS);
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
}
