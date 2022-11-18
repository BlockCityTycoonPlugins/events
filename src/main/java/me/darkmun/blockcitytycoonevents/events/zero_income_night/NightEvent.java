package me.darkmun.blockcitytycoonevents.events.zero_income_night;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.TimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.IncomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NightEvent implements IncomeEvent, TimeBasedEvent {
    private UUID plUUID;
    private double currentIncome;
    private boolean running = false;
    public NightEvent(UUID plUUID) {
        this.plUUID = plUUID;
        currentIncome = BCTEconomyPlugin.getConfig().getDouble("DataBaseIncome." + Bukkit.getPlayer(plUUID).getName() + ".total-income");
    }

    @Override
    public void run() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);

        BlockCityTycoonEvents.setTimeToPlayer(NIGHT_TIME, pl);
        running = true;
        currentIncome = BCTEconomyPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
        BCTEconomyPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", 0.0);
        BCTEconomyPlugin.saveConfig();
    }

    @Override
    public void stop() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);
        BlockCityTycoonEvents.setTimeToPlayer(DAY_TIME, pl/*Bukkit.getServer().getPlayer(pl.getUniqueId())*/);
        running = false;
        BCTEconomyPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", currentIncome);
        BCTEconomyPlugin.saveConfig();
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
