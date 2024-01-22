package me.loving11ish.clans.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.loving11ish.clans.models.ClanInvite;

import java.util.*;

public class ClanInviteUtil {

    private static Map<UUID, ClanInvite> invitesList = new HashMap<>();
    
    public static ClanInvite createInvite(String inviterUUID, String inviteeUUID){
        UUID uuid = UUID.fromString(inviterUUID);
        clearExpiredInvites();
        if (!invitesList.containsKey(uuid)){
            invitesList.put(uuid, new ClanInvite(inviterUUID, inviteeUUID));

            MessageUtils.sendDebugConsole("Clan invite created");

            return invitesList.get(uuid);
        }else {
            return null;
        }
    }

    public static boolean searchInvitee(String inviteeUUID){
        for (ClanInvite invite : invitesList.values()){
            if (invite.getInvitee().equals(inviteeUUID)){
                return true;
            }
        }
        return false;
    }

    public static void clearExpiredInvites(){
        int expiryTime = 25 * 1000;
        Date currentTime = new Date();
        for (ClanInvite clanInvite : invitesList.values()){
            if (currentTime.getTime() - clanInvite.getInviteTime().getTime() > expiryTime){
                invitesList.remove(clanInvite);

                MessageUtils.sendDebugConsole("Expired clan invites removed");
            }
        }
    }

    public static void emptyInviteList() throws UnsupportedOperationException{
        invitesList.clear();
    }

    public static void removeInvite(String inviterUUID){
        UUID uuid = UUID.fromString(inviterUUID);
        invitesList.remove(uuid);
    }

    public static Set<Map.Entry<UUID, ClanInvite>> getInvites(){
        return invitesList.entrySet();
    }

    @Deprecated
    public static Player getInviteOwner(String inviterUUID){
        if (inviterUUID.length() > 36){
            UUID uuid = UUID.fromString(inviterUUID);
            if (invitesList.containsKey(uuid)){

                MessageUtils.sendDebugConsole("Invite owner uuid: &d" + inviterUUID);

                return Bukkit.getPlayer(uuid);
            }
        }else {
            MessageUtils.sendConsole("warning", "&4An error occurred whilst getting an Invite Owner.");
            MessageUtils.sendConsole("warning", "&4Error: &3The provided UUID is too long.");
        }
        return null;
    }
}
