package me.loving11ish.clans.storage.mysql;

import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.models.ClanMember;
import me.loving11ish.clans.storage.mysql.serialization.ClanMemberSerialization;
import me.loving11ish.clans.storage.mysql.serialization.GeneralSerialization;
import me.loving11ish.clans.storage.mysql.serialization.ProtectedChestSerialization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class SqlClanQueryUtil {

    private static final String CLAN_TABLE = "clanslite_clans";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PREFIX = "prefix";
    private static final String COLUMN_MEMBERS = "members";
    private static final String COLUMN_ALLIES = "allies";
    private static final String COLUMN_ENEMIES = "enemies";
    private static final String COLUMN_FRIENDLY_FIRE = "friendly_fire";
    private static final String COLUMN_POINTS = "points";
    private static final String COLUMN_HOME = "home";
    private static final String COLUMN_MAX_PROTECTED_CHESTS = "max_protected_chests";
    private static final String COLUMN_PROTECTED_CHESTS = "protected_chests";


    /**
     * Don't use this unless it's for clan creation
     *
     * @param clan
     */
    @Deprecated
    public static boolean setClan(Connection connection, Clan clan) {
        String queryStr = String.format(
                "INSERT INTO `%s` " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                    "%s=?, " + // id
                    "%s=?, " + // name
                    "%s=?, " + // prefix
                    "%s=?, " + // members
                    "%s=?, " + // allies
                    "%s=?, " + // enemies
                    "%s=?, " + // friendly_fire
                    "%s=?, " + // points
                    "%s=?, " + // home
                    "%s=?, " + // max_protected_chests
                    "%s=?", // protected_chests
                CLAN_TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_PREFIX, COLUMN_MEMBERS, COLUMN_ALLIES, COLUMN_ENEMIES,
                COLUMN_FRIENDLY_FIRE, COLUMN_POINTS, COLUMN_HOME, COLUMN_MAX_PROTECTED_CHESTS, COLUMN_PROTECTED_CHESTS);

        try {
            PreparedStatement statement = connection.prepareStatement(queryStr);

            statement.setString(1, clan.getId().toString());
            statement.setString(2, clan.getName());
            statement.setString(3, clan.getPrefix());
            statement.setString(4, ClanMemberSerialization.serializeMemberList(clan.getMembers()));
            statement.setString(5, GeneralSerialization.serializeUUIDList(clan.getAllies()));
            statement.setString(6, GeneralSerialization.serializeUUIDList(clan.getEnemies()));
            statement.setBoolean(7, clan.isFriendlyFire());
            statement.setInt(8, clan.getPoints());
            statement.setString(9, GeneralSerialization.serializeLocation(clan.getHome().orElse(null)));
            statement.setInt(10, clan.getMaxAllowedProtectedChests());
            statement.setString(11, ProtectedChestSerialization.serializeProtectedChests(clan.getProtectedChests()));

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Clan getClan(Connection connection, UUID clanId) {
        String queryStr = String.format(
                "SELECT * FROM `%s` WHERE %s=?",
                CLAN_TABLE, COLUMN_ID
        );

        try {
            PreparedStatement statement = connection.prepareStatement(queryStr);
            statement.setString(1, clanId.toString());

            ResultSet result = statement.executeQuery();
            String id = result.getString(COLUMN_ID);
            String name = result.getString(COLUMN_NAME);
            String prefix = result.getString(COLUMN_PREFIX);
            String members = result.getString(COLUMN_MEMBERS);
            String allies = result.getString(COLUMN_ALLIES);
            String enemies = result.getString(COLUMN_ENEMIES);
            boolean friendlyFire = result.getBoolean(COLUMN_FRIENDLY_FIRE);
            int points = result.getInt(COLUMN_POINTS);
            String home = result.getString(COLUMN_HOME);
            int maxProtectedChests = result.getInt(COLUMN_MAX_PROTECTED_CHESTS);
            String protectedChests = result.getString(COLUMN_PROTECTED_CHESTS);

            if (!clanId.equals(UUID.fromString(id))) {
                throw new IllegalStateException("Clan ID mismatch!");
            }

            return new Clan(
                    clanId,
                    name,
                    prefix,
                    ClanMemberSerialization.deserializeMemberList(members),
                    GeneralSerialization.deserializeUUIDList(allies),
                    GeneralSerialization.deserializeUUIDList(enemies),
                    friendlyFire,
                    points,
                    GeneralSerialization.deserializeLocation(home),
                    maxProtectedChests,
                    ProtectedChestSerialization.deserializeProtectedChests(clanId, protectedChests)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<ClanMember> getClanMembers(Connection connection, UUID clanId) {
        String queryStr = String.format(
                "SELECT %s FROM `%s` WHERE %s=?",
                COLUMN_MEMBERS, CLAN_TABLE, COLUMN_ID
        );

        try {
            PreparedStatement statement = connection.prepareStatement(queryStr);
            statement.setString(1, clanId.toString());

            ResultSet result = statement.executeQuery();
            String members = result.getString(COLUMN_MEMBERS);

            return ClanMemberSerialization.deserializeMemberList(members);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
