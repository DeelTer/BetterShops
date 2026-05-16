package ru.deelter.bettershops.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import ru.deelter.bettershops.shop.ShopManager;
import ru.deelter.bettershops.shop.gui.ShopGui;

import java.util.List;
import java.util.stream.Stream;

public class ShopCommand implements TabExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
		if (args.length == 0) {
			sender.sendMessage("Usage: /shop <open|reload>");
			return true;
		}

		switch (args[0].toLowerCase()) {
			case "open" -> {
				if (args.length < 2) {
					sender.sendMessage("Specify shop id.");
					return true;
				}
				String shopId = args[1];
				var shop = ShopManager.getShop(shopId);
				if (shop == null) {
					sender.sendMessage("Shop not found.");
					return true;
				}

				Player target = null;
				if (sender instanceof Player player) {
					target = player;
				}
				if (target == null) {
					if (args.length < 3) {
						sender.sendMessage("Console usage: /shop open <shop_id> <player_name>");
						return true;
					}
					target = Bukkit.getPlayer(args[2]);
					if (target == null) {
						sender.sendMessage("Player not found.");
						return true;
					}
				}

				if (!sender.hasPermission("bettershops.open." + shopId)) {
					if (sender instanceof Player player) {
						player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
					}
					sender.sendMessage("You don't have permission to open this shop.");
					return true;
				}

				ShopGui.open(target, shop);
				if (sender != target) {
					sender.sendMessage("Opened shop '" + shopId + "' for " + target.getName());
				}
			}
			case "reload" -> {
				if (!sender.hasPermission("bettershops.reload")) {
					sender.sendMessage("No permission.");
					return true;
				}
				ShopManager.reload();
				sender.sendMessage("Shops reloaded.");
			}
			default -> sender.sendMessage("Unknown subcommand. Use /shop <open|reload>");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NonNull [] args) {
		if (args.length == 1) {
			return List.of("open", "reload");
		} else if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
			return ShopManager.getShops().keySet().stream().toList();
		} else if (args.length == 3 && args[0].equalsIgnoreCase("open") && !(sender instanceof Player)) {
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
		}
		return List.of();
	}
}