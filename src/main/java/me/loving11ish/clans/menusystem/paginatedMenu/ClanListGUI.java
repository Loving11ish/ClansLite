package me.loving11ish.clans.menusystem.paginatedMenu;

import me.loving11ish.clans.Clans;
import me.loving11ish.clans.menusystem.PaginatedMenu;
import me.loving11ish.clans.menusystem.PlayerMenuUtility;
import me.loving11ish.clans.menusystem.menu.ClanJoinRequestMenu;
import me.loving11ish.clans.models.Clan;
import me.loving11ish.clans.models.ClanPlayer;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

public class ClanListGUI extends PaginatedMenu {

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
            new ClanJoinRequestMenu(Clans.getPlayerMenuUtility(player)).open();

        } else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
            player.closeInventory();

        } else if (event.getCurrentItem().getType().equals(Material.STONE_BUTTON)) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.menu-controls.previous-page-icon-name")))) {
                if (page == 0) {
                    MessageUtils.sendPlayer(player, guiConfig.getString("clan-list.GUI-first-page"));
                } else {
                    page = page - 1;
                    super.open();
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(guiConfig.getString("clan-list.menu-controls.next-page-icon-name")))) {
                if (!((index + 1) >= clans.size())) {
                    page = page + 1;
                    super.open();
                } else {
                    MessageUtils.sendPlayer(player, guiConfig.getString("clan-list.GUI-last-page"));
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
        ArrayList<Clan> sanitisedClansList = new ArrayList<>();

        // Sanitise the list
        // This is broken as fuck!
        for (Clan clan : clans) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(clan.getClanOwner()));
            ClanPlayer clanOwner = UserMapStorageUtil.getClanPlayerByBukkitOfflinePlayer(offlinePlayer);
            if (clanOwner != null) {
                sanitisedClansList.add(clan);
            }
        }

        //Pagination loop template
        if (sanitisedClansList != null && !sanitisedClansList.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= sanitisedClansList.size()) break;
                if (sanitisedClansList.get(index) != null) {

                    //Create an item from our collection and place it into the inventory
                    String clanOwnerUUIDString = sanitisedClansList.get(i).getClanOwner();
                    UUID ownerUUID = UUID.fromString(clanOwnerUUIDString);
                    OfflinePlayer clanOwnerPlayer;

                    if (Clans.getPlugin().isOnlineMode()) {
                        clanOwnerPlayer = Bukkit.getOfflinePlayer(ownerUUID);
                    } else {
                        try {
                            clanOwnerPlayer = UserMapStorageUtil.getBukkitOfflinePlayerByUUID(ownerUUID);
                        } catch (Exception e) {
                            MessageUtils.sendConsole("Failed to retrieve player head info for UUID: " + clanOwnerUUIDString);
                            MessageUtils.sendDebugConsole(e.getMessage());
                            clanOwnerPlayer = null;
                        }
                    }

                    if (clanOwnerPlayer == null) {
                        continue;
                    }

                    Clan clan = ClansStorageUtil.findClanByOfflineOwner(clanOwnerPlayer);

                    if (clan == null) {
                        MessageUtils.sendDebugConsole("Failed to retrieve clan info for UUID: " + clanOwnerUUIDString);
                        MessageUtils.sendDebugConsole("Continuing to next clan...");
                        continue;
                    }

                    ItemStack clanItem = new ItemStack(Material.PAPER, 1);

                    ItemMeta clanItemMeta = clanItem.getItemMeta();
                    if (guiConfig.getBoolean("clan-list.icons.icon-display-name.use-clan-name")) {
                        String displayName = ColorUtils.translateColorCodes(clan.getClanFinalName());
                        clanItemMeta.setDisplayName(displayName);
                    } else {
                        clanItemMeta.setDisplayName(" ");
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

                    clanItemMeta.setLore(lore);
                    clanItemMeta.getPersistentDataContainer().set(new NamespacedKey(Clans.getPlugin(), "uuid"), PersistentDataType.STRING, clan.getClanOwner());

                    clanItem.setItemMeta(clanItemMeta);

                    inventory.setItem(index, clanItem);
                }
            }
        }

    }
}
