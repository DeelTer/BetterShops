package ru.deelter.bettershops.shop.cost;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import ru.deelter.bettershops.BetterShops;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MultiCost implements ICost {
    private final List<ICost> costs;

    public MultiCost(List<ICost> costs) {
        this.costs = costs;
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
    public Component getDescription(Player viewer) {
        Component costWord = BetterShops.getInstance().getLang().getMessage("shop-cost", viewer, Placeholder.component("cost", getPrice(viewer)));
        return costWord == null ? Component.text("Cost: ").append(getPrice(viewer)) : costWord;
    }

    @Override
    public Component getPrice(Audience viewer) {
        return Component.join(JoinConfiguration.separator(Component.text(" & ")),
            costs.stream().map(iCost -> iCost.getPrice(viewer)).collect(Collectors.toList()));
    }
}