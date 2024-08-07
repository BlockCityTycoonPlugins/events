package me.darkmun.blockcitytycoonevents.events.donate;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventWorker;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ThreadLocalRandom;

import static me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener.TICKS_PER_SECOND;

public class EventsWorkCommand implements CommandExecutor {
    @Override @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("bct.donate.manage")) {
            sender.sendMessage(ChatColor.RED + "У вас нет права на использование этой команды");
        } else if (args.length != 3 && args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Некорректное количество аргументов");
            sendUsage(sender);
        } else if (args.length == 3) {
            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[2]);

            if (!(offPlayer.hasPlayedBefore() || offPlayer.isOnline())) {
                sender.sendMessage(ChatColor.RED + "Игрок с ником " + args[2] + " никогда не заходил на сервер");
                sendUsage(sender);
            } else {
                BlockCityTycoonEventWorker[] BCTEWorkers = BlockCityTycoonEventsListener.getBCTEventsWorker().stream().filter(worker ->
                        worker[0].getPlayerUUID().equals(offPlayer.getUniqueId())).findAny().orElse(null);

                BlockCityTycoonEventWorker BCTworker = null;
                assert BCTEWorkers != null;
                for (BlockCityTycoonEventWorker worker : BCTEWorkers) {
                    if (worker != null) {
                        if (worker.getBCTEvent().getName().equals(args[0])) {
                            BCTworker = worker;
                            break;
                        }
                    }
                }

                if (BCTworker == null) {
                    sender.sendMessage(ChatColor.RED + "Некорректно введен первый аргумент");
                    sendUsage(sender);
                } else if (!BCTworker.getBCTEvent().getName().equals("rain-event")) {
                    sender.sendMessage(ChatColor.RED + "Без аргумента времени, вы можете взаимодействовать только с rain-event");
                    sendUsage(sender);
                } else if (!args[1].equals("start") && !args[1].equals("pause")) {
                    sender.sendMessage(ChatColor.RED + "Некорректно введен второй аргумент");
                    sendUsage(sender);
                } else if (args[1].equals("start")) {
                    if (!offPlayer.isOnline())
                        BCTworker.setOfflineWork(true);
                    BCTworker.stopEventWork();
                    BCTworker.createEventWork(1);
                } else if (args[1].equals("pause")) {
                    if (!offPlayer.isOnline())
                        BCTworker.setOfflineWork(true);
                    BCTworker.stopEventWork(); //TODO: Солнечный регион выдается до следующей перезагрузки сервера, а не навсегда
                    BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().set(offPlayer.getUniqueId() + ".rain-event.disable", true);
                    BlockCityTycoonEvents.getPlayerEventsConfig().saveConfig();
                    BlockCityTycoonEvents.getPlugin().getDonateLogger().info("Донат \"Солнечный регион\" выдан игроку " + offPlayer.getName() + " навсегда");
                }
            }
        } else if (args.length == 4) {
            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[3]);

            if (!(offPlayer.hasPlayedBefore() || offPlayer.isOnline())) {
                sender.sendMessage(ChatColor.RED + "Игрок с ником " + args[3] + " никогда не заходил на сервер");
                sendUsage(sender);
            } else {
                BlockCityTycoonEventWorker[] BCTEWorkers = BlockCityTycoonEventsListener.getBCTEventsWorker().stream().filter(worker ->
                        worker[0].getPlayerUUID().equals(offPlayer.getUniqueId())).findAny().orElse(null);

                BlockCityTycoonEventWorker BCTworker = null;
                assert BCTEWorkers != null;
                for (BlockCityTycoonEventWorker worker : BCTEWorkers) {
                    if (worker != null) {
                        if (worker.getBCTEvent().getName().equals(args[0])) {
                            BCTworker = worker;
                            break;
                        }
                    }
                }

                if (BCTworker == null
                        || (!BCTworker.getBCTEvent().getName().equals("economic-growth-event")
                            && !BCTworker.getBCTEvent().getName().equals("gold-rush-event")
                            && !BCTworker.getBCTEvent().getName().equals("rain-event"))) {
                    sender.sendMessage(ChatColor.RED + "Некорректно введен первый аргумент");
                    sendUsage(sender);
                } else {
                    try {
                        long time = Long.parseLong(args[2]);
                        String eventName = BCTworker.getBCTEvent().getName();
                        if (eventName.equals("economic-growth-event")
                                || eventName.equals("gold-rush-event")) {
                            if (!args[1].equals("start") && !args[1].equals("pause")) {
                                sender.sendMessage(ChatColor.RED + "Некорректно введен второй аргумент");
                                sendUsage(sender);
                            } else if (args[1].equals("start")) {
                                if (!offPlayer.isOnline())
                                    BCTworker.setOfflineWork(true);
                                if (BCTworker.eventIsRunning()) {
                                    BCTworker.stopEndTimeBasedEvent();
                                } else {
                                    BCTworker.stopEventWork();
                                }
                                BCTworker.createEventWork(1, time * TICKS_PER_SECOND);
                                if (eventName.equals("economic-growth-event")) {
                                    BlockCityTycoonEvents.getPlugin().getDonateLogger().info("Донат \"Экономический рост\" выдан игроку " + offPlayer.getName() + " на " + time/60d + " минут");
                                } else {
                                    BlockCityTycoonEvents.getPlugin().getDonateLogger().info("Донат \"Золотая лихорадка\" выдан игроку " + offPlayer.getName() + " на " + time/60d + " минут");
                                }
                            } else if (!BCTworker.eventIsRunning()) {
                                sender.sendMessage(ChatColor.RED + "Вы не можете поставить на паузу ивент, так как он не запущен");
                                sendUsage(sender);
                            } else {
                                sender.sendMessage(ChatColor.RED + "С аргументом времени, вы можете останавливать только rain-event");
                                sendUsage(sender);
                            }
                        } else {
                            if (!args[1].equals("start") && !args[1].equals("pause")) {
                                sender.sendMessage(ChatColor.RED + "Некорректно введен второй аргумент");
                                sendUsage(sender);
                            } else if (args[1].equals("start")) {
                                if (!offPlayer.isOnline())
                                    BCTworker.setOfflineWork(true);
                                BCTworker.stopEventWork();
                                BCTworker.createEventWork(time * TICKS_PER_SECOND);
                            } else {
                                if (!offPlayer.isOnline())
                                    BCTworker.setOfflineWork(true);
                                BCTworker.stopEventWork();
                                long minSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTworker.getBCTEvent().getName() + ".time-to-next-run.min");
                                long maxSec = BlockCityTycoonEvents.getPlugin().getConfig().getLong(BCTworker.getBCTEvent().getName() + ".time-to-next-run.max");
                                BCTworker.createEventWork(time * TICKS_PER_SECOND + ThreadLocalRandom.current().nextLong(minSec * TICKS_PER_SECOND, maxSec * TICKS_PER_SECOND));
                                BlockCityTycoonEvents.getPlugin().getDonateLogger().info("Донат \"Солнечный регион\" выдан игроку " + offPlayer.getName() + " на " + time/60d + " минут");
                            }
                        }
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChatColor.RED + "Некорректно введен третий аргумент");
                        sendUsage(sender);
                    }
                }
            }
        }

        return true;
    }

    private static void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Применение: /event [economic-growth-event | gold-rush-event | rain-event] [start | pause] <время в секундах> [игрок]"); ///event [economical-growth | gold-rush | rain-event] [start | pause] [время в секундах] (для rain-event можно не указывать время) [игрок]
    }
}
