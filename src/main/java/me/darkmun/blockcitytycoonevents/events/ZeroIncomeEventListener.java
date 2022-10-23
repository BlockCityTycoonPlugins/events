package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ZeroIncomeEventListener implements Listener {

    private Set<ZeroIncomeEventWorker> zeroIncomeEventWorkers = new HashSet<>();
    private final long minSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong("zero-income-event.time-to-next-run.min");
    private final long maxSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong("zero-income-event.time-to-next-run.max");
    private final long TICKS_PER_SECOND = 20;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        if (zeroIncomeEventWorkers.stream().noneMatch(worker -> worker.getPlayerUniqueId().equals(pl.getUniqueId()))) {
            BlockCityTycoonEvents.setTimeToPlayer(ZeroIncomeEvent.DAY_TIME, pl);
            ZeroIncomeEventWorker zeroIncomeEventWorker = new ZeroIncomeEventWorker(pl);
            zeroIncomeEventWorker.runEventIn(ThreadLocalRandom.current().nextLong(minSec * TICKS_PER_SECOND, maxSec * TICKS_PER_SECOND));

            zeroIncomeEventWorkers.add(zeroIncomeEventWorker);
        }
        else {
            ZeroIncomeEventWorker zeroIncomeEventWorker = Objects.requireNonNull(zeroIncomeEventWorkers.stream().filter(worker -> worker.getPlayerUniqueId().equals(pl.getUniqueId())).findAny().orElse(null));

            if(zeroIncomeEventWorker.eventIsRunning()) {
                BlockCityTycoonEvents.setTimeToPlayer(ZeroIncomeEvent.NIGHT_TIME, pl);
            }
            else {
                BlockCityTycoonEvents.setTimeToPlayer(ZeroIncomeEvent.DAY_TIME, pl);
            }
            zeroIncomeEventWorker.continueEvent();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player pl = e.getPlayer();
        ZeroIncomeEventWorker zeroIncomeEventWorker = Objects.requireNonNull(zeroIncomeEventWorkers.stream().filter(worker -> worker.getPlayerUniqueId().equals(pl.getUniqueId())).findAny().orElse(null));

        zeroIncomeEventWorker.pauseEvent();
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        Player pl = e.getPlayer();
        ZeroIncomeEventWorker zeroIncomeEventWorker = Objects.requireNonNull(zeroIncomeEventWorkers.stream().filter(worker -> worker.getPlayerUniqueId().equals(pl.getUniqueId())).findAny().orElse(null));

        if (zeroIncomeEventWorker.eventIsRunning()) {
            zeroIncomeEventWorker.stopEvent();
            zeroIncomeEventWorker.runEventIn(ThreadLocalRandom.current().nextLong(minSec * TICKS_PER_SECOND, maxSec * TICKS_PER_SECOND));
        }
        e.setCancelled(true);
    }
}
