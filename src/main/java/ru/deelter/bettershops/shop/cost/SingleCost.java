package ru.deelter.bettershops.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import ru.deelter.bettershops.utils.EconomyHook;

@Getter
@AllArgsConstructor
public class SingleCost implements ICost {
    private final String currency; // "coins", "crystals"
    private final double amount;

    @Override
    public boolean has(Player player) {
        return EconomyHook.getBalance(player, currency) >= amount;
    }

    @Override
    public void apply(Player player) {
        EconomyHook.withdraw(player, currency, amount);
    }

    @Override
    public Component getDescription() {
        return Component.translatable("shop.cost")
                .append(Component.text(": "))
                .append(EconomyHook.format(amount, currency));
    }

    @Override
    public Component getPrice() {
        return EconomyHook.format(amount, currency);
    }
}