package me.darkmun.blockcitytycoonevents.events.zero_income_insomnia;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class InsomniaEventWorker implements Listener {

    private Set<InsomniaTasks> tasks = new HashSet<>();
    //private Map<UUID, InsomniaTasks> tasks = new HashMap<>();
    private final int range = BlockCityTycoonEvents.getPlugin().getConfig().getInt("insomnia-event.range");
    private final int x = BlockCityTycoonEvents.getPlugin().getConfig().getInt("insomnia-event.bed-coord.x");
    private final int z = BlockCityTycoonEvents.getPlugin().getConfig().getInt("insomnia-event.bed-coord.z");

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player pl = e.getPlayer();
        Location newLocation = e.getTo();

        InsomniaTasks insTasks = tasks.stream().filter(task -> task.getPlayerUUID().equals(pl.getUniqueId())).findAny().orElse(null);

        if ((newLocation.getX() <= x + range)
                && (newLocation.getX() >= x - range)
                && (newLocation.getZ() <= z + range)
                && (newLocation.getZ() >= z - range)) {

            if (!insTasks.getEvent().isRunning() && !insTasks.isRunning()) {
                insTasks.runRunningTask();
            }
            else if (insTasks.isStopping()) {
                insTasks.stopStoppingTask();
            }
        }
        else {
            if (insTasks.getEvent().isRunning() && !insTasks.isStopping()) {
                insTasks.runStoppingTask();
            }
            else if (insTasks.isRunning()) {
                insTasks.stopRunningTask();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        InsomniaTasks insTasks;
        if (tasks.stream().noneMatch(task -> task.getPlayerUUID().equals(pl.getUniqueId()))) {
            insTasks = new InsomniaTasks(new InsomniaEvent(pl.getUniqueId()));
            tasks.add(insTasks);
        }
        else {
            insTasks = tasks.stream().filter(task -> task.getPlayerUUID().equals(pl.getUniqueId())).findAny().orElse(null);
            if (insTasks != null) {
                insTasks.continueTasks();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player pl = e.getPlayer();

        InsomniaTasks insTasks = tasks.stream().filter(task -> task.getPlayerUUID().equals(pl.getUniqueId())).findAny().orElse(null);
        //if (insTasks != null) {
            insTasks.pauseTasks();
        //}
    }

}
