package ru.deelter.bettershops.config;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.deelter.bettershops.BetterShops;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class Lang {

    private final BetterShops plugin;
    private final Map<String, Map<String, String>> messages = new HashMap<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private String defaultLanguage;
    private boolean autoDetect;

    public Lang(BetterShops plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        messages.clear();
        defaultLanguage = plugin.getConfig().getString("language.default", "en");
        autoDetect = plugin.getConfig().getBoolean("language.auto-detect", true);

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        String[] defaults = {"en.yml", "ru.yml", "uk.yml"};
        for (String name : defaults) {
            File target = new File(langFolder, name);
            if (!target.exists()) plugin.saveResource("lang/" + name, false);
        }

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String langCode = file.getName().replace(".yml", "");
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                Map<String, String> map = new HashMap<>();
                if (cfg.isConfigurationSection("messages")) {
                    for (String key : cfg.getConfigurationSection("messages").getKeys(false)) {
                        String value = cfg.getString("messages." + key);
                        if (value != null) map.put(key, value);
                    }
                }
                messages.put(langCode, map);
            }
        }
    }

    @Nullable
    public Component getMessage(String key, @Nullable CommandSender sender, String... placeholders) {
        Player player = (sender instanceof Player p) ? p : null;
        String raw = resolveRaw(key, player);
        if (raw == null || raw.isEmpty()) return null;

        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be key-value pairs");
        }
        TagResolver[] resolvers = new TagResolver[placeholders.length / 2];
        for (int i = 0; i < placeholders.length; i += 2) {
            resolvers[i / 2] = Placeholder.unparsed(placeholders[i], placeholders[i + 1]);
        }
        return miniMessage.deserialize(raw, resolvers);
    }

    private String resolveRaw(String key, Player player) {
        String lang = resolveLanguage(player);
        Map<String, String> langMap = messages.get(lang);
        if (langMap != null && langMap.containsKey(key)) {
            String val = langMap.get(key);
            if (val != null && !val.isEmpty()) return val;
        }
        Map<String, String> defaultMap = messages.get(defaultLanguage);
        if (defaultMap != null && defaultMap.containsKey(key)) {
            String val = defaultMap.get(key);
            if (val != null && !val.isEmpty()) return val;
        }
        return null;
    }

    private String resolveLanguage(Player player) {
        if (!autoDetect || player == null) return defaultLanguage;
        Locale locale = player.locale();
        String shortLang = locale.getLanguage().toLowerCase();
        if (messages.containsKey(shortLang)) return shortLang;
        return defaultLanguage;
    }
}