package net.simplyrin.bungeeguilds.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.google.common.base.Charsets;

import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.simplyrin.bungeeguilds.Main;
import net.simplyrin.bungeeguilds.tools.Config;

/**
 * Created by SimplyRin on 2018/07/03.
 *
 * Copyright (c) 2018 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class ConfigManager {

	private Main plugin;

	@Getter
	private Runnable runnable;

	@Getter
	private Configuration config;

	public ConfigManager(Main plugin) {
		this.plugin = plugin;

		this.createConfig();
		this.save();
	}

	public void save() {
		File config = new File(this.plugin.getDataFolder(), "config.yml");
		Config.saveConfig(this.config, config);
	}

	public void load() {
		File config = new File(this.plugin.getDataFolder(), "config.yml");
		this.config = Config.getConfig(config);
	}

	public void createConfig() {
		File folder = this.plugin.getDataFolder();
		if (!folder.exists()) {
			folder.mkdir();
		}

		File config = new File(folder, "config.yml");
		if (!config.exists()) {
			try {
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.config = Config.getConfig(config, Charsets.UTF_8);

			this.config.set("Plugin.Prefix", "&7[&cGuild&7] &r");

			this.config.set("Plugin.Default-Language", "english");

			this.config.set("Guild.SAGIRI.Owner", "b0bb65a2-832f-4a5d-854e-873b7c4522ed");
			this.config.set("Guild.SAGIRI.Tag", "SAGIRI");
			this.config.set("Guild.SAGIRI.Tag-Color", "&7");
			this.config.set("Guild.SAGIRI.Officers", Arrays.asList("64636120-8633-4541-aa5f-412b42ddb04d"));
			this.config.set("Guild.SAGIRI.Members", Arrays.asList()); // Arrays.asList("b0bb65a2-832f-4a5d-854e-873b7c4522ed", "64636120-8633-4541-aa5f-412b42ddb04d"));

			this.config.set("Player.b0bb65a2-832f-4a5d-854e-873b7c4522ed.Name", "SimplyRin");
			this.config.set("Player.b0bb65a2-832f-4a5d-854e-873b7c4522ed.Language", "english");
			this.config.set("Player.b0bb65a2-832f-4a5d-854e-873b7c4522ed.Joined-Guild", "SAGIRI");
			this.config.set("Player.b0bb65a2-832f-4a5d-854e-873b7c4522ed.Toggle", true);

			this.config.set("Player.64636120-8633-4541-aa5f-412b42ddb04d.Name", "SimplyFoxy");
			this.config.set("Player.64636120-8633-4541-aa5f-412b42ddb04d.Language", "english");
			this.config.set("Player.64636120-8633-4541-aa5f-412b42ddb04d.Joined-Guild", "SAGIRI");
			this.config.set("Player.64636120-8633-4541-aa5f-412b42ddb04d.Toggle", true);

			Config.saveConfig(this.config, config);
		}

		this.config = Config.getConfig(config, Charsets.UTF_8);
		this.save();
	}

	public void resetStringValue(String key, String value) {
		if (this.config.getString(key).equals("")) {
			this.config.set(key, value);
		}
	}

	public void resetBooleanValue(String key) {
		if (!this.config.getBoolean(key)) {
			this.config.set(key, false);
		}
	}

}
