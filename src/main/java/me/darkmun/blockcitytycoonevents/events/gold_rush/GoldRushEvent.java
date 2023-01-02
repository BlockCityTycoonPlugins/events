package me.darkmun.blockcitytycoonevents.events.gold_rush;

import me.darkmun.blockcitytycoonevents.events.EndTimeBasedEvent;

import java.util.UUID;

public class GoldRushEvent implements EndTimeBasedEvent {
    private final UUID plUUID;
    private boolean running = false;

    public GoldRushEvent(UUID plUUID) {
        this.plUUID = plUUID;
    }
    @Override
    public void run() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
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
