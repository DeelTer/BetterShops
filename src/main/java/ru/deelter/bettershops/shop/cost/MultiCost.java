package ru.deelter.bettershops.shop.cost;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import ru.deelter.bettershops.BetterShops;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MultiCost implements ICost {
    private final List<ICost> costs;
    private final Component description;
    private final Component price;

    public MultiCost(List<ICost> costs) {
        this.costs = costs;
        Component costMsg = BetterShops.getInstance().getLang().getMessage("shop-cost", null);
        if (costMsg == null) costMsg = Component.text("Cost");
        this.description = costMsg
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
    public Component getDescription() {
        return description;
    }

    @Override
    public Component getPrice() {
        return price;
    }
}