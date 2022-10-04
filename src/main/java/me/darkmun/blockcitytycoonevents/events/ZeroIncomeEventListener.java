package me.darkmun.blockcitytycoonevents.events;

import com.google.common.collect.Iterables;
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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ZeroIncomeEventListener implements Listener {
    private final long nightTime = 18000;
    private final long dayTime = 6000;
    //long remainingTime;
    //long timer = 0;
    private Set<ZeroIncomeEvent> zeroIncomeEvents = new HashSet<>();
    private BukkitTask timerTask;
    private BukkitTask eventRunTask;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        Bukkit.getLogger().info(String.valueOf(zeroIncomeEvents.size()));
        for(ZeroIncomeEvent zie : zeroIncomeEvents) {
            Bukkit.getLogger().info("zeroIncomeEvents: name: " + zie.getPlayer().getName());
            Bukkit.getLogger().info("zeroIncomeEvents: uniqueID: " + zie.getPlayer().getUniqueId().toString());
            Bukkit.getLogger().info("zeroIncomeEvents: isRunning: " + zie.isRunning());
            Bukkit.getLogger().info("zeroIncomeEvents: timer: " + zie.timer);
            Bukkit.getLogger().info("zeroIncomeEvents: eventRunTaskID: " + zie.getEventRunTaskID());
            Bukkit.getLogger().info("zeroIncomeEvents: timerTaskID: " + zie.getTimerTaskID());
        }

        if (zeroIncomeEvents.stream().noneMatch(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId()))) {
            Bukkit.getLogger().info("Wasn't matched");
            BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl);
            ZeroIncomeEvent zeroIncomeEvent = new ZeroIncomeEvent(pl);
            zeroIncomeEvent.remainingTime = ThreadLocalRandom.current().nextLong(400, 800);
            Bukkit.getLogger().info("Join: RemainingTime: " + zeroIncomeEvent.remainingTime);
            eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), zeroIncomeEvent::run, zeroIncomeEvent.remainingTime);
            timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1); // добавлять в конфиг таймер

            zeroIncomeEvent.setEventRunTaskID(eventRunTask.getTaskId());
            zeroIncomeEvent.setTimerTaskID(timerTask.getTaskId());

            Bukkit.getLogger().info("Join (wasn't matched): eventRunTaskID: " + zeroIncomeEvent.getEventRunTaskID());
            Bukkit.getLogger().info("Join (wasn't matched): timerTaskID: " + zeroIncomeEvent.getTimerTaskID());

            zeroIncomeEvents.add(zeroIncomeEvent);
        }
        else {
            Bukkit.getLogger().info("Was matched");
            ZeroIncomeEvent zeroIncomeEvent = zeroIncomeEvents.stream().filter(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId())).findAny().orElse(null);
            zeroIncomeEvent.setPlayer(pl);

            Bukkit.getLogger().info("Join (was matched): name: " + zeroIncomeEvent.getPlayer().getName());
            Bukkit.getLogger().info("Join (was matched): uniqueID: " + zeroIncomeEvent.getPlayer().getUniqueId().toString());
            Bukkit.getLogger().info("Join (was matched): isRunning: " + zeroIncomeEvent.isRunning());
            Bukkit.getLogger().info("Join (was matched): timer: " + zeroIncomeEvent.timer);
            Bukkit.getLogger().info("Join (was matched): eventRunTaskID: " + zeroIncomeEvent.getEventRunTaskID());
            Bukkit.getLogger().info("Join (was matched): timerTaskID: " + zeroIncomeEvent.getTimerTaskID());

            Bukkit.getLogger().info(String.valueOf(eventRunTask.getTaskId()));
            if(zeroIncomeEvent.isRunning()) {
                BlockCityTycoonEvents.setTimeToPlayer(nightTime, pl);
            }
            else {
                BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl);
                zeroIncomeEvent.remainingTime -= zeroIncomeEvent.timer;

                Bukkit.getLogger().info("remaining time: " + zeroIncomeEvent.remainingTime);

                eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), zeroIncomeEvent::run, zeroIncomeEvent.remainingTime); //1000 - timer

                zeroIncomeEvent.timer = 0;
                timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1);



                zeroIncomeEvent.setEventRunTaskID(eventRunTask.getTaskId());
                zeroIncomeEvent.setTimerTaskID(timerTask.getTaskId());

                Bukkit.getLogger().info("Join (was matched) (not running): eventRunTaskID: " + zeroIncomeEvent.getEventRunTaskID());
                Bukkit.getLogger().info("Join (was matched) (not running): timerTaskID: " + zeroIncomeEvent.getTimerTaskID());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player pl = e.getPlayer();
        ZeroIncomeEvent zeroIncomeEvent = zeroIncomeEvents.stream().filter(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId())).findAny().orElse(null);

        Bukkit.getLogger().info("Quit: name: " + zeroIncomeEvent.getPlayer().getName());
        Bukkit.getLogger().info("Quit: uniqueID: " + zeroIncomeEvent.getPlayer().getUniqueId().toString());
        Bukkit.getLogger().info("Quit: isRunning: " + zeroIncomeEvent.isRunning());
        Bukkit.getLogger().info("Quit: timer: " + zeroIncomeEvent.timer);
        Bukkit.getLogger().info("Quit: eventRunTaskID: " + zeroIncomeEvent.getEventRunTaskID());
        Bukkit.getLogger().info("Quit: timerTaskID: " + zeroIncomeEvent.getTimerTaskID());

        zeroIncomeEvent.cancelTimerTask();
        Bukkit.getLogger().info("Quit (cancel timerTask): eventRunTaskID: " + zeroIncomeEvent.getEventRunTaskID());
        Bukkit.getLogger().info("Quit (cancel timerTask): timerTaskID: " + zeroIncomeEvent.getTimerTaskID());
        if (((zeroIncomeEvent.remainingTime - zeroIncomeEvent.timer) > 0) && (!zeroIncomeEvent.isRunning())) {
            zeroIncomeEvent.cancelEventRunTask();
            Bukkit.getLogger().info("Quit (cancel eventRunTask): eventRunTaskID: " + zeroIncomeEvent.getEventRunTaskID());
            Bukkit.getLogger().info("Quit (cancel eventRunTask): timerTaskID: " + zeroIncomeEvent.getTimerTaskID());
            //zeroIncomeEvent.timer = 0;
        }
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        Player pl = e.getPlayer();
        ZeroIncomeEvent zeroIncomeEvent = zeroIncomeEvents.stream().filter(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId())).findAny().orElse(null);
        Bukkit.getLogger().info("OnBed: name: " + zeroIncomeEvent.getPlayer().getName());
        Bukkit.getLogger().info("OnBed: uniqueID: " + zeroIncomeEvent.getPlayer().getUniqueId().toString());
        Bukkit.getLogger().info("OnBed: isRunning: " + zeroIncomeEvent.isRunning());
        Bukkit.getLogger().info("OnBed: timer: " + zeroIncomeEvent.timer);
        Bukkit.getLogger().info("OnBed: eventRunTaskID: " + zeroIncomeEvent.getEventRunTaskID());
        Bukkit.getLogger().info("OnBed: timerTaskID: " + zeroIncomeEvent.getTimerTaskID());

        if (zeroIncomeEvent.isRunning()) {
            zeroIncomeEvent.stop();
            zeroIncomeEvent.remainingTime = ThreadLocalRandom.current().nextLong(400, 800);
            Bukkit.getLogger().info("OnBed: RemainingTime: " + zeroIncomeEvent.remainingTime);
            eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), zeroIncomeEvent::run, zeroIncomeEvent.remainingTime);
            zeroIncomeEvent.timer = 0;
            timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1);

            zeroIncomeEvent.setEventRunTaskID(eventRunTask.getTaskId());
            zeroIncomeEvent.setTimerTaskID(timerTask.getTaskId());
        }
        e.setCancelled(true);
    }
}
