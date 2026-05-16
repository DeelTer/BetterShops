package ru.deelter.bettershops.shop.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ru.deelter.bettershops.shop.Shop;

@AllArgsConstructor
@Data
public class ShopGuiHolder implements InventoryHolder {

	private final Shop shop;
	private int page = 0;

	public ShopGuiHolder(Shop shop) {
		this.shop = shop;
	}

	public int getMaxPage() {
		int size = shop.products().size();
		return Math.max(0, (size - 1) / 45);
	}

	@Override
	public @NotNull Inventory getInventory() {
		return null;
	}
}