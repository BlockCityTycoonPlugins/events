package me.darkmun.blockcitytycoonevents.commands;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("bctevents.reload")) {
            sender.sendMessage(ChatColor.RED + "У вас нет разрешения на использование этой команды");
        } else if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Команда предназначена для перезагрузки конфигов, поэтому может быть только один аргумент");
            sendUsage(sender);
        } else if (!args[0].equals("reload")) {
            sendUsage(sender);
        } else {
            Bukkit.getLogger().info("§eПерезагрузка основного конфига...");
            BlockCityTycoonEvents.getPlugin().reloadConfig();
            Bukkit.getLogger().info("§eПерезагрузка конфига с данными об ивентах игроков...");
            BlockCityTycoonEvents.getPlayerEventsConfig().reloadConfig();
            Bukkit.getLogger().info("§aПерезагрузка §6BCTEvents §aзавершена.");
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Использование: /bctevents reload");
    }
}
