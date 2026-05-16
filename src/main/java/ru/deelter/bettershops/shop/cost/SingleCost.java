package ru.deelter.bettershops.shop.cost;

import net.kyori.adventure.text.Component;
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
    public @NonNull Component getDescription() {
        Component costMsg = BetterShops.getInstance().getLang().getMessage("shop-cost", null);
        if (costMsg == null) costMsg = Component.text("Cost");
        return costMsg
                .append(Component.text(": "))
                .append(EconomyHook.format(amount, currency));
    }

    @Override
    public Component getPrice() {
        return EconomyHook.format(amount, currency);
    }
}