package net.kkah.redishomes.paper.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kkah.redishomes.common.model.Home;
import net.kkah.redishomes.paper.RedisHomesPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public record DelHomeCommand(RedisHomesPaper plugin) {

    public void register() {
        new CommandAPICommand("delhome")
                .withArguments(new StringArgument("name"))
                .executesPlayer((player, args) -> {
                    String name = (String) args.get("name");

                    Home dummy = new Home(player.getUniqueId(), name, null, null, 0, 0, 0, 0, 0);

                    plugin.getStorageService().deleteHome(dummy)
                            .thenRun(() -> player.sendMessage(Component.text("Home deleted.", NamedTextColor.GREEN)));
                })
                .register();
    }
}