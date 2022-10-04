package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ZeroIncomeEvent {
    private Player pl; //поменять на UUID потом
    private final long nightTime = 18000;
    private final long dayTime = 6000;
    public long timer = 0;
    public long remainingTime = 0;
    private Plugin WFXPlugin = Bukkit.getServer().getPluginManager().getPlugin("WFX-Business");
    private double currentIncome;
    private boolean running = false;
    private int eventRunTaskID;
    private int timerTaskID;

    public ZeroIncomeEvent(Player pl) {
        this.pl = pl;
        currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
    }
    public void run() {

        Bukkit.getScheduler().cancelTask(timerTaskID);

        BlockCityTycoonEvents.setTimeToPlayer(nightTime, pl);
        running = true;
        currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
        WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", 0.0);
        WFXPlugin.saveConfig();
    }

    public void stop() {
        BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl/*Bukkit.getServer().getPlayer(pl.getUniqueId())*/);
        running = false;
        WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", currentIncome);
        WFXPlugin.saveConfig();
    }

    public void cancelEventRunTask() {
        Bukkit.getScheduler().cancelTask(eventRunTaskID);
    }
    public void cancelTimerTask() {
        Bukkit.getScheduler().cancelTask(timerTaskID);
    }

    public boolean isRunning() { return running; }
    public Player getPlayer() { return  pl; }
    public int getEventRunTaskID() { return eventRunTaskID; }
    public int getTimerTaskID() { return timerTaskID; }

    public void setPlayer(Player player) { pl = player; }
    public void setEventRunTaskID(int taskID) { eventRunTaskID = taskID; }
    public void setTimerTaskID(int taskID) { timerTaskID = taskID; }


}
