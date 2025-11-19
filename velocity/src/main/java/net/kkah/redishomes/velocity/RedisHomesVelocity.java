package net.kkah.redishomes.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kkah.redishomes.velocity.command.HomeCommand;
import net.kkah.redishomes.velocity.service.ProxyRedisService;
import net.kkah.redishomes.velocity.service.ProxyStorageService;
import org.slf4j.Logger;

@Plugin(
        id = "redishomes",
        name = "RedisHomes",
        version = "1.0.0",
        authors = {"KKAH"}
)
public class RedisHomesVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private ProxyStorageService storageService;
    private ProxyRedisService redisService;

    @Inject
    public RedisHomesVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.storageService = new ProxyStorageService();
        this.redisService = new ProxyRedisService();

        this.storageService.initialize();
        this.redisService.initialize();

        this.server.getCommandManager().register(
                this.server.getCommandManager().metaBuilder("home").build(),
                new HomeCommand(server, storageService, redisService)
        );
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (redisService != null) redisService.shutdown();
        if (storageService != null) storageService.shutdown();
    }
}