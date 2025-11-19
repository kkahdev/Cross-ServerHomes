package net.kkah.redishomes.paper.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kkah.redishomes.common.model.Home;
import net.kkah.redishomes.common.storage.SQLQueries;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class BackendStorageService {

    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public BackendStorageService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/minecraft");
        config.setUsername("root");
        config.setPassword("password");
        config.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(config);
    }

    public CompletableFuture<Void> saveHome(Home home) {
        return CompletableFuture.runAsync(() -> {
            try (var conn = dataSource.getConnection();
                 var stmt = conn.prepareStatement(SQLQueries.INSERT_HOME)) {
                stmt.setString(1, home.owner().toString());
                stmt.setString(2, home.name());
                stmt.setString(3, home.server());
                stmt.setString(4, home.world());
                stmt.setDouble(5, home.x());
                stmt.setDouble(6, home.y());
                stmt.setDouble(7, home.z());
                stmt.setFloat(8, home.yaw());
                stmt.setFloat(9, home.pitch());
                // Duplicate key update params
                stmt.setString(10, home.server());
                stmt.setString(11, home.world());
                stmt.setDouble(12, home.x());
                stmt.setDouble(13, home.y());
                stmt.setDouble(14, home.z());
                stmt.setFloat(15, home.yaw());
                stmt.setFloat(16, home.pitch());

                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteHome(Home home) {
        return CompletableFuture.runAsync(() -> {
            try (var conn = dataSource.getConnection();
                 var stmt = conn.prepareStatement(SQLQueries.DELETE_HOME)) {
                stmt.setString(1, home.owner().toString());
                stmt.setString(2, home.name());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void shutdown() {
        if (dataSource != null) dataSource.close();
    }
}