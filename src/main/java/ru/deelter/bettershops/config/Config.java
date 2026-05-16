package ru.deelter.bettershops.config;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class Config {

	private static Config instance;
	private final int guiRows;
	private final ItemStack nextPageItem;
	private final ItemStack prevPageItem;
	private final ItemStack fillerItem;

	public static void init(FileConfiguration config) {
		instance = new Config(config);
	}

	public static Config get() {
		return instance;
	}

	private Config(FileConfiguration config) {
		guiRows = config.getInt("gui.rows", 6);
		nextPageItem = buildGuiItem(config.getConfigurationSection("gui.next-page-item"), "ARROW", "<color:#FFD700>Next Page");
		prevPageItem = buildGuiItem(config.getConfigurationSection("gui.prev-page-item"), "ARROW", "<color:#FFD700>Previous Page");
		fillerItem = buildGuiItem(config.getConfigurationSection("gui.filler-item"), "BLACK_STAINED_GLASS_PANE", " ");
	}

	private ItemStack buildGuiItem(org.bukkit.configuration.ConfigurationSection section, String defaultMat, String defaultName) {
		Material mat = Material.matchMaterial(section != null ? section.getString("material", defaultMat) : defaultMat);
		if (mat == null) mat = Material.valueOf(defaultMat);
		ItemStack item = new ItemStack(mat);
		String name = section != null ? section.getString("name", defaultName) : defaultName;
		if (name != null && !name.isEmpty()) {
			ItemMeta meta = item.getItemMeta();
			meta.displayName(MiniMessage.miniMessage().deserialize(name));
			item.setItemMeta(meta);
		}
		return item;
	}
}