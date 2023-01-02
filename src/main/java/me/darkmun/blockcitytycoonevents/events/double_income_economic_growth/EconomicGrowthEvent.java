package me.darkmun.blockcitytycoonevents.events.double_income_economic_growth;

import me.darkmun.blockcitytycoonevents.events.EndTimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.IncomeEvent;

import java.util.UUID;

public class EconomicGrowthEvent implements IncomeEvent, EndTimeBasedEvent {
    private final UUID plUUID;
    private final String plName;
    private boolean running = false;

    public EconomicGrowthEvent(UUID plUUID, String plName) {
        this.plUUID = plUUID;
        this.plName = plName;
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
        return "economic-growth-event";
    }
}
