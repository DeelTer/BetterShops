package ru.deelter.bettershops.shop.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class ShopListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!(event.getInventory().getHolder() instanceof ShopGuiHolder holder)) return;
		event.setCancelled(true);
		if (!(event.getWhoClicked() instanceof Player player)) return;
		int slot = event.getRawSlot();
		Inventory inv = event.getInventory();
		int lastRow = (inv.getSize() / 9 - 1) * 9;

		if (slot == lastRow) { // Prev page
			ShopGui.prevPage(player, holder);
		} else if (slot == lastRow + 8) { // Next page
			ShopGui.nextPage(player, holder);
		} else if (slot >= 0 && slot < inv.getSize() && slot / 9 < inv.getSize() / 9 - 1) {
			// product slot
			ShopGui.buy(player, holder, slot);
		}
	}

	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if (event.getInventory().getHolder() instanceof ShopGuiHolder) {
			event.setCancelled(true);
		}
	}
}