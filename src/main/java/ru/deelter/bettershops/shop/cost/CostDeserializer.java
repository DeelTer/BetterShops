package ru.deelter.bettershops.shop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CostDeserializer {

    public static ICost read(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        String type = obj.get("type").getAsString();
        return switch (type) {
            case "single" -> {
                String currency = obj.has("currency") ? obj.get("currency").getAsString() : "coins";
                double amount = obj.get("amount").getAsDouble();
                yield new SingleCost(currency, amount);
            }
            case "item" -> {
                int amount = obj.get("amount").getAsInt();
                JsonObject itemObj = obj.getAsJsonObject("item");
                ItemStack item = ItemStack.deserialize(
                        new com.google.gson.internal.LinkedTreeMap<>() {{
                            put("type", itemObj.get("id").getAsString());
                            put("amount", itemObj.get("count").getAsInt());
                        }}
                );
                yield new ItemCost(item, amount);
            }
            case "multi", "optional" -> {
                JsonArray arr = obj.getAsJsonArray("costs");
                List<ICost> costs = new java.util.ArrayList<>();
                for (JsonElement e : arr) {
                    costs.add(read(e));
                }
                if (type.equals("multi")) yield new MultiCost(costs);
                else yield new OptionalCost(costs);
            }
            default -> throw new IllegalArgumentException("Unknown cost type: " + type);
        };
    }
}