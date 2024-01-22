package me.loving11ish.clans.api.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncClanOfflineDisbandEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final OfflinePlayer createdBy;
    private final String clanName;

    public AsyncClanOfflineDisbandEvent(boolean isAsync, OfflinePlayer createdBy, String clanName) {
        super(isAsync);
        this.createdBy = createdBy;
        this.clanName = clanName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public OfflinePlayer getCreatedBy() {
        return createdBy;
    }

    public String getClan() {
        return clanName;
    }
}
