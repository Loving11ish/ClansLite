package me.loving11ish.clans.utils;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.clans.Clans;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TaskTimerUtils {

    private final static FoliaLib foliaLib = Clans.getFoliaLib();

    public static WrappedTask autoSaveTask;
    public static WrappedTask inviteClearTask;
    public static WrappedTask autoTopClansCacheUpdateTask;
    public static WrappedTask autoTopClanPlayersCacheUpdateTask;

    public static void runClansAutoSave() {
        autoSaveTask = foliaLib.getImpl().runTimerAsync(() -> {
            try {

                ClansStorageUtil.saveClans();

                if (Clans.getPlugin().getConfig().getBoolean("general.show-auto-save-task-message.enabled")) {
                    MessageUtils.sendConsole(Clans.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-save-complete"));
                }

                MessageUtils.sendDebugConsole("Wrapped task: " + autoSaveTask.toString());
                MessageUtils.sendDebugConsole("Auto save timed task loop run successfully");


            } catch (IOException e) {
                MessageUtils.sendConsole(Clans.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-save-failed"));
                e.printStackTrace();
                MessageUtils.sendDebugConsole("Wrapped task: " + autoSaveTask.toString());
                MessageUtils.sendDebugConsole("Auto save timed task loop run successfully");
            }
        }, 1L, 900L, TimeUnit.SECONDS);
    }

    public static void runClanInviteClear() {
        inviteClearTask = foliaLib.getImpl().runTimerAsync(() -> {
            try {

                ClanInviteUtil.emptyInviteList();

                if (Clans.getPlugin().getConfig().getBoolean("general.show-auto-invite-wipe-message.enabled")) {
                    MessageUtils.sendConsole(Clans.getPlugin().messagesFileManager.getMessagesConfig().getString("auto-invite-wipe-complete"));
                }

                MessageUtils.sendDebugConsole("Wrapped task: " + inviteClearTask.toString());
                MessageUtils.sendDebugConsole("Invite clear timed task loop run successfully");

            } catch (UnsupportedOperationException exception) {
                MessageUtils.sendConsole(Clans.getPlugin().messagesFileManager.getMessagesConfig().getString("invite-wipe-failed"));
                MessageUtils.sendDebugConsole("Wrapped task: " + inviteClearTask.toString());
                MessageUtils.sendDebugConsole("Invite clear timed task loop run successfully");
            }
        }, 1L, 900L, TimeUnit.SECONDS);
    }

    public static void runTopClansCacheUpdateTask() {
        autoTopClansCacheUpdateTask = foliaLib.getImpl().runTimerAsync(() -> {

            if (!ClansStorageUtil.getRawClansList().isEmpty()) {

                    ClansStorageUtil.setTopClansCache(ClansStorageUtil.getTopClansByClanPoints(10));

                    MessageUtils.sendDebugConsole("Wrapped task: " + autoTopClansCacheUpdateTask.toString());
                    MessageUtils.sendDebugConsole("Top clans cache update timed task loop run successfully");

                } else {
                    MessageUtils.sendDebugConsole("Wrapped task: " + autoTopClansCacheUpdateTask.toString());
                    MessageUtils.sendDebugConsole("There are no clans stored to update top clans cache from.");
                    MessageUtils.sendDebugConsole("Top clans cache update timed task loop run successfully");
            }
        }, 1L, 600L, TimeUnit.SECONDS);
    }

    public static void runTopClanPlayersCacheUpdateTask() {
        autoTopClanPlayersCacheUpdateTask = foliaLib.getImpl().runTimerAsync(() -> {

            if (!UserMapStorageUtil.getRawUserMapList().isEmpty()) {

                UserMapStorageUtil.setTopClanPlayersCache(UserMapStorageUtil.getTopClanPlayersByPlayerPoints(10));

                MessageUtils.sendDebugConsole("Wrapped task: " + autoTopClanPlayersCacheUpdateTask.toString());
                MessageUtils.sendDebugConsole("Top clans cache update timed task loop run successfully");

            } else {
                MessageUtils.sendDebugConsole("Wrapped task: " + autoTopClanPlayersCacheUpdateTask.toString());
                MessageUtils.sendDebugConsole("There are no clans stored to update top clans cache from.");
                MessageUtils.sendDebugConsole("Top clans cache update timed task loop run successfully");
            }
        }, 1L, 600L, TimeUnit.SECONDS);
    }
}
