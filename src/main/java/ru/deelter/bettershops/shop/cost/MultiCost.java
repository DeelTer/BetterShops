package ru.deelter.bettershops.shop;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MultiCost implements ICost {
    private final List<ICost> costs;
    private final Component description;
    private final Component price;

    public MultiCost(List<ICost> costs) {
        this.costs = costs;
        this.description = Component.translatable("shop.cost")
                .append(Component.text(": "))
                .append(Component.join(JoinConfiguration.separator(Component.text(" & ")),
                        costs.stream().map(ICost::getPrice).collect(Collectors.toList())));
        this.price = description;
    }

    @Override
    public boolean has(Player player) {
        return costs.stream().allMatch(c -> c.has(player));
    }

    @Override
    public void apply(Player player) {
        costs.forEach(c -> c.apply(player));
    }

    @Override
    public Component getDescription() { return description; }
    @Override
    public Component getPrice() { return price; }
}