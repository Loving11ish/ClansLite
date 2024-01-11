package me.loving11ish.clans.models;

public enum ClanRank {

    LEADER, // everything below + add/remove co-leaders
    CO_LEADER, // everything below + change clan settings, promote, demote, clan sethome
    OFFICER, // everything below + invite, kick
    MEMBER,
    ; // clan chat, clan chest, clan home

    public static ClanRank findOrDefault(String name) {
        String nameUpper = name.toUpperCase();

        for (ClanRank rank : values()) {
            if (rank.name().equals(nameUpper)) {
                return rank;
            }
        }

        return MEMBER;
    }
}
