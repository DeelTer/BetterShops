package ru.deelter.bettershops.shop;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.bettershops.shop.product.ProductDeserializer;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ShopManager {
	private static final Map<String, Shop> SHOPS = new HashMap<>();
	private static JavaPlugin plugin;

	public static void init(JavaPlugin p) {
		plugin = p;
		reload();
	}

	public static void reload() {
		SHOPS.clear();
		File folder = new File(plugin.getDataFolder(), "shops");
		if (!folder.exists()) folder.mkdirs();
		File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
		if (files == null) return;

		for (File file : files) {
			try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
				JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
				String id = obj.get("id").getAsString();
				Component title = MiniMessage.miniMessage().deserialize(obj.get("title").getAsString());
				var products = obj.getAsJsonArray("products").asList().stream()
						.map(ProductDeserializer::read)
						.toList();
				SHOPS.put(id.toLowerCase(), new Shop(id, title, products));
				plugin.getLogger().info("Loaded shop: " + id);
			} catch (Exception e) {
				plugin.getLogger().warning("Failed to load shop " + file.getName() + ": " + e.getMessage());
			}
		}
	}

	public static Shop getShop(String id) {
		return SHOPS.get(id.toLowerCase());
	}

	public static Map<String, Shop> getShops() {
		return SHOPS;
	}
}