package me.loving11ish.clans.api.events;

import me.loving11ish.clans.models.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncClanPrefixChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Clan clan;
    private final String oldPrefix;
    private final String newPrefix;

    public AsyncClanPrefixChangeEvent(boolean isAsync, Player createdBy, Clan clan, String oldPrefix, String newPrefix) {
        super(isAsync);
        this.createdBy = createdBy;
        this.clan = clan;
        this.oldPrefix = oldPrefix;
        this.newPrefix = newPrefix;
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

    public String getOldPrefix() {
        return oldPrefix;
    }

    public String getNewPrefix() {
        return newPrefix;
    }
}
