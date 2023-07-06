package xyz.gamlin.clans.database;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.gamlin.clans.Clans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtils {

    FileConfiguration clansConfig = Clans.getPlugin().getConfig();

    private Connection connection;

    public Connection getConnection() throws SQLException {
        if(this.connection != null){
            return this.connection;
        }

        String host = clansConfig.getString("storage.mysql.host");
        String port = clansConfig.getString("storage.mysql.port");
        String database = clansConfig.getString("storage.mysql.database");
        String username = clansConfig.getString("storage.mysql.username");
        String password = clansConfig.getString("storage.mysql.password");
        boolean useSSL = clansConfig.getBoolean("storage.mysql.useSSL");

        StringBuilder url = new StringBuilder();
        url.append("jdbc:mysql://");
        url.append(host);
        url.append(":");
        url.append(port);
        url.append("/");
        url.append(database);
        url.append(":");
        url.append(username);
        url.append(":");
        url.append(password);
        url.append("?");
        url.append("useSSL=").append(String.valueOf(useSSL));

        this.connection = DriverManager.getConnection(url.toString());

        return this.connection;
    }

    public void createTables() throws SQLException {
        Statement statement = getConnection().createStatement();
        String clanTable = "CREATE TABLE IF NOT EXISTS clans (uuid varchar(36) primary key, clanOwner varchar(36), clanFinalName varchar(256), clanPrefix varchar(256), friendlyFire boolean, clanPoints int, maxAllowedProtectedChests int)";
        String usermapTable = "CREATE TABLE IF NOT EXISTS usermap (uuid varchar(36) primary key, javaUUID varchar(36), lastPlayerName varchar(64), pointBalance int, canChatSpy boolean, isBedrockPlayer boolean, bedrockUUID varchar(36))";

        statement.execute(clanTable);
        statement.execute(usermapTable);

        statement.close();
    }
}
