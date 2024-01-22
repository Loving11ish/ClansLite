package me.loving11ish.clans.api.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncChestBreakEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Location chestLocation;
    private final Block block;
    private final String owningClanOwnerUUID;
    private final String owningClanName;

    public AsyncChestBreakEvent(boolean isAsync, Location chestLocation, Block block, String owningClanOwnerUUID, String owningClanName) {
        super(isAsync);
        this.chestLocation = chestLocation;
        this.block = block;
        this.owningClanOwnerUUID = owningClanOwnerUUID;
        this.owningClanName = owningClanName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    public Block getBlock() {
        return block;
    }

    public String getOwningClanOwnerUUID() {
        return owningClanOwnerUUID;
    }

    public String getOwningClanName() {
        return owningClanName;
    }
}
