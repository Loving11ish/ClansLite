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
}
