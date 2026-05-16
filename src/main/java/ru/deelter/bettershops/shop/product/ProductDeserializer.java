package ru.deelter.bettershops.shop.product;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import ru.deelter.bettershops.shop.cost.CostDeserializer;
import ru.deelter.bettershops.shop.cost.ICost;
import ru.deelter.bettershops.utils.ItemStackDeserializer;

import java.util.ArrayList;
import java.util.List;

public class ProductDeserializer {

    public static @NonNull IProduct read(@NonNull JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        String type = obj.get("type").getAsString();
        ICost cost = CostDeserializer.read(obj.get("cost"));
        ItemStack icon;
        if (obj.has("icon")) {
            icon = deserializeItem(obj.getAsJsonObject("icon"));
        } else {
            icon = new ItemStack(org.bukkit.Material.CHEST);
        }

        return switch (type) {
            case "items" -> {
                List<ItemStack> items = new ArrayList<>();
                JsonArray jsonArray = obj.getAsJsonArray("items");
                for (JsonElement jsonElement : jsonArray) {
                    items.add(deserializeItem(jsonElement.getAsJsonObject()));
                }
                if (!obj.has("icon") && !items.isEmpty()) icon = items.get(0);
                yield new ItemsProduct(cost, icon, items);
            }
            case "commands" -> {
                List<String> commands = new ArrayList<>();
                JsonArray arr = obj.getAsJsonArray("commands");
                for (JsonElement e : arr) commands.add(e.getAsString());
                yield new CommandsProduct(cost, icon, commands);
            }
            case "permission" -> {
                List<String> perms = new ArrayList<>();
                JsonArray jsonArray = obj.getAsJsonArray("permissions");
                for (JsonElement jsonElement : jsonArray) perms.add(jsonElement.getAsString());
                yield new PermissionProduct(cost, icon, perms);
            }
            default -> throw new IllegalArgumentException("Unknown product type: " + type);
        };
    }

    public static ItemStack deserializeItem(JsonObject obj) {
        // Используем универсальный десериализатор, который понимает оба формата
        return ItemStackDeserializer.deserialize(obj);
    }
}