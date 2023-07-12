package xyz.gamlin.clans;

import com.rylinaux.plugman.api.PlugManAPI;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.gamlin.clans.commands.*;
import xyz.gamlin.clans.commands.commandTabCompleters.ChestCommandTabCompleter;
import xyz.gamlin.clans.commands.commandTabCompleters.ClanAdminTabCompleter;
import xyz.gamlin.clans.commands.commandTabCompleters.ClanCommandTabCompleter;
import xyz.gamlin.clans.database.ConnectionUtils;
import xyz.gamlin.clans.expansions.PlayerClanExpansion;
import xyz.gamlin.clans.externalHooks.FloodgateAPI;
import xyz.gamlin.clans.externalHooks.PlaceholderAPI;
import xyz.gamlin.clans.externalHooks.PlugManXAPI;
import xyz.gamlin.clans.files.ClanGUIFileManager;
import xyz.gamlin.clans.files.ClansFileManager;
import xyz.gamlin.clans.files.MessagesFileManager;
import xyz.gamlin.clans.files.UsermapFileManager;
import xyz.gamlin.clans.listeners.*;
import xyz.gamlin.clans.menuSystem.PlayerMenuUtility;
import xyz.gamlin.clans.menuSystem.paginatedMenu.ClanListGUI;
import xyz.gamlin.clans.updateSystem.JoinEvent;
import xyz.gamlin.clans.updateSystem.UpdateChecker;
import xyz.gamlin.clans.utils.*;
import xyz.gamlin.clans.utils.abstractClasses.StorageUtils;
import xyz.gamlin.clans.utils.abstractClasses.UsermapUtils;
import xyz.gamlin.clans.utils.storageUtils.flatFile.FlatFileClanStorageUtils;
import xyz.gamlin.clans.utils.storageUtils.flatFile.FlatFileUsermapStorageUtils;
import xyz.gamlin.clans.utils.storageUtils.mySQL.MySQLClanStorageUtils;
import xyz.gamlin.clans.utils.storageUtils.mySQL.MySQLUsermapStorageUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class Clans extends JavaPlugin {

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();
    Logger logger = this.getLogger();

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
            logger.warning(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &4Your server version is: &d" + Bukkit.getServer().getVersion()));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &4This plugin is only supported on the Minecraft versions listed below:"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.13.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.14.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.15.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.16.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.17.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.18.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.19.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &41.20.x"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &4Is now disabling!"));
            logger.warning(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }else {
            logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &aA supported Minecraft version has been detected"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &aYour server version is: &d" + Bukkit.getServer().getVersion()));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
            logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
        }

        //Suggest PaperMC if not using
        if (foliaLib.isUnsupported()||foliaLib.isSpigot()){
            PaperLib.suggestPaper(this);
        }

        //Check if PlugManX is enabled
        if (Bukkit.getPluginManager().isPluginEnabled("PlugManX")||PlugManXAPI.isPlugManXEnabled()){
            if (!PlugManAPI.iDoNotWantToBeUnOrReloaded("ClansLite")){
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&4WARNING WARNING WARNING WARNING!"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4You appear to be using an unsupported version of &d&lPlugManX"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Please &4&lDO NOT USE PLUGMANX TO LOAD/UNLOAD/RELOAD THIS PLUGIN!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Please &4&lFULLY RESTART YOUR SERVER!"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4This plugin &4&lHAS NOT &4been validated to use this version of PlugManX!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4&lNo official support will be given to you if you use this!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4&lUnless Loving11ish has explicitly agreed to help!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Please add ClansLite to the ignored-plugins list in PlugManX's config.yml"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
            }else {
                logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite: &aSuccessfully hooked into PlugManX"));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite: &aSuccessfully added ClansLite to ignoredPlugins list."));
                logger.info(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
                logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            }
        }else {
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &cPlugManX not found!"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &cDisabling PlugManX hook loader"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &6Continuing plugin startup"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite: &a&lSUCCESSFULLY &aconnected to MySQL database!"));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite: &aContinuing plugin startup!"));
                    logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                }
            }catch (SQLException e){
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to connect to the MySQL database!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Please check you connection details in the config.yml"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                e.printStackTrace();
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Database connection failed!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling ClansLite!"));
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            try {
                if (this.connection != null){
                    connectionUtils.createTables();
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite: &a&lSUCCESSFULLY &acreated MySQL tables!"));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite: &aContinuing plugin startup!"));
                    logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                }
            }catch (SQLException e){
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to create the tables in your database!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                e.printStackTrace();
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Table creation failed!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling ClansLite!"));
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
                    logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from remote MySQL database!"));
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                    e.printStackTrace();
                    logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }else {
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to connect to remote MySQL database!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
                        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from clans.yml!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                        e.printStackTrace();
                        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                        Bukkit.getPluginManager().disablePlugin(this);
                        return;
                    }
                }
            }else {
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from clans.yml!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from remote MySQL database!"));
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                    e.printStackTrace();
                    logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }else {
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to connect to remote MySQL database!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from usermap.yml!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below for errors!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                        e.printStackTrace();
                        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                        Bukkit.getPluginManager().disablePlugin(this);
                        return;
                    }
                }
            }else {
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to load data from usermap.yml!"));
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Disabling Plugin!"));
                logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3PlaceholderAPI found!"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3External placeholders enabled!"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        }else {
            logger.warning(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &cPlaceholderAPI not found!"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite: &cExternal placeholders disabled!"));
            logger.warning(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Register FloodgateApi hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("floodgate")|| FloodgateAPI.isFloodgateEnabled()){
            floodgateApi = FloodgateApi.getInstance();
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3FloodgateApi found!"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Full Bedrock support enabled!"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        }else {
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3FloodgateApi not found!"));
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Bedrock support may not function!"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Plugin startup message
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin by: &b&lLoving11ish"));
        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3has been loaded successfully"));
        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin Version: &d&l" + pluginVersion));
        if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aDeveloper debug mode enabled!"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aThis WILL fill the console"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite-Debug: &awith additional ClansLite information!"));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aThis setting is not intended for "));
            logger.warning(ColorUtils.translateColorCodes("&6ClansLite-Debug: &acontinous use!"));
        }
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));

        //Check for available updates
        new UpdateChecker(97163).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.1")));
                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.2")));
                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.3")));
            }else {
                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.1")));
                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.2")));
                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.3")));
            }
        });

        //Run auto MySQL connection refresh task
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            foliaLib.getImpl().runLaterAsync(new Runnable() {
                @Override
                public void run() {
                    runAutoConnectionRefreshOne();
                    logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("mysql-connection-task-started")));
                }
            }, 5L, TimeUnit.SECONDS);
        }

        //Start auto save task
        if (getConfig().getBoolean("general.run-auto-save-task.enabled")){
            foliaLib.getImpl().runLaterAsync(new Runnable() {
                @Override
                public void run() {
                    TaskTimerUtils.runClansAutoSaveOne();
                    logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-save-started")));
                }
            }, 10L, TimeUnit.SECONDS);
        }

        //Start auto invite clear task
        if (getConfig().getBoolean("general.run-auto-invite-wipe-task.enabled")){
            foliaLib.getImpl().runLaterAsync(new Runnable() {
                @Override
                public void run() {
                    TaskTimerUtils.runClanInviteClearOne();
                    logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-started")));
                }
            }, 10L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDisable() {
        //Plugin shutdown logic

        //Unregister plugin listeners
        HandlerList.unregisterAll(this);

        //Safely stop the background tasks if running
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin by: &b&lLoving11ish"));
        try {
            if (!TaskTimerUtils.task1.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task1.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 1 canceled successfully"));
                }
                TaskTimerUtils.task1.cancel();
            }
            if (!TaskTimerUtils.task2.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task2.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 2 canceled successfully"));
                }
                TaskTimerUtils.task2.cancel();
            }
            if (!TaskTimerUtils.task3.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task3.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 3 canceled successfully"));
                }
                TaskTimerUtils.task3.cancel();
            }
            if (!TaskTimerUtils.task4.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + TaskTimerUtils.task4.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 4 canceled successfully"));
                }
                TaskTimerUtils.task4.cancel();
            }
            if (!ClanListGUI.task5.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + ClanListGUI.task5.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 5 canceled successfully"));
                }
                ClanListGUI.task5.cancel();
            }
            if (!this.task6.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + this.task6.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 6 canceled successfully"));
                }
                task6.cancel();
            }
            if (!this.task7.isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aWrapped task: " + this.task7.toString()));
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aTimed task 7 canceled successfully"));
                }
                task6.cancel();
            }
            if (foliaLib.isUnsupported()){
                Bukkit.getScheduler().cancelTasks(this);
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6ClansLite-Debug: &aBukkit scheduler tasks canceled successfully"));
                }
            }
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Background tasks have disabled successfully!"));
        }catch (Exception e){
            logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Background tasks have disabled successfully!"));
        }

        //Save clansList HashMap to MySQL or clans.yml
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                if (!storageUtils.getRawClansList().isEmpty()){
                    try {
                        storageUtils.saveClans();
                        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3All clans saved to remote database successfully!"));
                    } catch (IOException e) {
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save clans to remote database!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }
        }else {
            if (clansFileManager != null){
                if (!storageUtils.getRawClansList().isEmpty()){
                    try {
                        storageUtils.saveClans();
                        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3All clans saved to clans.yml successfully!"));
                    } catch (IOException e) {
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save clans to clans.yml!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }else {
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save clans to clans.yml!"));
            }
        }

        //Saver usermap to MySQL or usermap.yml
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                if (!usermapUtils.getRawUsermapList().isEmpty()){
                    try {
                        usermapUtils.saveUsermap();
                        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3All users saved to remote database successfully!"));
                    } catch (IOException e) {
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save usermap to remote database!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }
        }else {
            if (usermapFileManager != null){
                if (!usermapUtils.getRawUsermapList().isEmpty()){
                    try {
                        usermapUtils.saveUsermap();
                        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3All users saved to usermap.yml successfully!"));
                    } catch (IOException e) {
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save usermap to usermap.yml!"));
                        logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4See below error for reason!"));
                        e.printStackTrace();
                    }
                }
            }else {
                logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Failed to save usermap to usermap.yml!"));
            }
        }

        //Close the MySQL connection if open
        if (getConfig().getBoolean("storage.mysql.enabled")) {
            if (this.connection != null){
                try {
                    this.connection.close();
                } catch (SQLException e) {
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst closing the remote database connection!"));
                    logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Please report the below error to the developer!"));
                    e.printStackTrace();
                }
            }
        }

        //Final plugin shutdown message
        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Plugin Version: &d&l" + pluginVersion));
        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Has been shutdown successfully"));
        logger.info(ColorUtils.translateColorCodes("&6ClansLite: &3Goodbye!"));
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));

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
                            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to connect to the MySQL database!"));
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Please check you connection details in the config.yml"));
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                            e.printStackTrace();
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Database connection failed!"));
                            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));

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
                            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4An error occurred whilst attempting to connect to the MySQL database!"));
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Please check you connection details in the config.yml"));
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4If this error persists contact the developer and provide the below error!"));
                            e.printStackTrace();
                            logger.severe(ColorUtils.translateColorCodes("&6ClansLite: &4Database connection failed!"));
                            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
