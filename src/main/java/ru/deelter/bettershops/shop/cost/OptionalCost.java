package ru.deelter.bettershops.shop.cost;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import ru.deelter.bettershops.BetterShops;

import java.util.List;
import java.util.stream.Collectors;

public class OptionalCost implements ICost {
    private final List<ICost> costs;

    public OptionalCost(List<ICost> costs) {
        this.costs = costs;
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
        Component costWord = BetterShops.getInstance().getLang().getMessage("shop-cost", viewer, Placeholder.component("cost", getPrice(viewer)));
        return costWord == null ? Component.text("Cost: ").append(getPrice(viewer)) : costWord;
    }

    @Override
    public Component getPrice(Audience viewer) {
        return Component.join(JoinConfiguration.separator(Component.text(" / ")),
            costs.stream().map(iCost -> iCost.getPrice(viewer)).collect(Collectors.toList()));
    }
}