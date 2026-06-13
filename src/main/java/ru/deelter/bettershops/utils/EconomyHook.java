package ru.deelter.bettershops.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import ru.deelter.multieconomy.MultiEconomy;
import ru.deelter.multieconomy.data.Currency;
import ru.deelter.multieconomy.utils.EconomyUtils;

public final class EconomyHook {

	private static MultiEconomy getEconomy() {
		return MultiEconomy.getInstance();
	}

	public static double getBalance(@NonNull Player player, String currencyId) {
		return getEconomy().getEconomyManager().getBalance(player.getUniqueId(), currencyId);
	}

	public static boolean hasEnough(Player player, String currencyId, double amount) {
		return getBalance(player, currencyId) >= amount;
	}

	public static void withdraw(@NonNull Player player, String currencyId, double amount) {
		getEconomy().getEconomyManager().removeBalance(player.getUniqueId(), currencyId, amount);
	}

	public static void deposit(@NonNull Player player, String currencyId, double amount) {
		getEconomy().getEconomyManager().addBalance(player.getUniqueId(), currencyId, amount);
	}

	public static @NonNull Component format(double amount, String currencyId, Audience viewer) {
		Currency currency = getEconomy().getEconomyManager().getCurrencies().get(currencyId);
		if (currency == null) {
			return Component.text(amount + " " + currencyId).decoration(TextDecoration.ITALIC, false);
		}
		return EconomyUtils.getVisual(amount, currency, viewer).decoration(TextDecoration.ITALIC, false);
	}
}