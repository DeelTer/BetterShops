package ru.deelter.bettershops.shop.product;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.deelter.bettershops.shop.cost.ICost;

public interface IProduct {
	ICost cost();

	ItemStack icon();

	void apply(Player player);

	default boolean canBuy(Player player) {
		return true;
	}
}