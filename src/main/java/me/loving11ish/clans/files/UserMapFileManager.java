package me.loving11ish.clans.files;

import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.loving11ish.clans.Clans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UserMapFileManager {

    private Clans plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public void UserMapFileManager(Clans plugin){
        this.plugin = plugin;
        saveDefaultUserMapConfig();
    }

    public void reloadUserMapConfig(){
        if (this.configFile == null){
            this.configFile = new File(plugin.getDataFolder(), "usermap.yml");
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource("usermap.yml");
        if (defaultStream != null){
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getUserMapConfig(){
        if (this.dataConfig == null){
            this.reloadUserMapConfig();
        }
        return this.dataConfig;
    }

    public void saveUserMapConfig() {
        if (this.dataConfig == null||this.configFile == null){
            return;
        }
        try {
            this.getUserMapConfig().save(this.configFile);
        }catch (IOException e){
            MessageUtils.sendConsole("&4Could not save usermap.yml");
            MessageUtils.sendConsole("&4Check the below message for the reasons!");
            e.printStackTrace();
        }
    }

    public void saveDefaultUserMapConfig(){
        if (this.configFile == null){
            this.configFile = new File(plugin.getDataFolder(), "usermap.yml");
        }
        if (!this.configFile.exists()){
            this.plugin.saveResource("usermap.yml", false);
        }
    }
}
