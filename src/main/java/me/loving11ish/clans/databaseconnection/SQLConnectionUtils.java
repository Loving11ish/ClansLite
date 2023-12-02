package me.loving11ish.clans.databaseconnection;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import me.loving11ish.clans.Clans;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

public class SQLConnectionUtils {

    private final FileConfiguration clansConfig = Clans.getPlugin().getConfig();

    private JdbcPooledConnectionSource connectionSource;

    public JdbcPooledConnectionSource getConnection() throws SQLException {
        if (this.connectionSource != null){
            this.connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
            this.connectionSource.setCheckConnectionsEveryMillis(60 * 1000);
            this.connectionSource.setTestBeforeGet(true);
            return connectionSource;
        }

        String host = clansConfig.getString("storage.mysql.host");
        String port = clansConfig.getString("storage.mysql.port");
        String database = clansConfig.getString("storage.mysql.database");
        String username = clansConfig.getString("storage.mysql.username");
        String password = clansConfig.getString("storage.mysql.password");
        boolean useSSL = clansConfig.getBoolean("storage.mysql.useSSL");

        String url = "jdbc:h2:tcp://" + host + ":" + port + "/" + database + ":" + username + ":" + password + "?useSSL=" + useSSL;

        this.connectionSource = new JdbcPooledConnectionSource(url);
        this.connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        this.connectionSource.setCheckConnectionsEveryMillis(60 * 1000);
        this.connectionSource.setTestBeforeGet(true);

        return this.connectionSource;
    }
}
