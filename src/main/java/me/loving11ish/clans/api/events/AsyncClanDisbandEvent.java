package me.loving11ish.clans.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncClanDisbandEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final String clanName;

    public AsyncClanDisbandEvent(boolean isAsync, Player createdBy, String clanName) {
        super(isAsync);
        this.createdBy = createdBy;
        this.clanName = clanName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public String getClan() {
        return clanName;
    }
}
