package me.darkmun.blockcitytycoonevents;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockCityTycoonEvents extends JavaPlugin implements CommandExecutor {
    boolean zeroIncome = false;
    int currentIncome;
    @Override
    public void onEnable() {


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player pl = (Player) sender;
            Config WFXBusinessConfig = new Config();
            WFXBusinessConfig.setup(Bukkit.getServer().getPluginManager().getPlugin("WFX-Business").getDataFolder(), "config");


            if (!zeroIncome) {
                currentIncome = WFXBusinessConfig.getConfig().getInt("DataBaseIncome.total-income." + pl.getName());
                WFXBusinessConfig.getConfig().set("DataBaseIncome.total-income." + pl.getName(), 0);
                zeroIncome = true;
            }
            else {
                WFXBusinessConfig.getConfig().set("DataBaseIncome.total-income." + pl.getName(), currentIncome);
                zeroIncome = false;
            }
            WFXBusinessConfig.saveConfig();



            return true;
        }
        return false;
    }

}
