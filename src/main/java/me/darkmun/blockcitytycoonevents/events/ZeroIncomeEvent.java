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
    private Player pl;
    private final long nightTime = 18000;
    private final long dayTime = 6000;
    public long timer = 0;
    private Plugin WFXPlugin = Bukkit.getServer().getPluginManager().getPlugin("WFX-Business");
    private double currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
    private boolean running = false;
    private WrapperPlayServerUpdateTime wrapper = new WrapperPlayServerUpdateTime();

    public ZeroIncomeEvent(Player pl) { this.pl = pl; }
    public void run() {
        BlockCityTycoonEvents.setTimeToPlayer(nightTime, pl);
        running = true;
        currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
        WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", 0.0);
        WFXPlugin.saveConfig();
    }

    public void stop() {
        BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl);
        running = false;
        WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", currentIncome);
        WFXPlugin.saveConfig();
    }

    public boolean isRunning() { return running; }
    public Player getPlayer() { return  pl; }


}
