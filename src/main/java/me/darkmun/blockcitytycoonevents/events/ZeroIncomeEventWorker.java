package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ZeroIncomeEventWorker {

    private boolean works = false;
    private boolean paused = false;
    private UUID plUUID;
    private long timer = 0;
    private long remainingTime = 0;
    private int eventRunTaskID;
    private int timerTaskID;
    private ZeroIncomeEvent zeroIncomeEvent;

    public ZeroIncomeEventWorker(Player player) {
        plUUID = player.getUniqueId();
        zeroIncomeEvent = new ZeroIncomeEvent(plUUID);
    }

    public void runEventIn(long remainingTime) {
        Bukkit.getLogger().info("Remaining time: " + remainingTime);
        works = true;
        this.remainingTime = remainingTime;
        eventRunTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
            Bukkit.getScheduler().cancelTask(timerTaskID);
            timer = 0;
            zeroIncomeEvent.run();
        }, this.remainingTime).getTaskId();
        timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> timer++, 0, 1).getTaskId(); // добавлять в конфиг таймер
    }

    public void pauseEvent() {
        paused = true;
        if (!works) {
            Bukkit.getLogger().info("Ивент не может встать на паузу, т.к. он не собирается запускаться.");
        }
        else {
            Bukkit.getScheduler().cancelTask(timerTaskID);
            if (((remainingTime - timer) > 0) && (!zeroIncomeEvent.isRunning())) {
                Bukkit.getScheduler().cancelTask(eventRunTaskID);
                remainingTime -= timer;
                timer = 0;
            }
        }
    }

    public void continueEvent() {
        Bukkit.getLogger().info("Remaining time: " + remainingTime);
        Player pl = Bukkit.getServer().getPlayer(plUUID);

        if (!paused) {
            Bukkit.getLogger().info("Ивент не может продолжиться, т.к. он не стоит на паузе.");
        }
        else if(!zeroIncomeEvent.isRunning()) {
            BlockCityTycoonEvents.setTimeToPlayer(ZeroIncomeEvent.DAY_TIME, pl);

            eventRunTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                Bukkit.getScheduler().cancelTask(timerTaskID);
                timer = 0;
                zeroIncomeEvent.run();
            }, remainingTime).getTaskId(); //1000 - timer
            timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> timer++, 0, 1).getTaskId();
        }
        paused = false;
    }

    public void stopEvent() {
        if (!works) {
            Bukkit.getLogger().info("Ивент не может быть остановлен, т.к. он не собирается запускаться.");
        }
        else if (zeroIncomeEvent.isRunning()) {
            zeroIncomeEvent.stop();
            timer = 0;
        }
        else {
            Bukkit.getScheduler().cancelTask(eventRunTaskID);
            Bukkit.getScheduler().cancelTask(timerTaskID);
            timer = 0;
            remainingTime = 0;
        }
        works = false;
    }

    public UUID getPlayerUniqueId() {
        return plUUID;
    }

    public boolean eventIsRunning() {
        return zeroIncomeEvent.isRunning();
    }
}
