package me.loving11ish.clans.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.loving11ish.clans.models.ClanPlayer;

public class AsyncClanChatSpyToggledEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final ClanPlayer clanPlayer;
    private final boolean clanChatSpyState;

    public AsyncClanChatSpyToggledEvent(boolean isAsync, Player createdBy, ClanPlayer clanPlayer, boolean clanChatSpyState) {
        super(isAsync);
        this.createdBy = createdBy;
        this.clanPlayer = clanPlayer;
        this.clanChatSpyState = clanChatSpyState;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public ClanPlayer getClanPlayer() {
        return clanPlayer;
    }

    public boolean isClanChatSpyState() {
        return clanChatSpyState;
    }
}
