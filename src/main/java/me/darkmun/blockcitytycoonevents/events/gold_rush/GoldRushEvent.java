package me.darkmun.blockcitytycoonevents.events.gold_rush;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.EndTimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.TimeBasedEvent;

import java.util.Set;
import java.util.UUID;

public class GoldRushEvent implements EndTimeBasedEvent {
    private UUID plUUID;
    private Set<String> blocks = BCTMinePlugin.getConfig().getConfigurationSection("value-of-blocks").getKeys(false);
    private boolean running = false;
    private double[] blockValues = new double[blocks.size()];

    public GoldRushEvent(UUID plUUID) {
        this.plUUID = plUUID;
    }
    @Override
    public void run() {
        //int multiplier = BlockCityTycoonEvents.getPlugin().getConfig().getInt(getName() + ".multiplier");

        //int i = 0;
        //for (String block : blocks) {
        //    blockValues[i] = BCTMinePlugin.getConfig().getDouble("value-of-blocks." + block);
        //    BCTMinePlugin.getConfig().set("value-of-blocks." + block, blockValues[i] * multiplier);
        //    i++;
        //}
        running = true;
        //BCTMinePlugin.saveConfig();
    }

    @Override
    public void stop() {
        //int i = 0;
        //for (String block : blocks) {
        //    //blockValues[i] = BCTMinePlugin.getConfig().getDouble("value-of-blocks." + block);
        //    BCTMinePlugin.getConfig().set("value-of-blocks." + block, blockValues[i]);
        //    i++;
        //}
        running = false;
        //BCTMinePlugin.saveConfig();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public UUID getPlayerUUID() {
        return plUUID;
    }

    @Override
    public String getName() {
        return "gold-rush-event";
    }
}
