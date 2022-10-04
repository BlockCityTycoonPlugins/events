package me.darkmun.blockcitytycoonevents;

import com.comphenix.packetwrapper.WrapperPlayServerUpdateTime;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import me.darkmun.blockcitytycoonevents.events.ZeroIncomeEvent;
import me.darkmun.blockcitytycoonevents.events.ZeroIncomeEventListener;
import net.minecraft.server.v1_12_R1.PacketPlayOutUpdateTime;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.List;

public final class BlockCityTycoonEvents extends JavaPlugin implements CommandExecutor, Listener {
    private static BlockCityTycoonEvents plugin;
    private boolean isRunning = false;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        plugin = this;

        setTime(1000);
        enableZeroIncomeEvent(getConfig().getBoolean("enable-zero-income-event"));

        getLogger().info("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled.");
    }

    public void enableZeroIncomeEvent(boolean isEnable) {
        if (isEnable) {
            setTime(19000);
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.UPDATE_TIME) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrapperPlayServerUpdateTime wrapper = new WrapperPlayServerUpdateTime(event.getPacket());
                    if (wrapper.getTimeOfDay() != -18000 && wrapper.getTimeOfDay() != -6000) {
                        event.setCancelled(true);
                    }
                }
            });
            getServer().getPluginManager().registerEvents(new ZeroIncomeEventListener(), this);
        }
    }

    public static BlockCityTycoonEvents getPlugin() { return plugin; }

    public static void setTimeToPlayer(long time, Player pl) {
        CraftPlayer cp = (CraftPlayer)pl;
        if (cp.getHandle().playerConnection != null) {
            cp.getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateTime(cp.getHandle().world.getTime(), time, false));
        }
    }

    public static void setTime(long time) {
        getPlugin().getServer().getWorld("world").setTime(time);
    }
}
