package ru.deelter.bettershops.shop.cost;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import ru.deelter.bettershops.BetterShops;

import java.util.List;
import java.util.stream.Collectors;

public class OptionalCost implements ICost {
    private final List<ICost> costs;
    private final Component price; // краткая строка (без "Cost:")

    public OptionalCost(List<ICost> costs) {
        this.costs = costs;
        this.price = Component.join(JoinConfiguration.separator(Component.text(" / ")),
                costs.stream().map(ICost::getPrice).collect(Collectors.toList()));
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
    public Component getDescription(Player viewer) {
        Component costWord = BetterShops.getInstance().getLang().getMessage("shop-cost", viewer);
        if (costWord == null) costWord = Component.text("Cost");
        return costWord
                .append(Component.text(": "))
                .append(price);
    }

    @Override
    public Component getPrice() {
        return price;
    }
}