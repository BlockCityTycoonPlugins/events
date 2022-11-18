package me.darkmun.blockcitytycoonevents.events.zero_income_night;

import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventWorker;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.Objects;

public class NightEventStopper implements Listener {


    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        e.setCancelled(true);

        Player pl = e.getPlayer();
        //BlockCityTycoonEventsWorker ZIEWorker = null;
        BlockCityTycoonEventWorker[] BCTEWorkers = BlockCityTycoonEventsListener.getBCTEventsWorker().stream().filter(worker ->
                worker[0].getPlayerUUID().equals(pl.getUniqueId())).findAny().orElse(null);

        for (BlockCityTycoonEventWorker worker : BCTEWorkers) {
            if (worker != null) {
                if (worker.getBCTEvent() instanceof NightEvent) {
                    if (worker.eventIsRunning()) {
                        worker.stopEventWork();
                        worker.createEventWork();
                    }
                }
            }
        }


    }
}
