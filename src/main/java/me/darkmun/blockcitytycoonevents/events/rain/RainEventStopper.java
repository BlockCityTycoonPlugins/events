package me.darkmun.blockcitytycoonevents.events.rain;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityWeather;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.darkmun.blockcitytycoonevents.BlockCityTycoonEvents;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventWorker;
import me.darkmun.blockcitytycoonevents.events.BlockCityTycoonEventsListener;
import me.darkmun.blockcitytycoonevents.events.zero_income_night.NightEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RainEventStopper implements Listener {

    public static final Set<String> BLOCKS_COORD = BlockCityTycoonEvents.getPlugin().getConfig().getConfigurationSection("rain-event.ritual-blocks-coord").getKeys(false);
    private static Map<UUID, List<PlaceOfRitualBlock>> playersRitualBlocks = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        if (playersRitualBlocks.get(pl.getUniqueId()) == null) {
            Bukkit.getLogger().info("Have not ritual blocks");
            List<PlaceOfRitualBlock> blocks = new ArrayList<>();
            for (String coord : BLOCKS_COORD) {
                int x = BlockCityTycoonEvents.getPlugin().getConfig().getInt("rain-event.ritual-blocks-coord." + coord + ".x");
                int y = BlockCityTycoonEvents.getPlugin().getConfig().getInt("rain-event.ritual-blocks-coord." + coord + ".y");
                int z = BlockCityTycoonEvents.getPlugin().getConfig().getInt("rain-event.ritual-blocks-coord." + coord + ".z");
                PlaceOfRitualBlock place = new PlaceOfRitualBlock(x, y, z, coord);
                place.setPlaced(BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().getBoolean(String.format("%s.rain-event.ritual-blocks.%s.placed", pl.getUniqueId().toString(), coord)));
                blocks.add(place);
            }
            playersRitualBlocks.put(pl.getUniqueId(), blocks);
        }
        else if (BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().getBoolean(pl.getUniqueId().toString() + "rain-event.running")) {
            Bukkit.getLogger().info("Sending ritual blocks on join");
            for (PlaceOfRitualBlock place : playersRitualBlocks.get(pl.getUniqueId())) {
                if (place.isPlaced()) {
                    Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                        pl.sendBlockChange(new Location(pl.getWorld(), place.getX(), place.getY(), place.getZ()), Material.YELLOW_GLAZED_TERRACOTTA, (byte) 0);
                    }, 10);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Bukkit.getLogger().info("inter");
        Player pl = e.getPlayer();
        BlockCityTycoonEvents.getPlayerEventsConfig().reloadConfig();
        Bukkit.getLogger().info(String.valueOf(BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().getBoolean(pl.getUniqueId().toString() + ".rain-event.running")));
        if (BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().getBoolean(pl.getUniqueId().toString() + ".rain-event.running")) {
            Bukkit.getLogger().info("Interact: running");
            ItemStack itemInMainHand = pl.getInventory().getItemInMainHand();
            if (itemInMainHand.getType().equals(Material.YELLOW_GLAZED_TERRACOTTA) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                Bukkit.getLogger().info("Interact: sun in hand and right clicked");
                List<Block> blocksInLineOfSight = pl.getLineOfSight(null, 5);
                PlaceOfRitualBlock ritPlace = null;
                for (org.bukkit.block.Block block : blocksInLineOfSight) {
                    boolean isRitualBlock = false;
                    int x = block.getX();
                    int y = block.getY();
                    int z = block.getZ();
                    for (PlaceOfRitualBlock place : playersRitualBlocks.get(pl.getUniqueId())) {
                        Bukkit.getLogger().info("ritual x: " + place.getX() + " ritual y: " + place.getY() + " ritual z: " + place.getZ());
                        Bukkit.getLogger().info("x: " + x + " y: " + y + " z: " + z);
                        if (x == place.getX()
                                && y == place.getY()
                                && z == place.getZ()) {
                            Bukkit.getLogger().info("Ritual y: " + place.getY() + " found y");
                            isRitualBlock = true;
                            ritPlace = place;
                            break;
                        }
                    }
                    if (isRitualBlock) {
                        Bukkit.getLogger().info("Interact: it's ritual block");
                        Bukkit.getLogger().info("x: " + x + " y: " + y + " z: " + z);
                        if (!ritPlace.isPlaced()) {
                            Bukkit.getLogger().info("Interact: ritual block not placed");

                            ritPlace.setPlacing(true);
                            pl.sendBlockChange(block.getLocation(), Material.YELLOW_GLAZED_TERRACOTTA, (byte) 0);
                            ritPlace.setPlaced(true);
                            BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().set(String.format("%s.rain-event.ritual-blocks.%s.placed", pl.getUniqueId().toString(), ritPlace.getName()), true);

                            itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);

                            boolean allPlaced = true;
                            for (PlaceOfRitualBlock place : playersRitualBlocks.get(pl.getUniqueId())) {
                                if (!place.isPlaced()) {
                                    allPlaced = false;
                                    break;
                                }
                            }
                            if (allPlaced) {

                                Bukkit.getLogger().info("Interact: all ritual blocks placed");
                                BlockCityTycoonEventWorker[] BCTEWorkers = BlockCityTycoonEventsListener.getBCTEventsWorker().stream().filter(worker ->
                                        worker[0].getPlayerUUID().equals(pl.getUniqueId())).findAny().orElse(null);

                                for (BlockCityTycoonEventWorker worker : BCTEWorkers) {
                                    if (worker != null) {
                                        if (worker.getBCTEvent() instanceof RainEvent) {
                                            worker.stopEventWork();
                                            worker.createEventWork();
                                        }
                                    }
                                }


                                for (PlaceOfRitualBlock place : playersRitualBlocks.get(pl.getUniqueId())) {
                                    Bukkit.getScheduler().runTaskLater(BlockCityTycoonEvents.getPlugin(), () -> {
                                        place.setRemoving(true);
                                        pl.sendBlockChange(new Location(pl.getWorld(), place.getX(), place.getY(), place.getZ()), Material.AIR, (byte) 0);

                                        place.setPlaced(false);
                                        BlockCityTycoonEvents.getPlayerEventsConfig().getConfig().set(String.format("%s.rain-event.ritual-blocks.%s.placed", pl.getUniqueId().toString(), place.getName()), false);
                                        BlockCityTycoonEvents.getPlayerEventsConfig().saveConfig();
                                    }, 20);
                                }

                                sendThunderbolt(pl, ritPlace.getX(), ritPlace.getY(), ritPlace.getZ());
                            }
                        }
                    }
                }
            }
        }
        BlockCityTycoonEvents.getPlayerEventsConfig().saveConfig();
    }

    private void sendThunderbolt(Player pl, double x, double y, double z) {
        int entityId = 0;
        boolean left = false;
        boolean right = false;
        for (Entity entity : pl.getWorld().getEntities()) { // находим пустое место для id молнии
            int entityIdLeft = entity.getEntityId() - 1;
            int entityIdRight;

            if (entityIdLeft == 0) {
                entityIdLeft = entity.getEntityId();
            }

            if (entity.getEntityId() != Integer.MAX_VALUE) {
                entityIdRight = entity.getEntityId() + 1;
            }
            else {
                entityIdRight = entity.getEntityId();
            }

            for (Entity entity1 : pl.getWorld().getEntities()) {
                if (entity1.getEntityId() == entityIdLeft) {
                    left = true;
                    break;
                }
                else if (entity1.getEntityId() == entityIdRight) {
                    right = true;
                    break;
                }
            }

            if (left) {
                entityId = entityIdLeft;
            }
            else if (right) {
                entityId = entityIdRight;
            }
        }

        WrapperPlayServerSpawnEntityWeather wrapper = new WrapperPlayServerSpawnEntityWeather();
        wrapper.setEntityID(entityId);
        wrapper.setType(1);
        wrapper.setX(x);
        wrapper.setY(y);
        wrapper.setZ(z);
        wrapper.sendPacket(pl);

        Location loc = new Location(pl.getWorld(), x, y, z);
        pl.playSound(loc , Sound.ENTITY_LIGHTNING_IMPACT, 1, 1);
        pl.playSound(loc, Sound.ENTITY_LIGHTNING_THUNDER, 1, 1);
    }

    public static List<PlaceOfRitualBlock> getPlacesOfRitualBlocks(Player pl) {
        return playersRitualBlocks.get(pl.getUniqueId());
    }
}
