package ru.deelter.bettershops;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.bettershops.commands.ShopCommand;
import ru.deelter.bettershops.config.Config;
import ru.deelter.bettershops.config.Lang;
import ru.deelter.bettershops.shop.ShopManager;
import ru.deelter.bettershops.shop.gui.ShopListener;

@Getter
public final class BetterShops extends JavaPlugin {

	@Getter
	private static BetterShops instance;
	private Lang lang;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		reloadConfig();

		Config.init(getConfig());
		lang = new Lang(this);
		ShopManager.init(this);

		getCommand("shop").setExecutor(new ShopCommand());
		Bukkit.getPluginManager().registerEvents(new ShopListener(), this);

		getLogger().info("BetterShops enabled.");
	}

	@Override
	public void onDisable() {
		getLogger().info("BetterShops disabled.");
	}

	public void reload() {
		reloadConfig();
		Config.init(getConfig());
		lang.reload();
		ShopManager.reload();
	}
}