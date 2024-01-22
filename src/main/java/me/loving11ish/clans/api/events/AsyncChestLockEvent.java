package me.loving11ish.clans.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.loving11ish.clans.models.Chest;
import me.loving11ish.clans.models.Clan;

public class AsyncChestLockEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Clan owningClan;
    private final Chest chest;
    private final Location chestLocation;

    public AsyncChestLockEvent(boolean isAsync, Player createdBy, Clan owningClan, Chest chest, Location chestLocation) {
        super(isAsync);
        this.createdBy = createdBy;
        this.owningClan = owningClan;
        this.chest = chest;
        this.chestLocation = chestLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public Clan getOwningClan() {
        return owningClan;
    }

    public Chest getChest() {
        return chest;
    }

    public Location getChestLocation() {
        return chestLocation;
    }
}
