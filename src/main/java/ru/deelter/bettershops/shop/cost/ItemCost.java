package ru.deelter.bettershops.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class ItemCost implements ICost {
    private final ItemStack item;
    private final int amount;

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
    public Component getDescription() {
        return Component.translatable("shop.cost")
                .append(Component.text(": "))
                .append(item.displayName())
                .append(Component.text(" x" + amount));
    }

    @Override
    public Component getPrice() {
        return item.displayName().append(Component.text(" x" + amount));
    }
}