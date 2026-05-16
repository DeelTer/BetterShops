package ru.deelter.bettershops.shop.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.deelter.bettershops.BetterShops;
import ru.deelter.bettershops.config.Config;
import ru.deelter.bettershops.shop.Shop;
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
			// Используем getDescription() — он сам содержит слово «Cost» или «Цена»
			Component costLine = product.cost().getDescription()
					.decoration(TextDecoration.ITALIC, false);
			lore.add(costLine);

			meta.lore(lore);
			icon.setItemMeta(meta);
			inv.setItem(slot++, icon);
		}

		int rows = inv.getSize() / 9;
		int lastRow = (rows - 1) * 9;
		if (holder.getPage() > 0) {
			inv.setItem(lastRow, Config.get().getPrevPageItem());
		}
		if (holder.getPage() < holder.getMaxPage()) {
			inv.setItem(lastRow + 8, Config.get().getNextPageItem());
		}
		ItemStack filler = Config.get().getFillerItem();
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) inv.setItem(i, filler);
		}
	}

	public static void nextPage(Player player, ShopGuiHolder holder) {
		if (holder.getPage() < holder.getMaxPage()) {
			holder.setPage(holder.getPage() + 1);
			updateInventory(player.getOpenInventory().getTopInventory(), holder, player);
		}
	}

	public static void prevPage(Player player, ShopGuiHolder holder) {
		if (holder.getPage() > 0) {
			holder.setPage(holder.getPage() - 1);
			updateInventory(player.getOpenInventory().getTopInventory(), holder, player);
		}
	}

	public static void buy(Player player, ShopGuiHolder holder, int slot) {
		int start = holder.getPage() * 45;
		int index = start + slot;
		var products = holder.getShop().products();
		if (index >= 0 && index < products.size()) {
			IProduct product = products.get(index);
			if (product.cost().has(player)) {
				product.cost().apply(player);
				product.apply(player);
				Component msg = BetterShops.getInstance().getLang().getMessage("shop-purchased", player);
				if (msg != null) player.sendMessage(msg);
				updateInventory(player.getOpenInventory().getTopInventory(), holder, player);
			} else {
				Component msg = BetterShops.getInstance().getLang().getMessage("shop-cannot-afford", player);
				if (msg != null) player.sendMessage(msg);
			}
		}
	}
}