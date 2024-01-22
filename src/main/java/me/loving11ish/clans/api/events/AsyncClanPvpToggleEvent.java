package me.loving11ish.clans.api.events;

import me.loving11ish.clans.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncClanPvpToggleEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Clan clan;
    private final boolean pvpToggleState;

    public AsyncClanPvpToggleEvent(boolean isAsync, Player createdBy, Clan clan, boolean pvpToggleState) {
        super(isAsync);
        this.createdBy = createdBy;
        this.clan = clan;
        this.pvpToggleState = pvpToggleState;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public Clan getClan() {
        return clan;
    }

    public boolean isPvpToggleState() {
        return pvpToggleState;
    }
}
