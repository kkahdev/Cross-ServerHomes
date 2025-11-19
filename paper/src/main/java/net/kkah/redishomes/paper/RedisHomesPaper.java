package net.kkah.redishomes.paper;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import net.kkah.redishomes.paper.command.DelHomeCommand;
import net.kkah.redishomes.paper.command.SetHomeCommand;
import net.kkah.redishomes.paper.listener.JoinListener;
import net.kkah.redishomes.paper.service.BackendRedisService;
import net.kkah.redishomes.paper.service.BackendStorageService;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class RedisHomesPaper extends JavaPlugin {

    private BackendStorageService storageService;
    private BackendRedisService redisService;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        this.storageService = new BackendStorageService(this);
        this.storageService.initialize();

        this.redisService = new BackendRedisService(this);
        this.redisService.initialize();

        new SetHomeCommand(this).register();
        new DelHomeCommand(this).register();

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        if (redisService != null) redisService.shutdown();
        if (storageService != null) storageService.shutdown();
    }
}