package me.loving11ish.clans.menusystem.paginatedMenu;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.clans.Clans;
import me.loving11ish.clans.menusystem.PaginatedMenu;
import me.loving11ish.clans.menusystem.PlayerMenuUtility;
import me.loving11ish.clans.menusystem.menu.ClanJoinRequestMenu;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.utils.ClansStorageUtil;
import me.loving11ish.clans.utils.ColorUtils;
import me.loving11ish.clans.utils.MessageUtils;
import me.loving11ish.clans.utils.UserMapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClanListGUI extends PaginatedMenu {

    public static WrappedTask autoGUIRefreshTask;

    private final FileConfiguration guiConfig = Clans.getPlugin().clanGUIFileManager.getClanGUIConfig();
    private final FileConfiguration messagesConfig = Clans.getPlugin().messagesFileManager.getMessagesConfig();

    public ClanListGUI(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ColorUtils.translateColorCodes(guiConfig.getString("clan-list.name"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ArrayList<Clan> clans = new ArrayList<>(ClansStorageUtil.getClanList());

        if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
            Clan onlineClanOwner = ClansStorageUtil.findClanByOwner(player);
            Clan onlineClanPlayer = ClansStorageUtil.findClanByPlayer(player);
            UUID target = UUID.fromString(event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Clans.getPlugin(), "uuid"), PersistentDataType.STRING));
            if (onlineClanOwner != null) {
                MessageUtils.sendPlayer(player, messagesConfig.getString("clan-invite-failed-own-clan"));
                return;
            }
            if (onlineClanPlayer != null) {
                UUID ownerUUID = UUID.fromString(onlineClanPlayer.getClanOwner());
                if (ownerUUID.equals(target)) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-invite-failed-own-clan"));
                    return;
                }
            }
            PlayerMenuUtility playerMenuUtility = Clans.getPlayerMenuUtility(player);
            playerMenuUtility.setOfflineClanOwner(Bukkit.getOfflinePlayer(target));
            if (guiConfig.getBoolean("clan-list.icons.auto-refresh-data.enabled") && autoGUIRefreshTask != null && !autoGUIRefreshTask.isCancelled()) {
                autoGUIRefreshTask.cancel();
                MessageUtils.sendDebugConsole("Auto refresh task cancelled");
            }
            new ClanJoinRequestMenu(Clans.getPlayerMenuUtility(player)).open();

        } else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
            player.closeInventory();
            if (guiConfig.getBoolean("clan-list.icons.auto-refresh-data.enabled") && autoGUIRefreshTask != null && !autoGUIRefreshTask.isCancelled()) {
                autoGUIRefreshTask.cancel();
                MessageUtils.sendDebugConsole("Auto refresh task cancelled");
            }

        } else if (event.getCurrentItem().getType().equals(Material.STONE_BUTTON)) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.menu-controls.previous-page-icon-name")))) {
                if (page == 0) {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-list.GUI-first-page"));
                } else {
                    page = page - 1;
                    super.open();
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.menu-controls.next-page-icon-name")))) {
                if (!((index + 1) >= clans.size())) {
                    page = page + 1;
                    super.open();
                } else {
                    MessageUtils.sendPlayer(player, messagesConfig.getString("clan-list.GUI-last-page"));
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        // Add menu controls
        addMenuControls();

        // The thing you will be looping through to place items
        ArrayList<Clan> clans = new ArrayList<>(ClansStorageUtil.getClanList());

        // Auto refreshed data
        if (guiConfig.getBoolean("clan-list.icons.auto-refresh-data.enabled") && clans.size() < 45) {
            FoliaLib foliaLib = Clans.getFoliaLib();
            autoGUIRefreshTask = foliaLib.getImpl().runTimerAsync(() -> {
                //The thing you will be looping through to place items
                //ArrayList<Clan> clans = new ArrayList<>(ClansStorageUtil.getClanList());

                //Pagination loop template
                if (clans != null && !clans.isEmpty()) {
                    for (int i = 0; i < getMaxItemsPerPage(); i++) {
                        index = getMaxItemsPerPage() * page + i;
                        if (index >= clans.size()) break;
                        if (clans.get(index) != null) {

                            //Create an item from our collection and place it into the inventory
                            String clanOwnerUUIDString = clans.get(i).getClanOwner();
                            UUID ownerUUID = UUID.fromString(clanOwnerUUIDString);
                            OfflinePlayer clanOwnerPlayer = UserMapStorageUtil.getBukkitOfflinePlayerByUUID(ownerUUID);
                            if (clanOwnerPlayer == null) {
                                continue;
                            }
                            Clan clan = ClansStorageUtil.findClanByOfflineOwner(clanOwnerPlayer);

                            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);

                            if (Clans.getPlugin().isOnlineMode()) {
                                SkullMeta skull = (SkullMeta) playerHead.getItemMeta();
                                skull.setOwningPlayer(UserMapStorageUtil.getBukkitOfflinePlayerByUUID(ownerUUID));
                                playerHead.setItemMeta(skull);
                                MessageUtils.sendDebugConsole("Retrieved player head info for UUID: " + clanOwnerUUIDString);
                            }

                            ItemMeta meta = playerHead.getItemMeta();
                            if (guiConfig.getBoolean("clan-list.icons.icon-display-name.use-clan-name")) {
                                String displayName = ColorUtils.translateColorCodes(clan.getClanFinalName());
                                meta.setDisplayName(displayName);
                            } else {
                                meta.setDisplayName(" ");
                            }


                            ArrayList<String> lore = new ArrayList<>();
                            ArrayList<String> clanMembersList = clan.getClanMembers();
                            ArrayList<String> clanAlliesList = clan.getClanAllies();
                            ArrayList<String> clanEnemiesList = clan.getClanEnemies();
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.header")));
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.prefix") + clan.getClanPrefix()));
                            if (clanOwnerPlayer.isOnline()) {
                                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.owner-online") + clanOwnerPlayer.getName()));
                            } else {
                                lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.owner-offline") + clanOwnerPlayer.getName()));
                            }
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.members")));
                            for (String string : clanMembersList) {
                                UUID memberUUID = UUID.fromString(string);
                                OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
                                String offlineMemberName = member.getName();
                                lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineMemberName));
                                if (clanMembersList.size() >= 10) {
                                    int membersSize = clanMembersList.size() - 10;
                                    lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + membersSize + "&r &3&omore!"));
                                    break;
                                }
                            }
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.allies")));
                            for (String string : clanAlliesList) {
                                UUID allyUUID = UUID.fromString(string);
                                OfflinePlayer ally = Bukkit.getOfflinePlayer(allyUUID);
                                String offlineAllyName = ally.getName();
                                lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineAllyName));
                                if (clanAlliesList.size() >= 10) {
                                    int allySize = clanAlliesList.size() - 10;
                                    lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + allySize + "&r &3&omore!"));
                                    break;
                                }
                            }
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.enemies")));
                            for (String string : clanEnemiesList) {
                                UUID enemyUUID = UUID.fromString(string);
                                OfflinePlayer enemy = Bukkit.getOfflinePlayer(enemyUUID);
                                String offlineEnemyName = enemy.getName();
                                lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineEnemyName));
                                if (clanEnemiesList.size() >= 10) {
                                    int enemySize = clanEnemiesList.size() - 10;
                                    lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + enemySize + "&r &3&omore!"));
                                    break;
                                }
                            }
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.footer-1")));
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.action")));
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.footer-2")));

                            meta.setLore(lore);
                            meta.getPersistentDataContainer().set(new NamespacedKey(Clans.getPlugin(), "uuid"), PersistentDataType.STRING, clan.getClanOwner());

                            playerHead.setItemMeta(meta);

                            inventory.setItem(index, playerHead);
                            MessageUtils.sendDebugConsole("Auto refresh task running");
                        }
                    }
                }
            }, 0L, 5L, TimeUnit.SECONDS);

        // No auto refreshed data
        } else {
            //Pagination loop template
            if (clans != null && !clans.isEmpty()) {
                for (int i = 0; i < getMaxItemsPerPage(); i++) {
                    index = getMaxItemsPerPage() * page + i;
                    if (index >= clans.size()) break;
                    if (clans.get(index) != null) {

                        //Create an item from our collection and place it into the inventory
                        String clanOwnerUUIDString = clans.get(i).getClanOwner();
                        UUID ownerUUID = UUID.fromString(clanOwnerUUIDString);
                        OfflinePlayer clanOwnerPlayer = UserMapStorageUtil.getBukkitOfflinePlayerByUUID(ownerUUID);
                        if (clanOwnerPlayer == null) {
                            continue;
                        }
                        Clan clan = ClansStorageUtil.findClanByOfflineOwner(clanOwnerPlayer);

                        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);

                        if (Clans.getPlugin().isOnlineMode()) {
                            SkullMeta skull = (SkullMeta) playerHead.getItemMeta();
                            skull.setOwningPlayer(UserMapStorageUtil.getBukkitOfflinePlayerByUUID(ownerUUID));
                            playerHead.setItemMeta(skull);
                            MessageUtils.sendDebugConsole("Retrieved player head info for UUID: " + clanOwnerUUIDString);
                        }

                        ItemMeta meta = playerHead.getItemMeta();
                        String displayName = ColorUtils.translateColorCodes(clan.getClanFinalName());
                        meta.setDisplayName(displayName);

                        ArrayList<String> lore = new ArrayList<>();
                        ArrayList<String> clanMembersList = clan.getClanMembers();
                        ArrayList<String> clanAlliesList = clan.getClanAllies();
                        ArrayList<String> clanEnemiesList = clan.getClanEnemies();
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.header")));
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.prefix") + clan.getClanPrefix()));
                        if (clanOwnerPlayer.isOnline()) {
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.owner-online") + clanOwnerPlayer.getName()));
                        } else {
                            lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.owner-offline") + clanOwnerPlayer.getName()));
                        }
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.members")));
                        for (String string : clanMembersList) {
                            UUID memberUUID = UUID.fromString(string);
                            OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
                            String offlineMemberName = member.getName();
                            lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineMemberName));
                            if (clanMembersList.size() >= 10) {
                                int membersSize = clanMembersList.size() - 10;
                                lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + membersSize + "&r &3&omore!"));
                                break;
                            }
                        }
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.allies")));
                        for (String string : clanAlliesList) {
                            UUID allyUUID = UUID.fromString(string);
                            OfflinePlayer ally = Bukkit.getOfflinePlayer(allyUUID);
                            String offlineAllyName = ally.getName();
                            lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineAllyName));
                            if (clanAlliesList.size() >= 10) {
                                int allySize = clanAlliesList.size() - 10;
                                lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + allySize + "&r &3&omore!"));
                                break;
                            }
                        }
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.enemies")));
                        for (String string : clanEnemiesList) {
                            UUID enemyUUID = UUID.fromString(string);
                            OfflinePlayer enemy = Bukkit.getOfflinePlayer(enemyUUID);
                            String offlineEnemyName = enemy.getName();
                            lore.add(ColorUtils.translateColorCodes(" &7- &3" + offlineEnemyName));
                            if (clanEnemiesList.size() >= 10) {
                                int enemySize = clanEnemiesList.size() - 10;
                                lore.add(ColorUtils.translateColorCodes("&3&o+ &r&6&l" + enemySize + "&r &3&omore!"));
                                break;
                            }
                        }
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.footer-1")));
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.action")));
                        lore.add(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.icons.lore.footer-2")));

                        meta.setLore(lore);
                        meta.getPersistentDataContainer().set(new NamespacedKey(Clans.getPlugin(), "uuid"), PersistentDataType.STRING, clan.getClanOwner());

                        playerHead.setItemMeta(meta);

                        inventory.addItem(playerHead);
                        MessageUtils.sendDebugConsole("Auto refresh task not running");
                    }
                }
            }
        }
    }
}
