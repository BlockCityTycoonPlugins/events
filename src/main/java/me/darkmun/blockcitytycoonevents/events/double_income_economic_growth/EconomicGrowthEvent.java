package me.darkmun.blockcitytycoonevents.events.double_income_economic_growth;

import me.darkmun.blockcitytycoonevents.events.EndTimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.TimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.IncomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EconomicGrowthEvent implements IncomeEvent, EndTimeBasedEvent {
    private UUID plUUID;
    private String plName;
    private double currentIncome;
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

    @Override
    public double getRealIncome() {
        return currentIncome;
    }

    @Override
    public void setRealIncome(double income) {
        currentIncome = income;
    }
}
