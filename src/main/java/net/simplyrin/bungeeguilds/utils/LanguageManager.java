package net.simplyrin.bungeeguilds.utils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import com.google.common.base.Charsets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.simplyrin.bungeeguilds.Main;
import net.simplyrin.bungeeguilds.tools.Config;
import net.simplyrin.threadpool.ThreadPool;

/**
 * Created by SimplyRin on 2019/06/20.
 *
 * Copyright (C) 2019 SimplyRin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class LanguageManager {

	private Main plugin;

	private HashMap<String, Configuration> configMap;

	public LanguageManager(Main plugin) {
		this.plugin = plugin;
		this.configMap = new HashMap<>();

		File folder = plugin.getDataFolder();
		if (!folder.exists()) {
			folder.mkdir();
		}

		File languageFolder = new File(folder, "Language");
		if (!languageFolder.exists()) {
			languageFolder.mkdir();
		}

		this.plugin.info("Checking if a new language is available...");
		Configuration config;
		try {
			config = Config.getConfig(new URL("https://api.simplyrin.net/Bungee-Guilds/BungeeFriends/Languages/available.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.info("Failed connecting to the server.");
			return;
		}

		ThreadPool.run(() -> {
			for (String lang : config.getStringList("Langs")) {
				File file = new File(languageFolder, lang.toLowerCase() + ".yml");
				if (!file.exists()) {
					this.plugin.info("&7" + lang + " is available! Downloading...");
					Configuration langConfig;
					try {
						langConfig = Config.getConfig(new URL("https://api.simplyrin.net/Bungee-Guilds/BungeeFriends/Languages/Files/" + lang + ".yml"));
					} catch (Exception e) {
						return;
					}

					file = new File(languageFolder, lang.toLowerCase() + ".yml");
					Config.saveConfig(langConfig, file);

					this.plugin.info("&a" + lang + " language has been downloaded!");
				}
			}
		});
	}

	public LanguageUtils getPlayer(ProxiedPlayer player) {
		return new LanguageUtils(player.getUniqueId());
	}

	public LanguageUtils getPlayer(String uuid) {
		return new LanguageUtils(UUID.fromString(uuid));
	}

	public LanguageUtils getPlayer(UUID uniqueId) {
		return new LanguageUtils(uniqueId);
	}

	public class LanguageUtils {

		private UUID uuid;

		public LanguageUtils(UUID uuid) {
			this.uuid = uuid;

			Object lang = plugin.getString("Player." + this.uuid.toString() + ".Language");
			if (lang == null || lang.equals("")) {
				plugin.set("Player." + this.uuid.toString() + ".Language", "english");
			}

			if (LanguageManager.this.configMap.get("english") == null) {
				LanguageManager.this.configMap.put("english", Config.getConfig(this.getFile("english"), Charsets.UTF_8));
			}
		}

		public String getLanguage() {
			String key = plugin.getString("Player." + this.uuid.toString() + ".Language");
			if (key == null || key.equals("")) {
				return "english";
			}
			return key.substring(0, 1).toUpperCase() + key.substring(1, key.length());
		}

		public void setLanguage(String key) {
			plugin.set("Player." + this.uuid.toString() + ".Language", key);
		}

		public void reloadLanguage(String language) {
			File file = new File(this.getLanguagesFolder(), language + ".yml");
			LanguageManager.this.configMap.put(this.getLanguage().toLowerCase(), Config.getConfig(file, Charsets.UTF_8));
		}

		public String getString(String configKey) {
			Configuration config = LanguageManager.this.configMap.get(this.getLanguage());

			if (config == null) {
				File file = new File(this.getLanguagesFolder(), this.getLanguage().toLowerCase() + ".yml");
				LanguageManager.this.configMap.put(this.getLanguage(), Config.getConfig(file, Charsets.UTF_8));
			}

			config = LanguageManager.this.configMap.get(this.getLanguage());
			String result = config.getString(configKey);
			if (result.equals("")) {
				try {
					return LanguageManager.this.configMap.get("english").getString(configKey);
				} catch (Exception e) {
					plugin.info("&cAn error occured! You need remove 'english.yml' or set '" + configKey + "'!");
					e.printStackTrace();
				}
			}
			return result;
		}

		public File getLanguagesFolder() {
			File folder = plugin.getDataFolder();
			if (!folder.exists()) {
				folder.mkdir();
			}

			File languageFolder = new File(folder, "Language");
			if (!languageFolder.exists()) {
				languageFolder.mkdir();
			}

			return languageFolder;
		}

		public File getFile() {
			return this.getFile(this.getLanguage());
		}

		public File getFile(String key) {
			return new File(this.getLanguagesFolder(), key.toLowerCase() + ".yml");
		}

	}

}
