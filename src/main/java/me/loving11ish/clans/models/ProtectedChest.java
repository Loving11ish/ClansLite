package me.loving11ish.clans.models;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProtectedChest {

    private final UUID clanId;
    private final Location chestLocation;

    public ProtectedChest(UUID clanId, Location location) {
        this.clanId = clanId;
        this.chestLocation = location;
    }

    public UUID getClanId() {
        return clanId;
    }

    public Location getLocation() {
        return chestLocation;
    }

}
