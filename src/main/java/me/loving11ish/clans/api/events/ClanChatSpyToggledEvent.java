package me.loving11ish.clans.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.loving11ish.clans.models.ClansLitePlayer;

public class ClanChatSpyToggledEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final ClansLitePlayer clansLitePlayer;
    private final boolean clanChatSpyState;

    public ClanChatSpyToggledEvent(Player createdBy, ClansLitePlayer clansLitePlayer, boolean clanChatSpyState) {
        this.createdBy = createdBy;
        this.clansLitePlayer = clansLitePlayer;
        this.clanChatSpyState = clanChatSpyState;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public ClansLitePlayer getClanPlayer() {
        return clansLitePlayer;
    }

    public boolean isClanChatSpyState() {
        return clanChatSpyState;
    }
}
