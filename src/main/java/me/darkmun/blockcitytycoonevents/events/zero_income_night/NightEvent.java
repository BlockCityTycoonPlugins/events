package me.darkmun.blockcitytycoonevents.events.zero_income_night;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.TimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.IncomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NightEvent implements IncomeEvent, TimeBasedEvent {
    private UUID plUUID;
    private String plName;
    private double currentIncome;
    private boolean running = false;
    public NightEvent(UUID plUUID, String plName) {
        this.plUUID = plUUID;
        this.plName = plName;
    }

    @Override
    public void run() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);

        BlockCityTycoonEvents.setTimeToPlayer(NIGHT_TIME, pl);
        running = true;
    }

    @Override
    public void stop() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);
        BlockCityTycoonEvents.setTimeToPlayer(DAY_TIME, pl);
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
        return "night-event";
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
