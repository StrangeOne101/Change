package com.strangeone101.easygui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class MenuListener implements Listener {

    private static boolean registered = false;

    public MenuListener(Plugin plugin) {
        if (registered) return;

        registered = true;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMenuItemClicked(InventoryClickEvent event) {
        try {
            Inventory inventory = event.getInventory();
            if (inventory.getHolder() instanceof MenuBase) {
                MenuBase menu = (MenuBase) inventory.getHolder();
                if (event.getWhoClicked() instanceof Player) {
                    Player player = (Player) event.getWhoClicked();
                    if (event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
                        int index = event.getRawSlot();
                        if (index < inventory.getSize()) {
                            event.setCancelled(true);
                            menu.onMenuClick(player, index, event.getClick(), event.getCursor());
                        } else {
                            menu.setLastClickedSlot(index);
                        }
                    }
                }
            }
        } catch (Exception e) {
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(ChatColor.RED + "An error occured while processing the clickevent.");
            e.printStackTrace();
        }
    }

    public static boolean isRegistered() {
        return registered;
    }
}
