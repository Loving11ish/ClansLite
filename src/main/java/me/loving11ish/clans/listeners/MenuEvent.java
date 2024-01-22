package me.loving11ish.clans.listeners;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.clans.utils.MessageUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.menusystem.Menu;
import me.loving11ish.clans.menusystem.paginatedMenu.ClanListGUI;
import me.loving11ish.clans.utils.ColorUtils;

public class MenuEvent implements Listener {

    private final FileConfiguration guiConfig = Clans.getPlugin().clanGUIFileManager.getClanGUIConfig();

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu) {
            Menu menu = (Menu) holder;
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu){
            if (((Menu) holder).getMenuName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.name")))){
                WrappedTask wrappedTask = ClanListGUI.autoGUIRefreshTask;
                if (!wrappedTask.isCancelled()){
                    wrappedTask.cancel();
                    MessageUtils.sendDebugConsole("Auto refresh task cancelled");
                }
            }
        }
    }
}
