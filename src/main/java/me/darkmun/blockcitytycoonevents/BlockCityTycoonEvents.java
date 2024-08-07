package me.darkmun.blockcitytycoonevents;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerUpdateTime;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import me.darkmun.blockcitytycoonevents.commands.ReloadCommand;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEvent;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventWorker;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener;
import me.darkmun.blockcitytycoonevents.events.donate.EventsWorkCommand;
import me.darkmun.blockcitytycoonevents.events.rain.PlaceOfRitualBlock;
import me.darkmun.blockcitytycoonevents.events.rain.RainEventStopper;
import me.darkmun.blockcitytycoonevents.events.zero_income_insomnia.InsomniaEventWorker;
import me.darkmun.blockcitytycoonevents.events.zero_income_night.NightEventStopper;
import net.minecraft.server.v1_12_R1.PacketPlayOutUpdateTime;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.ShortConsoleLogFormatter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.FileHandler;

public final class BlockCityTycoonEvents extends JavaPlugin implements CommandExecutor, Listener {
    private static BlockCityTycoonEvents plugin;
    private final PluginLogger donateLogger = new PluginLogger(this);
    private static final Config playerEventsConfig = new Config();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        playerEventsConfig.setup(getDataFolder(), "playerEventsData");
        playerEventsConfig.getConfig().options().copyDefaults(true);

        plugin = this;

        setTime(1000);

        if (getConfig().getBoolean("enable")) {

            donateLogger.setUseParentHandlers(false);
            try {
                File file = new File(getDataFolder().getPath() + "/logs");
                @SuppressWarnings("unused")
                boolean created = file.mkdirs();
                FileHandler fileHandler = new FileHandler(getDataFolder().getPath() + "/logs/donate%g.log", 524288, 50, true);
                fileHandler.setFormatter(new ShortConsoleLogFormatter(((CraftServer) getServer()).getServer()));
                donateLogger.addHandler(fileHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            setTime(19000);
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.UPDATE_TIME) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrapperPlayServerUpdateTime wrapper = new WrapperPlayServerUpdateTime(event.getPacket());
                    if (wrapper.getTimeOfDay() != -BlockCityTycoonEvent.NIGHT_TIME && wrapper.getTimeOfDay() != -BlockCityTycoonEvent.DAY_TIME) {
                        event.setCancelled(true);
                    }
                }
            });

            getServer().getPluginManager().registerEvents(new BlockCityTycoonEventsListener(), this);
            if (getConfig().getBoolean("night-event.enable")) {
                getServer().getPluginManager().registerEvents(new NightEventStopper(), this);
            }
            if (getConfig().getBoolean("insomnia-event.enable")) {
                getServer().getPluginManager().registerEvents(new InsomniaEventWorker(), this);
            }
            if (getConfig().getBoolean("rain-event.enable")) {
                getServer().getPluginManager().registerEvents(new RainEventStopper(), this);
                ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.BLOCK_CHANGE) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(event.getPacket());
                        int x = wrapper.getLocation().getX();
                        int y = wrapper.getLocation().getY();
                        int z = wrapper.getLocation().getZ();
                        PlaceOfRitualBlock place = RainEventStopper.getPlacesOfRitualBlocks(event.getPlayer()).stream().filter(block ->
                            x == block.getX() && y == block.getY() && z == block.getZ()).findAny().orElse(null);

                        if (place != null) {
                            if (place.isPlacing()) {
                                place.setPlacing(false);
                            }
                            else if (place.isRemoving()) {
                                place.setRemoving(false);
                            }
                            else {
                                event.setCancelled(true);
                            }
                        }
                    }
                });
            }

            getCommand("event").setExecutor(new EventsWorkCommand());
            getCommand("bctevents").setExecutor(new ReloadCommand());

            getLogger().info("Plugin enabled.");
        }
        else {
            getLogger().info("Plugin not enabled.");
        }
    }

    @Override
    public void onDisable() {
        for (OfflinePlayer pl : getServer().getOfflinePlayers()) {
            BlockCityTycoonEventWorker[] BCTEWorkers = BlockCityTycoonEventsListener.getBCTEventsWorker().stream().filter(worker -> {
                if (worker != null) {
                    if (worker[0] != null) {
                        return worker[0].getPlayerUUID().equals(pl.getUniqueId());
                    }
                }
                return false;
            }).findAny().orElse(null);

            if (BCTEWorkers != null) {
                for (BlockCityTycoonEventWorker worker : BCTEWorkers) {
                    if (worker != null) {
                        if (worker.isPaused()) {
                            worker.continueEventWork();
                        }
                        worker.stopEventWork(true);
                    }
                }
            }
        }

        Set<String> uuids = playerEventsConfig.getConfig().getKeys(false);
        for (String uuid : uuids) {
            playerEventsConfig.getConfig().set(uuid + ".insomnia-event.running", false);
        }
        playerEventsConfig.saveConfig();
        getLogger().info("Plugin disabled.");
    }

    public static BlockCityTycoonEvents getPlugin() { return plugin; }

    public static void setTimeToPlayer(long time, Player pl) {
        if (pl != null) {
            CraftPlayer cp = (CraftPlayer)pl;
            if (cp.getHandle().playerConnection != null ) {
                cp.getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateTime(cp.getHandle().world.getTime(), time, false));
            }
        }
    }

    public static void setTime(long time) {
        getPlugin().getServer().getWorld("world").setTime(time);
    }
    public PluginLogger getDonateLogger() {
        return donateLogger;
    }

    public static Config getPlayerEventsConfig() {
        return playerEventsConfig;
    }
}
