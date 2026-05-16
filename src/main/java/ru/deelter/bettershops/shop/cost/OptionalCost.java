package ru.deelter.bettershops.shop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.stream.Collectors;

public class OptionalCost implements ICost {
    private final List<ICost> costs;
    private final Component description;
    private final Component price;

    public OptionalCost(List<ICost> costs) {
        this.costs = costs;
        this.description = Component.translatable("shop.cost")
                .append(Component.text(": "))
                .append(Component.join(JoinConfiguration.separator(Component.text(" / ")),
                        costs.stream().map(ICost::getPrice).collect(Collectors.toList())));
        this.price = description;
    }

    @Override
    public boolean has(Player player) {
        return costs.stream().anyMatch(c -> c.has(player));
    }

    @Override
    public void apply(Player player) {
        costs.stream().filter(c -> c.has(player)).findFirst().ifPresent(c -> c.apply(player));
    }

    @Override
    public Component getDescription() { return description; }
    @Override
    public Component getPrice() { return price; }
}