package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.Config;
import me.darkmun.blockcitytycoonevents.events.double_income_economic_growth.EconomicGrowthEvent;
import me.darkmun.blockcitytycoonevents.events.gold_rush.GoldRushEvent;
import me.darkmun.blockcitytycoonevents.events.zero_income_night.NightEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.beans.EventHandler;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEvent.BCTEconomyPlugin;
import static me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener.TICKS_PER_SECOND;

public class BlockCityTycoonEventWorker {

    private boolean works = false;
    private boolean paused = false;
    //private UUID plUUID;
    private long timer = 0;
    private long remainingTimeToRun = 0;
    private long remainingTimeToEnd = 0;
    private int eventRunTaskID;
    private int eventEndTaskID;
    private int timerTaskID;
    private BlockCityTycoonEvent BCTEvent;
    Config config = BlockCityTycoonEvents.getPlayerEventsConfig();

    public BlockCityTycoonEventWorker(BlockCityTycoonEvent event) {
        //plUUID = event.getPlayerUUID();
        BCTEvent = event;
    }

    public void createEventWork() {
        //Bukkit.getLogger().info("Remaining time: " + remainingTime);
        works = true;
        if (BCTEvent instanceof TimeBasedEvent) {
            long minSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-next-run.min");
            long maxSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-next-run.max");

            remainingTimeToRun = ThreadLocalRandom.current().nextLong(minSec * TICKS_PER_SECOND, maxSec * TICKS_PER_SECOND);
            eventRunTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                runTimeBasedEvent();
            }, remainingTimeToRun).getTaskId();
            timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> timer++, 0, 1).getTaskId(); // добавлять в конфиг таймер
        }
        Bukkit.getLogger().info("Remaining time to run (create): " + remainingTimeToRun);
        Bukkit.getLogger().info("Remaining time to end (create): " + remainingTimeToEnd);
        config.saveConfig();
    }

    public void pauseEventWork() {
        Bukkit.getLogger().info("Remaining time to run (pause): " + remainingTimeToRun);
        Bukkit.getLogger().info("Remaining time to end (pause): " + remainingTimeToEnd);

        if (!works) {
            Bukkit.getLogger().info("Ивент не может встать на паузу, т.к. он не собирается запускаться.");
        }
        else {
            if (BCTEvent instanceof TimeBasedEvent) {
                Bukkit.getScheduler().cancelTask(timerTaskID);
                if (((remainingTimeToRun - timer) > 0) && (!BCTEvent.isRunning())) {
                    Bukkit.getScheduler().cancelTask(eventRunTaskID);
                    remainingTimeToRun -= timer;
                    timer = 0;
                }
                else if (((remainingTimeToEnd - timer) > 0) && BCTEvent.isRunning() && (BCTEvent instanceof EndTimeBasedEvent)) {
                    Bukkit.getScheduler().cancelTask(eventEndTaskID);
                    remainingTimeToEnd -= timer;
                    timer = 0;
                }
            }
            paused = true;
        }
    }

    public void continueEventWork() {
        Bukkit.getLogger().info("Remaining time to run (continue): " + remainingTimeToRun);
        Bukkit.getLogger().info("Remaining time to end (continue): " + remainingTimeToEnd);
        Player pl = Bukkit.getServer().getPlayer(getPlayerUUID());

        if (!paused) {
            Bukkit.getLogger().info("Ивент не может продолжиться, т.к. он не стоит на паузе.");
        }
        else {
            if (BCTEvent instanceof TimeBasedEvent) {
                if(!BCTEvent.isRunning()) {
                    eventRunTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                        runTimeBasedEvent();
                    }, remainingTimeToRun).getTaskId(); //1000 - timer
                    timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> timer++, 0, 1).getTaskId();

                    if (BCTEvent instanceof NightEvent) {
                        BlockCityTycoonEvents.setTimeToPlayer(NightEvent.DAY_TIME, pl);
                    }

                }
                else {
                    if (BCTEvent instanceof NightEvent) {
                        BlockCityTycoonEvents.setTimeToPlayer(NightEvent.NIGHT_TIME, pl);
                    }
                    if (BCTEvent instanceof EndTimeBasedEvent) {
                        eventEndTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                            stopEndTimeBasedEvent();
                        }, remainingTimeToEnd).getTaskId();
                        timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> timer++, 0, 1).getTaskId();
                    }
                }
            }
            paused = false;
        }
        config.saveConfig();
    }

    public void stopEventWork() {
        Bukkit.getLogger().info("Remaining time to run (stop): " + remainingTimeToRun);
        Bukkit.getLogger().info("Remaining time to end (stop): " + remainingTimeToEnd);
        if (!works) {
            Bukkit.getLogger().info("Ивент не может быть остановлен, т.к. он не собирается запускаться.");
        }
        else {
            if (BCTEvent instanceof TimeBasedEvent) {
                if (BCTEvent.isRunning()) {
                    BCTEvent.stop();
                    timer = 0;

                    if (BCTEvent instanceof NightEvent) {
                        if (config.getConfig().getBoolean(getPlayerUUID() + ".economic-growth-event.running")) {
                            setIncome(((IncomeEvent) BCTEvent).getRealIncome() * 2d);
                        }
                    }

                    if (BCTEvent instanceof EconomicGrowthEvent) {
                        if (config.getConfig().getBoolean(getPlayerUUID() + ".night-event.running")) {
                            setIncome(0);
                        }
                    }

                    config.getConfig().set(getPlayerUUID() + "." + BCTEvent.getName() + ".running", false);
                }
                else {
                    Bukkit.getScheduler().cancelTask(eventRunTaskID);
                    Bukkit.getScheduler().cancelTask(timerTaskID);
                    timer = 0;
                    remainingTimeToRun = 0;
                    remainingTimeToEnd = 0;
                }
            }
            works = false;
        }
        config.saveConfig();
    }

    private void runTimeBasedEvent() {
        Bukkit.getScheduler().cancelTask(timerTaskID);
        timer = 0;
        BCTEvent.run();
        Player pl = Bukkit.getPlayer(getPlayerUUID());

        if (pl != null) {
            EventMessages.sendTitle(pl, BCTEvent);
        }

        if (BCTEvent instanceof NightEvent) {
            if (!config.getConfig().getBoolean(getPlayerUUID() + ".economic-growth-event.running")) {
                config.getConfig().set(getPlayerUUID() + ".income", ((IncomeEvent) BCTEvent).getRealIncome());
            }
            else {
                ((IncomeEvent) BCTEvent).setRealIncome(config.getConfig().getDouble(getPlayerUUID() + ".income"));
            }
        }

        if (BCTEvent instanceof EconomicGrowthEvent) {
            if (!config.getConfig().getBoolean(getPlayerUUID() + ".night-event.running")) {
                config.getConfig().set(getPlayerUUID() + ".income", ((IncomeEvent) BCTEvent).getRealIncome());
            }
            else {
                ((IncomeEvent) BCTEvent).setRealIncome(config.getConfig().getDouble(getPlayerUUID() + ".income"));
            }

        }

        if (BCTEvent instanceof EndTimeBasedEvent) {
            long minSecToEnd = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-end.min");
            long maxSecToEnd = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-end.max");

            remainingTimeToEnd = ThreadLocalRandom.current().nextLong(minSecToEnd * TICKS_PER_SECOND, maxSecToEnd * TICKS_PER_SECOND);
            eventEndTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                stopEndTimeBasedEvent();
            }, remainingTimeToEnd).getTaskId();
            timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> timer++, 0, 1).getTaskId();
        }

        config.getConfig().set(getPlayerUUID() + "." + BCTEvent.getName() + ".running", true);
        config.saveConfig();

        Bukkit.getLogger().info("Remaining time to run (run): " + remainingTimeToRun);
        Bukkit.getLogger().info("Remaining time to end (run): " + remainingTimeToEnd);
    }

    private void stopEndTimeBasedEvent() {
        Bukkit.getScheduler().cancelTask(timerTaskID);
        timer = 0;
        stopEventWork();
        createEventWork();
        Bukkit.getLogger().info("Remaining time to run (stop double event): " + remainingTimeToRun);
        Bukkit.getLogger().info("Remaining time to end (stop double event): " + remainingTimeToEnd);
    }

    private void setIncome(double income) {
        Player pl = Bukkit.getPlayer(getPlayerUUID());
        BCTEconomyPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", income);
    }

    public UUID getPlayerUUID() {
        return BCTEvent.getPlayerUUID();
    }

    public BlockCityTycoonEvent getBCTEvent() {
        return BCTEvent;
    }

    public boolean eventIsRunning() {
        return BCTEvent.isRunning();
    }
}
