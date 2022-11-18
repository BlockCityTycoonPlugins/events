package me.darkmun.blockcitytycoonevents.events.double_income_economic_growth;

import me.darkmun.blockcitytycoonevents.events.EndTimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.TimeBasedEvent;
import me.darkmun.blockcitytycoonevents.events.IncomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EconomicGrowthEvent implements IncomeEvent, EndTimeBasedEvent {
    private UUID plUUID;
    private double currentIncome;
    private boolean running = false;

    public EconomicGrowthEvent(UUID plUUID) {
        this.plUUID = plUUID;
        currentIncome = BCTEconomyPlugin.getConfig().getDouble("DataBaseIncome." + Bukkit.getPlayer(plUUID).getName() + ".total-income");
    }
    @Override
    public void run() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);

        running = true;
        currentIncome = BCTEconomyPlugin.getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income");
        BCTEconomyPlugin.getConfig().set("DataBaseIncome." + pl.getName() + ".total-income", currentIncome * 2);
        BCTEconomyPlugin.saveConfig();
    }

    @Override
    public void stop() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);

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
