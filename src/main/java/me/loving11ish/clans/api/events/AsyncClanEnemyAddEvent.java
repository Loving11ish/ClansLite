package me.loving11ish.clans.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.loving11ish.clans.models.Clan;

public class AsyncClanEnemyAddEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final Clan clan;
    private final Player enemyClanCreatedBy;
    private final Clan enemyClan;

    public AsyncClanEnemyAddEvent(boolean isAsync, Player createdBy, Clan clan, Player enemyClanCreatedBy, Clan enemyClan) {
        super(isAsync);
        this.createdBy = createdBy;
        this.clan = clan;
        this.enemyClanCreatedBy = enemyClanCreatedBy;
        this.enemyClan = enemyClan;
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

    public Player getEnemyClanCreatedBy() {
        return enemyClanCreatedBy;
    }

    public Clan getEnemyClan() {
        return enemyClan;
    }

}
