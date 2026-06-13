package ru.deelter.bettershops.shop.cost;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.deelter.bettershops.BetterShops;

public record ItemCost(ItemStack item, int amount) implements ICost {

    @Override
    public boolean has(Player player) {
        int total = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && item.isSimilar(content)) {
                total += content.getAmount();
                if (total >= amount) return true;
            }
        }
        return false;
    }

    @Override
    public void apply(Player player) {
        int toRemove = amount;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || !item.isSimilar(content)) continue;
            int amt = content.getAmount();
            if (amt >= toRemove) {
                content.setAmount(amt - toRemove);
                if (content.getAmount() <= 0) player.getInventory().remove(content);
                break;
            } else {
                toRemove -= amt;
                player.getInventory().remove(content);
            }
        }
    }

    @Override
    public Component getDescription(Player viewer) {
        Component costWord = BetterShops.getInstance().getLang().getMessage("shop-cost", viewer);
        if (costWord == null) costWord = Component.text("Cost");
        return costWord
                .append(Component.text(": "))
                .append(getPrice(viewer));
    }

    @Override
    public Component getPrice(Audience viewer) {
        return item.displayName().append(Component.text(" x" + amount));
    }
}