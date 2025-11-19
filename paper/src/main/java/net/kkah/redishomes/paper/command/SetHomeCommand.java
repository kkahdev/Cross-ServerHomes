package net.kkah.redishomes.paper.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kkah.redishomes.common.model.Home;
import net.kkah.redishomes.paper.RedisHomesPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public record SetHomeCommand(RedisHomesPaper plugin) {

    public void register() {
        new CommandAPICommand("sethome")
                .withOptionalArguments(new StringArgument("name"))
                .executesPlayer((player, args) -> {
                    String name = (String) args.getOptional("name").orElse("home");
                    var loc = player.getLocation();

                    // NOTE: Server ID should be fetched from config in production
                    String serverId = "survival_1";

                    Home home = new Home(
                            player.getUniqueId(),
                            name,
                            serverId,
                            loc.getWorld().getName(),
                            loc.getX(),
                            loc.getY(),
                            loc.getZ(),
                            loc.getYaw(),
                            loc.getPitch()
                    );

                    plugin.getStorageService().saveHome(home)
                            .thenRun(() -> player.sendMessage(Component.text("Home set!", NamedTextColor.GREEN)))
                            .exceptionally(ex -> {
                                player.sendMessage(Component.text("Error saving home.", NamedTextColor.RED));
                                return null;
                            });
                })
                .register();
    }
}