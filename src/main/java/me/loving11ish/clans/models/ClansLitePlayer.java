package me.loving11ish.clans.models;

import java.util.UUID;

public class ClansLitePlayer {

    private final UUID uuid;
    private String lastPlayerName;
    private boolean isBedrockPlayer;
    private int pointBalance;
    private boolean wantsChatSpy;

    public ClansLitePlayer(UUID uuid, String playerName, boolean isBedrockPlayer, int pointBalance, boolean wantsChatSpy) {
        this.uuid = uuid;
        this.lastPlayerName = playerName;
        this.isBedrockPlayer = isBedrockPlayer;
        this.pointBalance = pointBalance;
        this.wantsChatSpy = wantsChatSpy;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastPlayerName() {
        return lastPlayerName;
    }

    public void setLastPlayerName(String lastPlayerName) {
        this.lastPlayerName = lastPlayerName;
    }

    public boolean isBedrockPlayer() {
        return isBedrockPlayer;
    }

    public void setBedrockPlayer(boolean bedrockPlayer) {
        isBedrockPlayer = bedrockPlayer;
    }

    public int getPointBalance() {
        return pointBalance;
    }

    public void setPointBalance(int pointBalance) {
        this.pointBalance = pointBalance;
    }

    public boolean wantsChatSpy() {
        return wantsChatSpy;
    }

    public void setWantsChatSpy(boolean wantsChatSpy) {
        this.wantsChatSpy = wantsChatSpy;
    }

}
