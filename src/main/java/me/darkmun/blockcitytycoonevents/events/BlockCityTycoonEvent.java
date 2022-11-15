package me.darkmun.blockcitytycoonevents.events;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public interface BlockCityTycoonEvent {
    long NIGHT_TIME = 18000;
    long DAY_TIME = 6000;
    Plugin BCTEconomyPlugin = Bukkit.getServer().getPluginManager().getPlugin("BlockCityTycoonEconomy");

    void run();

    void stop();

    boolean isRunning();

    UUID getPlayerUUID();

    String getName();
}
