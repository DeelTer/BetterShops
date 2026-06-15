package ru.deelter.bettershops.shop.gui;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.deelter.bettershops.BetterShops;
import ru.deelter.bettershops.config.Config;
import ru.deelter.bettershops.shop.Shop;
import ru.deelter.bettershops.shop.cost.ICost;
import ru.deelter.bettershops.shop.cost.OptionalCost;
import ru.deelter.bettershops.shop.product.IProduct;

import java.util.ArrayList;
import java.util.List;

public class ShopGui {

	public static void open(Player player, Shop shop) {
		ShopGuiHolder holder = new ShopGuiHolder(shop);
		int rows = Config.get().getGuiRows();
		Inventory inv = Bukkit.createInventory(holder, rows * 9, shop.title());
		updateInventory(inv, holder, player);
		player.openInventory(inv);
	}

	private static void updateInventory(Inventory inv, ShopGuiHolder holder, Player player) {
		inv.clear();
		Shop shop = holder.getShop();
		int start = holder.getPage() * 45;
		int end = Math.min(start + 45, shop.products().size());
		List<IProduct> visible = shop.products().subList(start, end);

		int slot = 0;
		for (IProduct product : visible) {
			ItemStack icon = product.icon().clone();
			ItemMeta meta = icon.getItemMeta();
			List<Component> lore = meta.lore() == null ? new ArrayList<>() : meta.lore();
			lore.add(Component.empty());
			// Используем getDescription, который сам добавляет локализованное "Цена: "
			Component costLine = product.cost().getDescription(player)
					.decoration(TextDecoration.ITALIC, false);
			lore.add(costLine);
			meta.lore(lore);
			icon.setItemMeta(meta);
			inv.setItem(slot++, icon);
		}

		int rows = inv.getSize() / 9;
		int lastRow = (rows - 1) * 9;
		if (holder.getPage() > 0) inv.setItem(lastRow, Config.get().getPrevPageItem());
		if (holder.getPage() < holder.getMaxPage()) inv.setItem(lastRow + 8, Config.get().getNextPageItem());

		ItemStack filler = Config.get().getFillerItem();
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) inv.setItem(i, filler);
		}
	}

	public static void nextPage(Player player, @NonNull ShopGuiHolder holder) {
		if (holder.getPage() < holder.getMaxPage()) {
			holder.setPage(holder.getPage() + 1);
			updateInventory(player.getOpenInventory().getTopInventory(), holder, player);
			player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
		}
	}

	public static void prevPage(Player player, @NonNull ShopGuiHolder holder) {
		if (holder.getPage() > 0) {
			holder.setPage(holder.getPage() - 1);
			updateInventory(player.getOpenInventory().getTopInventory(), holder, player);
			player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
		}
	}

	public static void buy(Player player, @NonNull ShopGuiHolder holder, int slot, boolean rightClick) {
		int start = holder.getPage() * 45;
		int index = start + slot;
		var products = holder.getShop().products();
		if (index >= 0 && index < products.size()) {
			IProduct product = products.get(index);
			if (!product.canBuy(player)) {
				Component message = BetterShops.getInstance().getLang().getMessage("shop-already-owned", player);
				if (message != null) player.sendMessage(message);

				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				return;
			}
			ICost cost = product.cost();
			int costIndex = (rightClick && cost instanceof OptionalCost) ? 1 : 0;
			boolean canAfford = (cost instanceof OptionalCost opt)
					? opt.has(player, costIndex)
					: cost.has(player);
			if (canAfford) {
				if (cost instanceof OptionalCost opt) {
					opt.apply(player, costIndex);
				} else {
					cost.apply(player);
				}
				product.apply(player);
				Component msg = BetterShops.getInstance().getLang().getMessage("shop-purchased", player);
				if (msg != null) player.sendMessage(msg);
				updateInventory(player.getOpenInventory().getTopInventory(), holder, player);

				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
			} else {
				Component msg = BetterShops.getInstance().getLang().getMessage("shop-cannot-afford", player);
				if (msg != null) player.sendMessage(msg);

				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
			}
		}
	}
}