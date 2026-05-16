package ru.deelter.bettershops.shop.product;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.deelter.bettershops.shop.cost.ICost;

import java.util.List;

public record CommandsProduct(ICost cost, ItemStack icon, List<String> commands) implements IProduct {
	@Override
	public void apply(Player player) {
		for (String cmd : commands) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(cmd, player.getName()));
		}
	}
}