package ru.deelter.bettershops.shop.cost;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import ru.deelter.bettershops.BetterShops;
import ru.deelter.bettershops.utils.EconomyHook;

public record SingleCost(String currency, double amount) implements ICost {

    @Override
    public boolean has(Player player) {
        return EconomyHook.getBalance(player, currency) >= amount;
    }

    @Override
    public void apply(Player player) {
        EconomyHook.withdraw(player, currency, amount);
    }

    @Override
    public @NonNull Component getDescription(Player viewer) {
        Component costWord = BetterShops.getInstance().getLang().getMessage("shop-cost", viewer, Placeholder.component("cost", getPrice(viewer)));
        return costWord == null ? Component.text("Cost: ").append(getPrice(viewer)) : costWord;
    }

    @Override
    public @NonNull Component getPrice(Audience viewer) {
        return EconomyHook.format(amount, currency, viewer);
    }
}