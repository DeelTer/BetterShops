package ru.deelter.bettershops.shop.product;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.deelter.bettershops.shop.cost.ICost;

import java.util.List;

public record ItemsProduct(ICost cost, ItemStack icon, List<ItemStack> items) implements IProduct {
	@Override
	public void apply(Player player) {
		Location loc = player.getLocation();
		for (ItemStack item : items) {
			var leftover = player.getInventory().addItem(item.clone());
			leftover.values().forEach(drop -> loc.getWorld().dropItemNaturally(loc, drop));
		}
	}
}