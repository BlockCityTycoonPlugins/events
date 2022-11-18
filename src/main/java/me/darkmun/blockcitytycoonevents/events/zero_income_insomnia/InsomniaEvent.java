package me.darkmun.blockcitytycoonevents.events.zero_income_insomnia;

import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InsomniaEvent implements BlockCityTycoonEvent {
    private UUID plUUID;
    private boolean running = false;
    public InsomniaEvent(UUID plUUID) {
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
        return "insomnia-event";
    }
}
