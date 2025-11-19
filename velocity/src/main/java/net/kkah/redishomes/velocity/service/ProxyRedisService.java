package net.kkah.redishomes.velocity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import net.kkah.redishomes.common.model.TeleportRequest;

import java.util.concurrent.CompletableFuture;

public class ProxyRedisService {

    private static final String CHANNEL = "redishomes:teleport";
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private final ObjectMapper mapper = new ObjectMapper();

    public void initialize() {
        this.redisClient = RedisClient.create("redis://localhost:6379");
        this.connection = redisClient.connect();
    }

    public CompletableFuture<Void> sendTeleportRequest(TeleportRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                String json = mapper.writeValueAsString(request);
                connection.async().publish(CHANNEL, json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        if (connection != null) connection.close();
        if (redisClient != null) redisClient.shutdown();
    }
}