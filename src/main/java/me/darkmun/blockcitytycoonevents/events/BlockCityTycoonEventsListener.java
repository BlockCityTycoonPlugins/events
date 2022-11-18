package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.double_income_economic_growth.EconomicGrowthEvent;
import me.darkmun.blockcitytycoonevents.events.gold_rush.GoldRushEvent;
import me.darkmun.blockcitytycoonevents.events.zero_income_night.NightEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.*;

public class BlockCityTycoonEventsListener implements Listener {

    private static Set<BlockCityTycoonEventWorker[]> blockCityTycoonEventWorkers = new HashSet<>();
    //public static final long MIN_SEC = BlockCityTycoonEvents.getPlugin().getConfig().getLong("night-event.time-to-next-run.min");
    //public static final long MAX_SEC = BlockCityTycoonEvents.getPlugin().getConfig().getLong("night-event.time-to-next-run.max");
    public static final long TICKS_PER_SECOND = 20;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        BlockCityTycoonEvents.setTimeToPlayer(NightEvent.DAY_TIME, pl);


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
                BlockCityTycoonEventWorker nightEventWorker = new BlockCityTycoonEventWorker(new NightEvent(pl.getUniqueId()));
                nightEventWorker.createEventWork();
                workers[i] = nightEventWorker;
                i++;
            }
            if (BlockCityTycoonEvents.getPlugin().getConfig().getBoolean("economic-growth-event.enable")) {
                BlockCityTycoonEventWorker economicGrowthEventWorker = new BlockCityTycoonEventWorker(new EconomicGrowthEvent(pl.getUniqueId()));
                economicGrowthEventWorker.createEventWork();
                workers[i] = economicGrowthEventWorker;
                i++;
            }
            if (BlockCityTycoonEvents.getPlugin().getConfig().getBoolean("gold-rush-event.enable")) {
                BlockCityTycoonEventWorker goldRushEventWorker = new BlockCityTycoonEventWorker(new GoldRushEvent(pl.getUniqueId()));
                goldRushEventWorker.createEventWork();
                workers[i] = goldRushEventWorker;
                i++;
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
                        worker.continueEventWork();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player pl = e.getPlayer();
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
                    worker.pauseEventWork();
                }
            }
        }
    }

    public static Set<BlockCityTycoonEventWorker[]> getBCTEventsWorker() {
        return blockCityTycoonEventWorkers;
    }

}
