package ru.deelter.bettershops.shop.cost;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface ICost {
	boolean has(Player player);

	void apply(Player player);

	Component getDescription(Player viewer);

	Component getPrice();
}