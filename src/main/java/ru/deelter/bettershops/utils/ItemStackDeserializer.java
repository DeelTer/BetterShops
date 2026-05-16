package ru.deelter.bettershops.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import ru.deelter.bettershops.BetterShops;

public final class ItemStackDeserializer {

	public static ItemStack deserialize(JsonObject obj) {
		try {
			String nbtString;
			if (obj.has("nbt")) {
				nbtString = obj.get("nbt").getAsString();
			} else {
				nbtString = convertLegacyToSNBT(obj);
			}
			ReadableNBT nbt = NBT.parseNBT(nbtString);
			return NBT.itemStackFromNBT(nbt);
		} catch (Exception e) {
			BetterShops.getInstance().getLogger().warning("Failed to deserialize item: " + e.getMessage());
			return new ItemStack(Material.STONE);
		}
	}

	private static @NonNull String convertLegacyToSNBT(@NonNull JsonObject legacy) {
		String id = legacy.get("id").getAsString();
		int count = legacy.has("count") ? legacy.get("count").getAsInt() : 1;

		StringBuilder snbt = new StringBuilder("{");
		snbt.append("id:\"").append(id).append("\",");
		snbt.append("Count:").append(count).append("b");

		if (legacy.has("components")) {
			JsonObject comp = legacy.getAsJsonObject("components");
			snbt.append(",tag:{");

			boolean first = true;
			for (var entry : comp.entrySet()) {
				if (!first) snbt.append(",");
				first = false;
				String key = entry.getKey();
				JsonElement value = entry.getValue();

				switch (key) {
					case "minecraft:custom_name" -> {
						String nameJson = value.getAsString();
						snbt.append("display:{Name:").append(escapeSNBTString(nameJson)).append("}");
					}
					case "minecraft:item_model" -> {
						String model = value.getAsString();
						snbt.append("ItemModel:\"").append(escapeString(model)).append("\"");
					}
					case "minecraft:custom_data" -> {
						String customDataStr = value.getAsString().trim();
						if (customDataStr.startsWith("\"") && customDataStr.endsWith("\"")) {
							customDataStr = customDataStr.substring(1, customDataStr.length() - 1);
						}
						snbt.append("CustomData:").append(customDataStr);
					}
					default -> {
						if (value.isJsonPrimitive()) {
							snbt.append(key).append(":").append(escapeSNBTValue(value.getAsString()));
						} else {
							snbt.append(key).append(":").append(value);
						}
					}
				}
			}
			snbt.append("}");
		}
		snbt.append("}");
		return snbt.toString();
	}

	private static @NonNull String escapeSNBTString(@NonNull String raw) {
		String escaped = raw.replace("\\", "\\\\").replace("\"", "\\\"");
		return "\"" + escaped + "\"";
	}

	private static @NonNull String escapeString(@NonNull String raw) {
		return raw.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static @NonNull String escapeSNBTValue(@NonNull String raw) {
		if (raw.matches("-?\\d+(\\.\\d+)?")) return raw;
		if (raw.equals("true") || raw.equals("false")) return raw;
		return escapeSNBTString(raw);
	}
}