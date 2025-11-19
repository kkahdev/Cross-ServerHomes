package net.kkah.redishomes.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kkah.redishomes.common.model.Home;
import net.kkah.redishomes.common.model.TeleportRequest;
import net.kkah.redishomes.velocity.service.ProxyRedisService;
import net.kkah.redishomes.velocity.service.ProxyStorageService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public record HomeCommand(ProxyServer server, ProxyStorageService storage, ProxyRedisService redis) implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) return;

        String[] args = invocation.arguments();
        String homeName = args.length > 0 ? args[0] : "home";

        storage.getHome(player.getUniqueId(), homeName)
                .thenAccept(home -> handleHomeTeleport(player, home, homeName));
    }

    private void handleHomeTeleport(Player player, Home home, String homeName) {
        if (home == null) {
            player.sendMessage(Component.text("Home '" + homeName + "' not found.", NamedTextColor.RED));
            return;
        }

        TeleportRequest request = new TeleportRequest(
                player.getUniqueId(),
                home.server(),
                home.world(),
                home.x(),
                home.y(),
                home.z(),
                home.yaw(),
                home.pitch()
        );

        redis.sendTeleportRequest(request).thenRun(() -> {
            server.getServer(home.server()).ifPresentOrElse(registeredServer -> {
                player.getCurrentServer().ifPresent(current -> {
                    if (!current.getServerInfo().getName().equals(home.server())) {
                        player.createConnectionRequest(registeredServer).connect();
                    }
                });
            }, () -> player.sendMessage(Component.text("Target server offline.", NamedTextColor.RED)));
        });
    }
}