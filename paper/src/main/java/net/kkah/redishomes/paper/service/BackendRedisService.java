package net.kkah.redishomes.paper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import net.kkah.redishomes.common.model.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BackendRedisService {

    private final JavaPlugin plugin;
    private final ObjectMapper mapper = new ObjectMapper();
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;

    // Cache requests for players currently connecting
    private final Cache<UUID, TeleportRequest> pendingTeleports = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .build();

    public BackendRedisService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        this.redisClient = RedisClient.create("redis://localhost:6379");
        this.pubSubConnection = redisClient.connectPubSub();

        pubSubConnection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                if (channel.equals("redishomes:teleport")) {
                    handleTeleportMessage(message);
                }
            }
        });

        pubSubConnection.async().subscribe("redishomes:teleport");
    }

    private void handleTeleportMessage(String json) {
        try {
            TeleportRequest request = mapper.readValue(json, TeleportRequest.class);
            // Ignore if not for this server
            // Note: In a real env, server ID should be configurable in config.yml
            // Assuming "server_id" matches folder name or configured ID
            // Here we blindly accept or check a config value

            Player player = Bukkit.getPlayer(request.playerUuid());
            if (player != null && player.isOnline()) {
                // Player is already here, teleport immediately
                Bukkit.getScheduler().runTask(plugin, () -> applyTeleport(player, request));
            } else {
                // Player connecting, cache it
                pendingTeleports.put(request.playerUuid(), request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processPendingTeleport(Player player) {
        TeleportRequest request = pendingTeleports.getIfPresent(player.getUniqueId());
        if (request != null) {
            applyTeleport(player, request);
            pendingTeleports.invalidate(player.getUniqueId());
        }
    }

    private void applyTeleport(Player player, TeleportRequest req) {
        org.bukkit.World world = Bukkit.getWorld(req.worldName());
        if (world != null) {
            Location loc = new Location(world, req.x(), req.y(), req.z(), req.yaw(), req.pitch());
            player.teleportAsync(loc);
        }
    }

    public void shutdown() {
        if (pubSubConnection != null) pubSubConnection.close();
        if (redisClient != null) redisClient.shutdown();
    }
}