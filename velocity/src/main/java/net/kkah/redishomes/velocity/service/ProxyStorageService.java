package net.kkah.redishomes.velocity.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kkah.redishomes.common.model.Home;
import net.kkah.redishomes.common.storage.SQLQueries;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProxyStorageService {

    private HikariDataSource dataSource;

    public void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/minecraft");
        config.setUsername("root");
        config.setPassword("password");
        config.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(config);

        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(SQLQueries.INIT_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public CompletableFuture<Home> getHome(UUID uuid, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = dataSource.getConnection();
                 var stmt = conn.prepareStatement(SQLQueries.SELECT_HOME)) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, name);

                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Home(
                            uuid,
                            name,
                            rs.getString("server_id"),
                            rs.getString("world_name"),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z"),
                            rs.getFloat("yaw"),
                            rs.getFloat("pitch")
                    );
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void shutdown() {
        if (dataSource != null) dataSource.close();
    }
}