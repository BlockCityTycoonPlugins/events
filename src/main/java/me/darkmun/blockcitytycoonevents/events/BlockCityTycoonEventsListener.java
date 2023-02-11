package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.double_income_economic_growth.EconomicGrowthEvent;
import me.darkmun.blockcitytycoonevents.events.gold_rush.GoldRushEvent;
import me.darkmun.blockcitytycoonevents.events.rain.RainEvent;
import me.darkmun.blockcitytycoonevents.events.zero_income_night.NightEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.*;

public class BlockCityTycoonEventsListener implements Listener {

    private static final Set<BlockCityTycoonEventWorker[]> blockCityTycoonEventWorkers = new HashSet<>();
    public static final long TICKS_PER_SECOND = 20;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        BlockCityTycoonEvents.setTimeToPlayer(NightEvent.DAY_TIME, pl);

        FileConfiguration config = BlockCityTycoonEvents.getPlayerEventsConfig().getConfig();
        if (blockCityTycoonEventWorkers.stream().noneMatch(worker -> {
            if (worker != null) {
                if (worker[0] != null) {
                    return worker[0].getPlayerUUID().equals(pl.getUniqueId());
                }
            }
            return false;
        })) {

            BlockCityTycoonEventWorker[] workers = new BlockCityTycoonEventWorker[8];
            int i = 0;

            if (BlockCityTycoonEvents.getPlugin().getConfig().getBoolean("night-event.enable")) {
                BlockCityTycoonEventWorker nightEventWorker = new BlockCityTycoonEventWorker(new NightEvent(pl.getUniqueId(), pl.getName()));
                workers[i] = nightEventWorker;
                i++;
            }
            if (BlockCityTycoonEvents.getPlugin().getConfig().getBoolean("economic-growth-event.enable")) {
                BlockCityTycoonEventWorker economicGrowthEventWorker = new BlockCityTycoonEventWorker(new EconomicGrowthEvent(pl.getUniqueId(), pl.getName()));
                workers[i] = economicGrowthEventWorker;
                i++;
            }
            if (BlockCityTycoonEvents.getPlugin().getConfig().getBoolean("gold-rush-event.enable")) {
                BlockCityTycoonEventWorker goldRushEventWorker = new BlockCityTycoonEventWorker(new GoldRushEvent(pl.getUniqueId()));
                workers[i] = goldRushEventWorker;
                i++;
            }
            if (BlockCityTycoonEvents.getPlugin().getConfig().getBoolean("rain-event.enable")) {
                BlockCityTycoonEventWorker rainEventWorker = new BlockCityTycoonEventWorker(new RainEvent(pl.getUniqueId(), pl.getName()));
                workers[i] = rainEventWorker;
                i++;
            }

            for (BlockCityTycoonEventWorker worker : workers) {
                if (worker != null) {
                    String playerEventPath = String.format("%s.%s", pl.getUniqueId().toString(), worker.getBCTEvent().getName());
                    if (!config.getBoolean(playerEventPath + ".disable")) {
                        if (config.contains(playerEventPath)) {
                            if (config.getBoolean(String.format("%s.%s.running", pl.getUniqueId().toString(), worker.getBCTEvent().getName()))) {
                                worker.createEventWork(1, config.getLong(String.format("%s.%s.remaining-time-to-end", pl.getUniqueId().toString(), worker.getBCTEvent().getName())));
                            } else {
                                worker.createEventWork(config.getLong(String.format("%s.%s.remaining-time-to-run", pl.getUniqueId().toString(), worker.getBCTEvent().getName())));
                            }
                        } else {
                            worker.createEventWork();
                        }
                    }
                }
            }

            blockCityTycoonEventWorkers.add(workers);
        }
        else {
            BlockCityTycoonEventWorker[] BCTEWorkers = blockCityTycoonEventWorkers.stream().filter(worker -> {
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
                        String playerEventPath = String.format("%s.%s", pl.getUniqueId().toString(), worker.getBCTEvent().getName());
                        if (!config.getBoolean(playerEventPath + ".disable")) {
                            worker.continueEventWork();
                            worker.setOfflineWork(false);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player pl = e.getPlayer();
        FileConfiguration config = BlockCityTycoonEvents.getPlayerEventsConfig().getConfig();

        BlockCityTycoonEventWorker[] BCTEWorkers = blockCityTycoonEventWorkers.stream().filter(worker -> {
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
                    String playerEventPath = String.format("%s.%s", pl.getUniqueId().toString(), worker.getBCTEvent().getName());
                    if (!config.getBoolean(playerEventPath + ".disable")) {
                        worker.pauseEventWork();
                    }
                }
            }
        }
    }

    public static Set<BlockCityTycoonEventWorker[]> getBCTEventsWorker() {
        return blockCityTycoonEventWorkers;
    }

}
