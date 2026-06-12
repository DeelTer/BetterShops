package ru.deelter.bettershops.shop.product;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.deelter.bettershops.shop.cost.ICost;

import java.util.List;

public record PermissionProduct(ICost cost, ItemStack icon, List<String> permissions) implements IProduct {
	@Override
	public boolean canBuy(Player player) {
		return permissions.stream().anyMatch(perm -> !player.hasPermission(perm));
	}

	@Override
	public void apply(Player player) {
		for (String perm : permissions) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
					String.format("lp user %s permission set %s true", player.getName(), perm));
		}
	}
}