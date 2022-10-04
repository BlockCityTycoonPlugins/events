package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
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

        if (zeroIncomeEvents.stream().noneMatch(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId()))) {
            BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl);
            ZeroIncomeEvent zeroIncomeEvent = new ZeroIncomeEvent(pl);
            zeroIncomeEvent.remainingTime = ThreadLocalRandom.current().nextLong(400, 800);
            eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), zeroIncomeEvent::run, zeroIncomeEvent.remainingTime);
            timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1); // добавлять в конфиг таймер

            zeroIncomeEvent.setEventRunTaskID(eventRunTask.getTaskId());
            zeroIncomeEvent.setTimerTaskID(timerTask.getTaskId());

            zeroIncomeEvents.add(zeroIncomeEvent);
        }
        else {
            ZeroIncomeEvent zeroIncomeEvent = zeroIncomeEvents.stream().filter(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId())).findAny().orElse(null);
            zeroIncomeEvent.setPlayer(pl);

            Bukkit.getLogger().info(String.valueOf(eventRunTask.getTaskId()));
            if(zeroIncomeEvent.isRunning()) {
                BlockCityTycoonEvents.setTimeToPlayer(nightTime, pl);
            }
            else {
                BlockCityTycoonEvents.setTimeToPlayer(dayTime, pl);
                zeroIncomeEvent.remainingTime -= zeroIncomeEvent.timer;

                eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), zeroIncomeEvent::run, zeroIncomeEvent.remainingTime); //1000 - timer

                zeroIncomeEvent.timer = 0;
                timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1);



                zeroIncomeEvent.setEventRunTaskID(eventRunTask.getTaskId());
                zeroIncomeEvent.setTimerTaskID(timerTask.getTaskId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player pl = e.getPlayer();
        ZeroIncomeEvent zeroIncomeEvent = zeroIncomeEvents.stream().filter(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId())).findAny().orElse(null);

        zeroIncomeEvent.cancelTimerTask();
        if (((zeroIncomeEvent.remainingTime - zeroIncomeEvent.timer) > 0) && (!zeroIncomeEvent.isRunning())) {
            zeroIncomeEvent.cancelEventRunTask();
        }
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        Player pl = e.getPlayer();
        ZeroIncomeEvent zeroIncomeEvent = zeroIncomeEvents.stream().filter(zie -> zie.getPlayer().getUniqueId().equals(pl.getUniqueId())).findAny().orElse(null);

        if (zeroIncomeEvent.isRunning()) {
            zeroIncomeEvent.stop();
            zeroIncomeEvent.remainingTime = ThreadLocalRandom.current().nextLong(400, 800);
            eventRunTask = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), zeroIncomeEvent::run, zeroIncomeEvent.remainingTime);
            zeroIncomeEvent.timer = 0;
            timerTask = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> zeroIncomeEvent.timer++, 0, 1);

            zeroIncomeEvent.setEventRunTaskID(eventRunTask.getTaskId());
            zeroIncomeEvent.setTimerTaskID(timerTask.getTaskId());
        }
        e.setCancelled(true);
    }
}
