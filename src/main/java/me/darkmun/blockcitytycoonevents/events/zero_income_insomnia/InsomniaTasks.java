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
    //private UUID plUUID;
    private int runTaskId;
    private int stopTaskId;
    private boolean running = false;
    private boolean stopping = false;
    private InsomniaEvent event;
    private BossBar bossBar;

    public InsomniaTasks(InsomniaEvent event) {
        //this.plUUID = event.getPlayerUUID();
        this.event = event;
        bossBar = Bukkit.createBossBar(EventMessages.getFormattedEventName(event), BarColor.valueOf(EventMessages.getEventColor(event)), BarStyle.SOLID);
    }

    public void runRunningTask() {
        Bukkit.getLogger().info("Running Task");
        running = true;
        Bukkit.getLogger().info("runTask: " + running);
        runTaskId = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
            event.run();
            Bukkit.getLogger().info("runEvent: " + running);
            Bukkit.getLogger().info("Running Event");

            Player pl = Bukkit.getPlayer(getPlayerUUID());
            if (pl != null) {
                EventMessages.sendTitle(pl, event);
                bossBar.addPlayer(pl);
            }

            BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().set(getPlayerUUID().toString() + ".insomnia-event.running", true);
            running = false;
            BlockCityTycoonEvents.getPlayerEventsConfig().saveConfig();
        }, BlockCityTycoonEvents.getPlugin().getConfig().getLong("insomnia-event.time-to-run") * TICKS_PER_SECOND).getTaskId();
        Bukkit.getLogger().info("runTask: " + running);
    }

    public void runStoppingTask() {
        Bukkit.getLogger().info("Stopping Task");
        stopping = true;
        stopTaskId = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
            event.stop();
            Bukkit.getLogger().info("Stopping Event");

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
        Bukkit.getLogger().info("Stop Stopping Task");
        Bukkit.getScheduler().cancelTask(stopTaskId);
        stopping = false;
    }

    public void stopRunningTask() {
        Bukkit.getLogger().info("Stop Running Task");
        Bukkit.getScheduler().cancelTask(runTaskId);
        running = false;
    }

    public void pauseTasks() {
        Bukkit.getLogger().info("pause: " + running);
        if (running) {
            Bukkit.getLogger().info("Pausing with running");
            Bukkit.getScheduler().cancelTask(runTaskId);
        }
        else if (stopping) {
            Bukkit.getLogger().info("Pausing with stopping");
            Bukkit.getScheduler().cancelTask(stopTaskId);
        }
    }

    public void continueTasks() {
        if (running) {
            Bukkit.getLogger().info("Continuing with running");
            runRunningTask();
        }
        else if (stopping) {
            Bukkit.getLogger().info("Continuing with stopping");
            runStoppingTask();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isStopping() {
        return stopping;
    }

    public void setRunTaskId(int runTaskId) {
        this.runTaskId = runTaskId;
    }

    public int getRunTaskId() {
        return runTaskId;
    }

    public int getStopTaskId() {
        return stopTaskId;
    }

    public InsomniaEvent getEvent() {
        return event;
    }

    public UUID getPlayerUUID() {
        return event.getPlayerUUID();
    }
}
