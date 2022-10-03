package me.darkmun.blockcitytycoonevents;

import com.comphenix.packetwrapper.WrapperPlayServerUpdateTime;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import me.darkmun.blockcitytycoonevents.events.ZeroIncomeEvent;
import net.minecraft.server.v1_12_R1.PacketPlayOutUpdateTime;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        plugin = this;
        getCommand("zeroincome").setExecutor(this);
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.UPDATE_TIME) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerUpdateTime wrapper = new WrapperPlayServerUpdateTime(event.getPacket());
                if (wrapper.getTimeOfDay() == -1000) {
                    event.setCancelled(true);
                }
            }
        });
        getServer().getPluginManager().registerEvents();
    }

    @Override
    public void onDisable() {
        //saveConfig();
    }

    public static BlockCityTycoonEvents getPlugin() { return plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (sender instanceof Player) {
            Player pl = (Player) sender;
            ZeroIncomeEvent zeroIncomeEvent = new ZeroIncomeEvent(pl);
            if (!zeroIncomeEvent.isRunning()) {
                zeroIncomeEvent.run();
            }
            else {
                zeroIncomeEvent.stop();
            }
            /*getLogger().info(pl.getWorld().getName());
            CraftPlayer cp = (CraftPlayer)pl;
            //cp.getHandle().world.setDayTime(nightTime);


            if (cp.getHandle().playerConnection != null) {
                cp.getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateTime(cp.getHandle().world.getTime() + 1000, cp.getHandle().getPlayerTime() + 1000, cp.getHandle().world.getGameRules().getBoolean("doDaylightCycle")));
            }*/



            /*PacketListener packetListenerNight = new PacketAdapter(this, PacketType.Play.Server.UPDATE_TIME) {
                final long nightTime = 18000;
                Player plTime;
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();

                @Override
                public void onPacketSending(PacketEvent event) {
                    plTime = event.getPlayer();
                    PacketContainer packet = event.getPacket();

                    try {
                        manager.sendServerPacket(plTime, packet);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    //plTime = event.getPlayer();
                    //WrapperPlayServerUpdateTime wrapper = new WrapperPlayServerUpdateTime(event.getPacket());
                    //wrapper.setTimeOfDay(nightTime);
                    //wrapper.sendPacket(plTime);
                }
            };

            PacketListener packetListenerDay = new PacketAdapter(this, PacketType.Play.Server.UPDATE_TIME) {
                final long dayTime = 6000;
                Player plTime;
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();

                @Override
                public void onPacketSending(PacketEvent event) {
                    plTime = event.getPlayer();
                    PacketContainer packet = event.getPacket();
                    try {
                        manager.sendServerPacket(plTime, packet);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    //plTime = event.getPlayer();
                    //WrapperPlayServerUpdateTime wrapper = new WrapperPlayServerUpdateTime(event.getPacket());
                    //wrapper.setTimeOfDay(dayTime);
                    //wrapper.sendPacket(plTime);
                }
            };*/

            /*Plugin WFXPlugin = Bukkit.getServer().getPluginManager().getPlugin("WFX-Business");
            if (!zeroIncome) {
                currentIncome = WFXPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
                WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", 0.0);
                zeroIncome = true;

                wrapper.setTimeOfDay(nightTime);
                taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> wrapper.sendPacket(pl),0, 0);
                //manager.removePacketListener(packetListenerDay);
                //manager.addPacketListener(packetListenerNight);
            }
            else {
                WFXPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", currentIncome);
                zeroIncome = false;

                Bukkit.getScheduler().cancelTask(taskID);
                //manager.removePacketListener(packetListenerNight);
                //manager.addPacketListener(packetListenerDay);
            }

            WFXPlugin.saveConfig();*/
            return true;
        }
        return false;
    }

    public static void setTimeToPlayer(long time, Player pl) {
        CraftPlayer cp = (CraftPlayer)pl;
        if (cp.getHandle().playerConnection != null) {
            cp.getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateTime(cp.getHandle().world.getTime(), time, false));
        }
    }



}
