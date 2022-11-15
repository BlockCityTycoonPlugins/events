package me.darkmun.blockcitytycoonevents.events.gold_rush;

import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEvent;

import java.util.UUID;

public class GoldRushEvent implements BlockCityTycoonEvent {
    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public UUID getPlayerUUID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
