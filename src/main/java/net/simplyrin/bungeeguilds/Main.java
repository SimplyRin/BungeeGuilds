package net.simplyrin.bungeeguilds;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.simplyrin.bungeeguilds.commands.GuildCommand;
import net.simplyrin.bungeeguilds.messages.Messages;
import net.simplyrin.bungeeguilds.tools.ThreadPool;
import net.simplyrin.bungeeguilds.utils.ConfigManager;
import net.simplyrin.bungeeguilds.utils.GuildManager;
import net.simplyrin.bungeeguilds.utils.LanguageManager;
import net.simplyrin.bungeeguilds.utils.MessageBuilder;
import net.simplyrin.bungeeguilds.utils.MySQLManager;

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
@Getter
public class Main extends Plugin {

	private static Main instance;

	private ConfigManager configManager;
	private GuildManager guildManager;
	private LanguageManager languageManager;

	private boolean isEnabledMySQL;
	private MySQLManager mySQLManager;

	private GuildCommand guildCommand;

	@Override
	public void onEnable() {
		instance = this;

		if (this.getProxy().getPluginManager().getPlugin("BungeeFriends") == null) {
			this.info("&c" + Messages.CONSOLE_HYPHEN);
			this.info("");
			this.info("&4&lYou need requires BungeeFriends(version 1.5.14.1+) to use this plugin!");
			this.info("&4&lYou can download BungeeFriends at ");
			this.info("");
			this.info("&c" + Messages.CONSOLE_HYPHEN);
			return;
		}

		this.configManager = new ConfigManager(this);
		this.guildManager = new GuildManager(this);
		this.mySQLManager = new MySQLManager(this);

		this.isEnabledMySQL = this.mySQLManager.getConfig().getBoolean("Enable");

		this.guildCommand = new GuildCommand(this);
	}

	@Override
	public void onDisable() {

	}

	public String getPrefix() {
		return this.configManager.getConfig().getString("Plugin.Prefix");
	}

	public String getString(String key) {
		if (this.isEnabledMySQL) {
			return this.getMySQLManager().getEditor().get(key);
		}
		return this.getConfigManager().getConfig().getString(key);
	}

	public List<String> getStringList(String key) {
		if (this.isEnabledMySQL) {
			return this.getMySQLManager().getEditor().getList(key);
		}
		return this.getConfigManager().getConfig().getStringList(key);
	}

	public boolean getBoolean(String key) {
		if (this.isEnabledMySQL) {
			return Boolean.valueOf(this.getMySQLManager().getEditor().get(key));
		}
		return Boolean.valueOf(this.getConfigManager().getConfig().getBoolean(key));
	}

	public void set(String key, List<String> list) {
		if (this.isEnabledMySQL) {
			ThreadPool.run(() -> this.getMySQLManager().getEditor().set(key, list));
		} else {
			this.getConfigManager().getConfig().set(key, list);
		}
	}

	public void set(String key, String value) {
		if (this.isEnabledMySQL) {
			ThreadPool.run(() -> this.getMySQLManager().getEditor().set(key, String.valueOf(value)));
		} else {
			this.getConfigManager().getConfig().set(key, value);
		}
	}

	public void set(String key, boolean value) {
		if (this.isEnabledMySQL) {
			ThreadPool.run(() -> {
				this.getMySQLManager().getEditor().set(key, String.valueOf(value));
			});
		} else {
			this.getConfigManager().getConfig().set(key, value);
		}
	}


	@SuppressWarnings("deprecation")
	public void info(String args) {
		this.getProxy().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPrefix() + args));
	}

	@SuppressWarnings("deprecation")
	public void info(ProxiedPlayer player, String args) {
		if (args.equals("") || args == null) {
			return;
		}
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPrefix() + args));
	}

	@SuppressWarnings("deprecation")
	public void info(UUID uuid, String args) {
		if (args.equals("") || args == null) {
			return;
		}
		ProxiedPlayer player = this.getProxy().getPlayer(uuid);
		if (player != null) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPrefix() + args));
		}
	}

	public void info(ProxiedPlayer player, TextComponent args) {
		if (args.getText().equals("") || args == null) {
			return;
		}
		player.sendMessage(MessageBuilder.get(this.getPrefix()), args);
	}

}
