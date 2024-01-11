package me.loving11ish.clans.models;

import java.util.UUID;

public class ClanMember {

    private final UUID clanId;

    private ClanRank rank;

    public ClanMember(UUID clanId, ClanRank rank) {
        this.clanId = clanId;
        this.rank = rank;
    }

    public UUID getClanId() {
        return clanId;
    }

    public ClanRank getRank() {
        return rank;
    }

    public void setRank(ClanRank rank) {
        this.rank = rank;
    }
}
