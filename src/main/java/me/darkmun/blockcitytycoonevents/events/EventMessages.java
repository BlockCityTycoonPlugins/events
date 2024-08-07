package me.darkmun.blockcitytycoonevents.events;

import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

import static me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener.TICKS_PER_SECOND;

public class EventMessages {
    private static final DecimalFormat df = new DecimalFormat("#.##");
    public static void sendTitle(Player pl, BlockCityTycoonEvent event) {
        String title;
        String subtitle;
        switch (event.getName()) {
            case "night-event":
                title = ChatColor.RED + "Ночь";
                subtitle = ChatColor.RED + "Ваш доход становится нулевым";
                break;
            case "economic-growth-event":
                title = ChatColor.GREEN + "Экономический рост";
                subtitle = ChatColor.GREEN + "Ваш доход увеличивается в два раза";
                break;
            case "gold-rush-event":
                title = ChatColor.GREEN + "Золотая лихорадка";
                subtitle = ChatColor.GREEN + "Ценность ломаемых блоков увеличивается в " + df.format(BlockCityTycoonEvents.getPlugin().getConfig().getDouble("gold-rush-event.multiplier")) + " раза";
                break;
            case "insomnia-event":
                title = ChatColor.RED + "Бессонница";
                subtitle = ChatColor.RED + "Вы не можете поспать ночью";
                break;
            case "rain-event":
                title = ChatColor.RED + "Дождь";
                subtitle = ChatColor.RED + "Ваш доход уменьшается в два раза";
                break;
            default:
                title = ChatColor.GOLD + "Таково ивента нет";
                subtitle = ChatColor.GOLD + "Ивент еще не добавлен в список тайтлов";
        }

        pl.sendTitle(title, subtitle, (int) (TICKS_PER_SECOND), (int) (4 * TICKS_PER_SECOND), (int) (TICKS_PER_SECOND));
    }

    public static String getFormattedEventName(BlockCityTycoonEvent event) {
        switch (event.getName()) {
            case "night-event":
                return "Ночь";
            case "economic-growth-event":
                return "Экономический рост";
            case "gold-rush-event":
                return "Золотая лихорадка";
            case "insomnia-event":
                return "Бессонница";
            case "rain-event":
                return "Дождь";
            default:
                return "Несуществующий ивент";
        }
    }

    public static String getEventColor(BlockCityTycoonEvent event) {
        switch (event.getName()) {
            case "night-event":
            case "insomnia-event":
            case "rain-event":
                return "RED";
            case "economic-growth-event":
            case "gold-rush-event":
                return "GREEN";
            default:
                return "WHITE";
        }
    }
}
