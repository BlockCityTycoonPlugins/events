package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class ZeroIncomeEvent {
    private UUID plUUID; //поменять на UUID потом
    public static final long NIGHT_TIME = 18000;
    public static final long DAY_TIME = 6000;
    private final Plugin WFXPlugin = Bukkit.getServer().getPluginManager().getPlugin("WFX-Business");
    private double currentIncome;
    private boolean running = false;
    public ZeroIncomeEvent(UUID plUUID) {
        this.plUUID = plUUID;
        currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + Bukkit.getPlayer(plUUID).getName() + ".total-income");
    }
    public void run() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);

        BlockCityTycoonEvents.setTimeToPlayer(NIGHT_TIME, pl);
        running = true;
        currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
        WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", 0.0);
        WFXPlugin.saveConfig();
    }

    public void stop() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);
        BlockCityTycoonEvents.setTimeToPlayer(DAY_TIME, pl/*Bukkit.getServer().getPlayer(pl.getUniqueId())*/);
        running = false;
        WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", currentIncome);
        WFXPlugin.saveConfig();
    }

    public boolean isRunning() { return running; }


}
