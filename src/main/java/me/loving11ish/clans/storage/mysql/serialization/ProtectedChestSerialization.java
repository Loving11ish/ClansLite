package me.loving11ish.clans.storage.mysql.serialization;

import com.google.common.collect.ImmutableList;
import me.loving11ish.clans.models.ProtectedChest;
import me.loving11ish.clans.storage.mysql.SqlClanQueryUtil;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProtectedChestSerialization {

    private static final int SERIALIZATION_VERSION = 1;

    public static ProtectedChest deserializeProtectedChest(String serializedChest) {
        if (serializedChest == null || serializedChest.isEmpty()) return null;

        String[] parts = serializedChest.split(",");

        return new ProtectedChest(
                // ignore 0, it's the version
                UUID.fromString(parts[1]),
                GeneralSerialization.deserializeLocation(parts[2])
        );
    }

    public static String serializeProtectedChest(ProtectedChest protectedChest) {
        return String.format(
                "%d,%s,%s",
                SERIALIZATION_VERSION,
                protectedChest.getClanId().toString(),
                GeneralSerialization.serializeLocation(protectedChest.getLocation())
        );
    }

    public static List<ProtectedChest> deserializeProtectedChests(UUID clanId, String serializedChests) {
        if (serializedChests == null || serializedChests.isEmpty()) return ImmutableList.of();

        return ImmutableList.copyOf(serializedChests.split("\n")).stream()
                .map(ProtectedChestSerialization::deserializeProtectedChest)
                .collect(Collectors.toList());
    }

    public static String serializeProtectedChests(List<ProtectedChest> protectedChests) {
        List<String> locations = protectedChests.stream()
                .map(ProtectedChestSerialization::serializeProtectedChest)
                .collect(Collectors.toList());

        return String.join("\n", locations);
    }

}
