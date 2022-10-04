package me.darkmun.blockcitytycoonevents.events;

import com.comphenix.packetwrapper.WrapperPlayServerUpdateTime;
import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import net.minecraft.server.v1_12_R1.PacketPlayOutUpdateTime;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
    private WrapperPlayServerUpdateTime wrapper = new WrapperPlayServerUpdateTime();
    private int eventRunTaskID;
    private int timerTaskID;

    public ZeroIncomeEvent(Player pl) {
        this.pl = pl;
        Bukkit.getLogger().info("DataBaseIncome." + pl.getName() + ".total-income");
        Bukkit.getLogger().info(String.valueOf(WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income")));
        currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
    }
    public void run() {
        Bukkit.getLogger().info("Run: name: " + getPlayer().getName());
        Bukkit.getLogger().info("Run: uniqueID: " + getPlayer().getUniqueId().toString());
        Bukkit.getLogger().info("Run: isRunning: " + isRunning());
        Bukkit.getLogger().info("Run: timer: " + timer);
        Bukkit.getLogger().info("Run: eventRunTaskID: " + getEventRunTaskID());
        Bukkit.getLogger().info("Run: timerTaskID: " + getTimerTaskID());

        Bukkit.getScheduler().cancelTask(timerTaskID);
        Bukkit.getLogger().info("Run (cancel timerTask): eventRunTaskID: " + getEventRunTaskID());
        Bukkit.getLogger().info("Run (cancel timerTask): timerTaskID: " + getTimerTaskID());

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

    public boolean isRunning() { return running; }
    public Player getPlayer() { return  pl; }

    public int getEventRunTaskID() { return eventRunTaskID; }

    public int getTimerTaskID() { return timerTaskID; }

    //public long getRemainingTime() { return remainingTime; }

    public void setPlayer(Player player) { pl = player; }
    public void setEventRunTaskID(int taskID) { eventRunTaskID = taskID; }
    public void setTimerTaskID(int taskID) { timerTaskID = taskID; }
    //public void setRemainingTime(long remainingTime) { this.remainingTime = remainingTime; }

    public void cancelEventRunTask() {
        Bukkit.getScheduler().cancelTask(eventRunTaskID);
    }

    public void cancelTimerTask() {
        Bukkit.getScheduler().cancelTask(timerTaskID);
    }


}
