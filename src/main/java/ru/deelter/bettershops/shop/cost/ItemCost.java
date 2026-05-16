package ru.deelter.bettershops.shop.cost;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import ru.deelter.bettershops.BetterShops;

public record ItemCost(ItemStack item, int amount) implements ICost {
    @Override
    public boolean has(@NonNull Player player) {
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
    public void apply(@NonNull Player player) {
        int toRemove = amount;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || !item.isSimilar(content)) continue;
            int amt = content.getAmount();
            if (amt >= toRemove) {
                content.setAmount(amt - toRemove);
                if (content.getAmount() <= 0) {
                    player.getInventory().remove(content);
                }
                break;
            } else {
                toRemove -= amt;
                player.getInventory().remove(content);
            }
        }
    }

    @Override
    public @NonNull Component getDescription() {
        Component costMsg = BetterShops.getInstance().getLang().getMessage("shop-cost", null);
        if (costMsg == null) costMsg = Component.text("Cost");
        return costMsg
                .append(Component.text(": "))
                .append(item.displayName())
                .append(Component.text(" x" + amount));
    }

    @Override
    public @NonNull Component getPrice() {
        return item.displayName().append(Component.text(" x" + amount));
    }
}