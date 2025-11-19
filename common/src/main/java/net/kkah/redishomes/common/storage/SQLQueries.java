package net.kkah.redishomes.common.storage;

public final class SQLQueries {
    public static final String INIT_TABLE = """
        CREATE TABLE IF NOT EXISTS homes (
            owner_uuid VARCHAR(36) NOT NULL,
            home_name VARCHAR(32) NOT NULL,
            server_id VARCHAR(32) NOT NULL,
            world_name VARCHAR(64) NOT NULL,
            x DOUBLE NOT NULL,
            y DOUBLE NOT NULL,
            z DOUBLE NOT NULL,
            yaw FLOAT NOT NULL,
            pitch FLOAT NOT NULL,
            PRIMARY KEY (owner_uuid, home_name)
        );
    """;

    public static final String INSERT_HOME =
            "INSERT INTO homes VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE server_id=?, world_name=?, x=?, y=?, z=?, yaw=?, pitch=?";

    public static final String DELETE_HOME = "DELETE FROM homes WHERE owner_uuid=? AND home_name=?";
    public static final String SELECT_HOME = "SELECT * FROM homes WHERE owner_uuid=? AND home_name=?";
    public static final String SELECT_ALL_HOMES = "SELECT home_name FROM homes WHERE owner_uuid=?";
}