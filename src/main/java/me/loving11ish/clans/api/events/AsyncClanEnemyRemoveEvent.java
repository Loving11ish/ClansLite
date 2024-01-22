package me.loving11ish.clans.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.loving11ish.clans.models.Clan;

public class AsyncClanEnemyRemoveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Clan clan;
    private final Player exEnemyClanCreatedBy;
    private final Clan exEnemyClan;

    public AsyncClanEnemyRemoveEvent(boolean isAsync, Player createdBy, Clan clan, Player exEnemyClanCreatedBy, Clan exEnemyClan) {
        super(isAsync);
        this.createdBy = createdBy;
        this.clan = clan;
        this.exEnemyClanCreatedBy = exEnemyClanCreatedBy;
        this.exEnemyClan = exEnemyClan;
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

    public Player getExEnemyClanCreatedBy() {
        return exEnemyClanCreatedBy;
    }

    public Clan getExEnemyClan() {
        return exEnemyClan;
    }
}
