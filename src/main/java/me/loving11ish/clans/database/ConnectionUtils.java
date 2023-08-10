package me.loving11ish.clans.database;

import org.bukkit.configuration.file.FileConfiguration;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.models.Chest;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.models.ClanPlayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
        String clanTable = "CREATE TABLE IF NOT EXISTS clans (clanUUID varchar(36) primary key, clanOwner varchar(36), clanFinalName varchar(256), clanPrefix varchar(256), friendlyFire boolean, clanPoints int, maxAllowedProtectedChests int)";
        String usermapTable = "CREATE TABLE IF NOT EXISTS usermap (uuid varchar(36) primary key, javaUUID varchar(36), lastPlayerName varchar(64), pointBalance int, canChatSpy boolean, isBedrockPlayer boolean, bedrockUUID varchar(36))";
        String clanMembersTable = "CREATE TABLE IF NOT EXISTS clan_members (memberUUID varchar(36) primary key, clanOwnerUUID varchar(36), clanFinalName varchar(256))";
        String clanEnemiesTable = "CREATE TABLE IF NOT EXISTS clan_enemies (enemyOwnerUUID varchar(36) primary key, enemyClanName varchar(256), clanOwnerUUID varchar(36), clanName varchar(256))";
        String clanAlliesTable = "CREATE TABLE IF NOT EXISTS clan_allies (allyOwnerUUID varchar(36) primary key, allyClanName varchar(256), clanOwnerUUID varchar(36), clanName varchar(256))";
        String clanChestsTable = "CREATE TABLE IF NOT EXISTS chests (chestUUID varchar(36) primary key, chestWorldName varchar(256), chestX double, chestY double, chestZ double)";
        String playersWithAccessTable = "CREATE TABLE IF NOT EXISTS players_with_access (playerUUID varchar(36) primary key, chestUUID varchar(36))";

        statement.execute(clanTable);
        statement.execute(usermapTable);
        statement.execute(clanMembersTable);
        statement.execute(clanEnemiesTable);
        statement.execute(clanAlliesTable);
        statement.execute(clanChestsTable);
        statement.execute(playersWithAccessTable);

        statement.close();
    }

    public void insertClanIntoTable(Clan clan) throws SQLException {

    }

    public void insertUserIntoTable(ClanPlayer clanPlayer) throws SQLException {

    }

    public void dropClanFromTable(Clan clan) throws SQLException {

    }

    public void dropUserFromTable(ClanPlayer clanPlayer) throws SQLException {

    }

    public List<Clan> getAllClansFromTable() throws SQLException {
        return null;
    }

    public List<ClanPlayer> getAllUsersFromTable() throws SQLException {
        return null;
    }

    public List<String> getClanMembersPerClanFromTable(Clan clan) throws SQLException {
        return null;
    }

    public List<String> getClanAlliesPerClanFromTable(Clan clan) throws SQLException {
        return null;
    }

    public List<String> getClanEnemiesPerClanFromTable(Clan clan) throws SQLException {
        return null;
    }

    public List<Chest> getClanChestsPerClanFromTable(Clan clan) throws SQLException {
        return null;
    }

    public List<String> getPlayersWithAccessPerChestFromTable(Chest chest) throws SQLException {
        return null;
    }
}
