package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.Config;
import me.darkmun.blockcitytycoonevents.events.double_income_economic_growth.EconomicGrowthEvent;
import me.darkmun.blockcitytycoonevents.events.rain.PlaceOfRitualBlock;
import me.darkmun.blockcitytycoonevents.events.rain.RainEvent;
import me.darkmun.blockcitytycoonevents.events.rain.RainEventStopper;
import me.darkmun.blockcitytycoonevents.events.zero_income_night.NightEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEvent.BCTEconomyPlugin;
import static me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener.TICKS_PER_SECOND;
import static me.darkmun.blockcitytycoonevents.events.rain.RainEventStopper.BLOCKS_COORD;

public class BlockCityTycoonEventWorker {

    private boolean works = false;
    private boolean paused = false;
    //private UUID plUUID;
    private long timer = 0;
    private long remainingTimeToRun = 0;
    private long remainingTimeToEnd = 0;
    private long currentTimeToEnd = 0;
    private int eventRunTaskID;
    private int eventEndTaskID;
    private int timerTaskID;
    private BlockCityTycoonEvent BCTEvent;
    private Config config = BlockCityTycoonEvents.getPlayerEventsConfig();
    private BossBar bossBar;
    public BlockCityTycoonEventWorker(BlockCityTycoonEvent event) {
        BCTEvent = event;
        bossBar = Bukkit.getServer().createBossBar(EventMessages.getFormattedEventName(BCTEvent), BarColor.valueOf(EventMessages.getEventColor(BCTEvent)), BarStyle.SOLID);
        //для некоторых ивентов можно создать один бар на сервер
    }

    public void createEventWork() {  // избавиться от таймера
        long minSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-next-run.min");
        long maxSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-next-run.max");
        createEventWork(ThreadLocalRandom.current().nextLong(minSec * TICKS_PER_SECOND, maxSec * TICKS_PER_SECOND));
    }

    public void createEventWork(long remainingTime) {
        createEventWork(remainingTime, -1);
    }

    public void createEventWork(long remainingTimeRun, long remainingTimeEnd) {
        //Bukkit.getLogger().info("Remaining time: " + remainingTime);
        works = true;

        if (BCTEvent instanceof TimeBasedEvent) {

            remainingTimeToRun = remainingTimeRun;
            eventRunTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                if (remainingTimeEnd == -1) {
                    runTimeBasedEvent();
                } else {
                    runTimeBasedEvent(remainingTimeEnd);
                }
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
            if (pl != null) {
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

                        if (BCTEvent instanceof RainEvent) {
                            ((RainEvent) BCTEvent).startRain(pl);
                            for (PlaceOfRitualBlock place : RainEventStopper.getPlacesOfRitualBlocks(pl)) {
                                Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                                    if (place.isPlaced()) {
                                        Bukkit.getLogger().info("Continue: placing");
                                        place.setPlacing(true);
                                        Player player = Bukkit.getPlayer(pl.getUniqueId());
                                        if (player != null) {
                                            player.sendBlockChange(new Location(pl.getWorld(), place.getX(), place.getY(), place.getZ()), Material.AIR, (byte) 0);
                                        }
                                    }
                                    //place.setPlaced(false);
                                }, 20);
                            }
                        }

                        if (BCTEvent instanceof EndTimeBasedEvent) {
                            eventEndTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                                stopEndTimeBasedEvent();
                            }, remainingTimeToEnd).getTaskId();
                            timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> {
                                timer++;
                                bossBar.setProgress(((float) currentTimeToEnd)/((float) remainingTimeToEnd));
                                currentTimeToEnd--;
                            }, 0, 1).getTaskId();
                        }

                        bossBar.addPlayer(pl);
                    }
                }
                paused = false;
            }
        }
        config.saveConfig();
    }

    public void stopEventWork() {
        stopEventWork(false);
    }

    public void stopEventWork(boolean eventIsRunning) {
        Bukkit.getLogger().info("Remaining time to run (stop): " + remainingTimeToRun);
        Bukkit.getLogger().info("Remaining time to end (stop): " + remainingTimeToEnd);
        if (!works) {
            Bukkit.getLogger().info("Ивент не может быть остановлен, т.к. он не собирается запускаться.");
        }
        else {
            if (BCTEvent instanceof TimeBasedEvent) {
                if (BCTEvent.isRunning()) {
                    BCTEvent.stop();

                    Player pl = Bukkit.getPlayer(getPlayerUUID());
                    if (pl != null) {
                        bossBar.removePlayer(pl);
                    }
                    if (BCTEvent instanceof EndTimeBasedEvent) {
                        config.getConfig().set(getPlayerUUID() + "." + BCTEvent.getName() + ".remaining-time-to-end", remainingTimeToEnd - timer);
                    }

                    config.getConfig().set(getPlayerUUID() + "." + BCTEvent.getName() + ".running", eventIsRunning);
                }
                else {
                    Bukkit.getScheduler().cancelTask(eventRunTaskID);
                    Bukkit.getScheduler().cancelTask(timerTaskID);
                    config.getConfig().set(getPlayerUUID() + "." + BCTEvent.getName() + ".remaining-time-to-run", remainingTimeToRun - timer);
                    remainingTimeToRun = 0;
                    remainingTimeToEnd = 0;
                }
                timer = 0;
            }
            works = false;
        }
        config.saveConfig();
    }

    private void runTimeBasedEvent() {
        if (BCTEvent instanceof EndTimeBasedEvent) {
            long minSecToEnd = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-end.min");
            long maxSecToEnd = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTEvent.getName() + ".time-to-end.max");
            runTimeBasedEvent(ThreadLocalRandom.current().nextLong(minSecToEnd * TICKS_PER_SECOND, maxSecToEnd * TICKS_PER_SECOND));
        } else {
            runTimeBasedEvent(-1);
        }


    }

    private void runTimeBasedEvent(long remainingTime) {
        Bukkit.getScheduler().cancelTask(timerTaskID);
        timer = 0;
        BCTEvent.run();

        Player pl = Bukkit.getPlayer(getPlayerUUID());
        Bukkit.getLogger().info(BCTEvent.getName());
        if (pl != null) {
            EventMessages.sendTitle(pl, BCTEvent);
            bossBar.addPlayer(pl);
        }

        if (BCTEvent instanceof EndTimeBasedEvent) {
            remainingTimeToEnd = remainingTime;
            currentTimeToEnd = remainingTimeToEnd;
            eventEndTaskID = Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                stopEndTimeBasedEvent();
            }, remainingTimeToEnd).getTaskId();
            timerTaskID = Bukkit.getScheduler().runTaskTimer(BlockCityTycoonEvents.getPlugin(), () -> {
                timer++;
                bossBar.setProgress(((float) currentTimeToEnd)/((float) remainingTimeToEnd));
                currentTimeToEnd--;
            }, 0, 1).getTaskId();


        }


        if (BCTEvent instanceof RainEvent) {
            if (pl != null && pl.getInventory().first(Material.YELLOW_GLAZED_TERRACOTTA) == -1) {
                ItemStack item = new ItemStack(Material.YELLOW_GLAZED_TERRACOTTA, BLOCKS_COORD.size()); //можно оптимизировать на хотя бы милисекунду мейби)
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Кусочек солнца");
                meta.setLore(null);
                item.setItemMeta(meta);
                pl.getInventory().addItem(item);
            }

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

    public boolean isPaused() {
        return paused;
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
