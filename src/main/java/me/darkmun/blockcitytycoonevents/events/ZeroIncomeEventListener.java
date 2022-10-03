package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;

public class ZeroIncomeEventListener implements Listener {
    private final long nightTime = 18000;
    private final long dayTime = 6000;
    //long timer = 0;
    private Set<ZeroIncomeEvent> zeroIncomeEvents = new HashSet<>();
    BukkitTask timerTask;
    BukkitTask eventRunTask;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        if (!pl.hasPlayedBefore() || (zeroIncomeEvents.isEmpty())) {
            ZeroIncomeEvent zeroIncomeEvent = new ZeroIncomeEvent(pl);
            //Random rnd = new Random();
            eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), zeroIncomeEvent::run, 1000); //добавить к рану add в set
            timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1); // добавлять в конфиг таймер
            BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl);
            zeroIncomeEvents.add(zeroIncomeEvent);
        }
        else {
            for (ZeroIncomeEvent zie : zeroIncomeEvents) {
                if (zie.getPlayer() == pl) {
                    ZeroIncomeEvent zeroIncomeEvent = zie;
                    break;
                }
            }
            if(zeroIncomeEvent.isRunning()) {
                BlockCityTycoonEvents.setTimeToPlayer(nightTime, pl);
            }
            else {
                BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl);
                eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.run(), 1000); //1000 - timer
                timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        timerTask.cancel();
        if ((1000-zeroIncomeEvent.timer) > 0) {
            eventRunTask.cancel();
            zeroIncomeEvent.timer = 0;
        }
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        if (zeroIncomeEvent.isRunning())
    }
}
