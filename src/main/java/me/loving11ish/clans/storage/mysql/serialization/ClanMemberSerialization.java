package me.loving11ish.clans.storage.mysql.serialization;

import com.google.common.collect.ImmutableList;
import me.loving11ish.clans.models.ClanMember;
import me.loving11ish.clans.models.ClanRank;
import me.loving11ish.clans.storage.mysql.SqlClanQueryUtil;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClanMemberSerialization {

    private static final int SERIALIZATION_VERSION = 1;

    public static ClanMember deserializeMember(String serializedMember) {
        if (serializedMember == null || serializedMember.isEmpty()) return null;

        String[] parts = serializedMember.split(",");

        return new ClanMember(
                // ignore 0, it's the version
                UUID.fromString(parts[1]),
                ClanRank.findOrDefault(parts[2])
        );
    }

    public static List<ClanMember> deserializeMemberList(String serializedMembers) {
        if (serializedMembers == null || serializedMembers.isEmpty()) return ImmutableList.of();

        return ImmutableList.copyOf(
                        serializedMembers.split("\n")).stream()
                .map(ClanMemberSerialization::deserializeMember)
                .collect(Collectors.toList());
    }

    public static String serializeMember(ClanMember member) {
        return String.format(
                "%s,%s,%s",
                SERIALIZATION_VERSION,
                member.getClanId().toString(),
                member.getRank().name()
        );
    }

    public static String serializeMemberList(List<ClanMember> members) {
        return members.stream()
                .map(ClanMemberSerialization::serializeMember)
                .collect(Collectors.joining("\n"));
    }

}
