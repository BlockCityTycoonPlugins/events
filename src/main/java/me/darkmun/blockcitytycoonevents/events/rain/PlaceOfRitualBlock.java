package me.darkmun.blockcitytycoonevents.events.rain;

import org.bukkit.Location;

public class PlaceOfRitualBlock {
    private int x, y, z;
    private boolean placed = false;
    private boolean placing = false;
    private boolean removing = false;

    public PlaceOfRitualBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PlaceOfRitualBlock(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public boolean isPlacing() {
        return placing;
    }

    public boolean isRemoving() {
        return removing;
    }

    public void setPlacing(boolean placing) {
        this.placing = placing;
    }

    public void setRemoving(boolean removing) {
        this.removing = removing;
    }
}
