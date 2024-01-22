package me.loving11ish.clans.utils;

import org.bukkit.Bukkit;

import java.util.regex.PatternSyntaxException;

public class VersionCheckerUtils {

    private final String serverPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private int version;

    public void setVersion() {
        try {
            version = Integer.parseInt(serverPackage.split("_")[1]);
        }catch (NumberFormatException | PatternSyntaxException e){
            MessageUtils.sendConsole("&c-------------------------------------------");
            MessageUtils.sendConsole("&4Unable to process server version!");
            MessageUtils.sendConsole("&4Some features may break unexpectedly!");
            MessageUtils.sendConsole("&4Report any issues to the developer!");
            MessageUtils.sendConsole("&c-------------------------------------------");
        }
    }

    public String getServerPackage() {
        return serverPackage;
    }

    public int getVersion() {
        return version;
    }
}
