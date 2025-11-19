package net.kkah.redishomes.paper.listener;

import net.kkah.redishomes.paper.RedisHomesPaper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record JoinListener(RedisHomesPaper plugin) implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getRedisService().processPendingTeleport(event.getPlayer());
    }
}