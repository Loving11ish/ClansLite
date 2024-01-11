package me.loving11ish.clans.storage.mysql.serialization;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GeneralSerialization {

    private static final int SERIALIZATION_VERSION = 1;

    public static String serializeLocation(Location home) {
        if (home == null) return null;

        return String.format(
                "%d,%s,%s,%s,%s,%s,%s",
                SERIALIZATION_VERSION, home.getWorld().getName(), home.getX(),
                home.getY(), home.getZ(), home.getYaw(), home.getPitch()
        );
    }

    public static Location deserializeLocation(String serializedLoc) {
        if (serializedLoc == null || serializedLoc.isEmpty()) return null;

        String[] parts = serializedLoc.split(",");

        return new Location(
                // ignore 0, it's the version
                Bukkit.getWorld(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Double.parseDouble(parts[4]),
                Float.parseFloat(parts[5]),
                Float.parseFloat(parts[6])
        );
    }

    public static String serializeUUIDList(ImmutableList<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (UUID uuid : uuids) {
            builder.append(uuid.toString()).append('\n');
        }

        return builder.toString();
    }

    public static ImmutableList<UUID> deserializeUUIDList(String serializedList) {
        if (serializedList == null || serializedList.isEmpty()) return ImmutableList.of();

        ImmutableList<String> stringsList = ImmutableList.copyOf(serializedList.split("\n"));

        return stringsList.stream()
                .map(UUID::fromString)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        ImmutableList::copyOf)
                );
    }

    public static String serializeLocationList(List<Location> locations) {
        if (locations == null || locations.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (Location location : locations) {
            builder.append(serializeLocation(location)).append('\n');
        }

        return builder.toString();
    }

    public static List<Location> deserializeLocationList(String serializedList) {
        if (serializedList == null || serializedList.isEmpty()) return ImmutableList.of();

        ImmutableList<String> stringsList = ImmutableList.copyOf(serializedList.split("\n"));

        return stringsList.stream()
                .map(GeneralSerialization::deserializeLocation)
                .collect(Collectors.toList());
    }


}
