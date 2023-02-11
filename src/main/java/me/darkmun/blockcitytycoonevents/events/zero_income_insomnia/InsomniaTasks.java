package me.darkmun.blockcitytycoonevents.events.zero_income_insomnia;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.EventMessages;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener.TICKS_PER_SECOND;

public class InsomniaTasks {
    private int runTaskId;
    private int stopTaskId;
    private boolean running = false;
    private boolean stopping = false;
    private final InsomniaEvent event;
    private final BossBar bossBar;

    public InsomniaTasks(InsomniaEvent event) {
        //this.plUUID = event.getPlayerUUID();
        this.event = event;
        bossBar = Bukkit.createBossBar(EventMessages.getFormattedEventName(event), BarColor.valueOf(EventMessages.getEventColor(event)), BarStyle.SOLID);
    }

    public void runRunningTask() {
        running = true;
        runTaskId = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
            event.run();
            Player pl = Bukkit.getPlayer(getPlayerUUID());
            if (pl != null) {
                EventMessages.sendTitle(pl, event);
                bossBar.addPlayer(pl);
            }

            BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().set(getPlayerUUID().toString() + ".insomnia-event.running", true);
            running = false;
            BlockCityTycoonEvents.getPlayerEventsConfig().saveConfig();
        }, BlockCityTycoonEvents.getPlugin().getConfig().getLong("insomnia-event.time-to-run") * TICKS_PER_SECOND).getTaskId();
    }

    public void runStoppingTask() {
        stopping = true;
        stopTaskId = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
            event.stop();

            Player pl = Bukkit.getPlayer(getPlayerUUID());
            if (pl != null) {
                bossBar.removePlayer(pl);
            }

            BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().set(getPlayerUUID().toString() + ".insomnia-event.running", false);
            BlockCityTycoonEvents.getPlayerEventsConfig().saveConfig();
            stopping = false;
        }, BlockCityTycoonEvents.getPlugin().getConfig().getLong("insomnia-event.time-to-stop") * TICKS_PER_SECOND).getTaskId();
    }

    public void stopStoppingTask() {
        Bukkit.getScheduler().cancelTask(stopTaskId);
        stopping = false;
    }

    public void stopRunningTask() {
        Bukkit.getScheduler().cancelTask(runTaskId);
        running = false;
    }

    public void pauseTasks() {
        if (running) {
            Bukkit.getScheduler().cancelTask(runTaskId);
        }
        else if (stopping) {
            Bukkit.getScheduler().cancelTask(stopTaskId);
        }
    }

    public void continueTasks() {
        if (running) {
            runRunningTask();
        }
        else if (stopping) {
            runStoppingTask();
        }
        if (event.isRunning()) {
            Player pl = Bukkit.getPlayer(getPlayerUUID());
            if (pl != null) {
                bossBar.addPlayer(pl);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isStopping() {
        return stopping;
    }

    /*public void setRunTaskId(int runTaskId) {
        this.runTaskId = runTaskId;
    }

    public int getRunTaskId() {
        return runTaskId;
    }

    public int getStopTaskId() {
        return stopTaskId;
    }*/

    public InsomniaEvent getEvent() {
        return event;
    }

    public UUID getPlayerUUID() {
        return event.getPlayerUUID();
    }
}
