package me.darkmun.blockcitytycoonevents.events.rain;

import com.comphenix.packetwrapper.WrapperPlayServerGameStateChange;
import me.darkmun.blockcitytycoonevents.events.IncomeEvent;
import me.darkmun.blockcitytycoonevents.events.TimeBasedEvent;
import net.minecraft.server.v1_12_R1.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RainEvent implements TimeBasedEvent, IncomeEvent {
    private UUID plUUID;
    private String plName;
    private double currentIncome;
    private boolean running = false;

    public RainEvent(UUID plUUID, String plName) {
        this.plUUID = plUUID;
        this.plName = plName;
    }

    @Override
    public void run() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);
        startRain(pl);
        running = true;
    }

    @Override
    public void stop() {
        Player pl = Bukkit.getServer().getPlayer(plUUID);
        stopRain(pl);
        running = false;
    }

    public void startRain(Player pl) {
        if (pl != null) {
            WrapperPlayServerGameStateChange wrapper = new WrapperPlayServerGameStateChange();
            wrapper.setReason(2);
            wrapper.setValue(0);
            wrapper.sendPacket(pl);
        }
    }

    public void stopRain(Player pl) {
        if (pl != null) {
            WrapperPlayServerGameStateChange wrapper = new WrapperPlayServerGameStateChange();
            wrapper.setReason(1);
            wrapper.setValue(0);
            wrapper.sendPacket(pl);
        }
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
        return "rain-event";
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
