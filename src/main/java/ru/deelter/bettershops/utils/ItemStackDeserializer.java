package ru.deelter.bettershops.utils;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.deelter.bettershops.BetterShops;

public final class ItemStackDeserializer {

	public static ItemStack deserialize(JsonObject obj) {
		try {
			String nbtString;
			if (obj.has("nbt")) {
				nbtString = obj.get("nbt").getAsString();
			} else {
				nbtString = obj.toString();
			}
			ReadableNBT nbt = NBT.parseNBT(nbtString);
			return NBT.itemStackFromNBT(nbt);
		} catch (Exception e) {
			BetterShops.getInstance().getLogger().warning("Failed to deserialize item: " + e.getMessage());
			return new ItemStack(Material.STONE);
		}
	}
}