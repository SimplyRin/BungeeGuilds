package net.simplyrin.bungeeguilds.utils;

import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.simplyrin.bungeeguilds.Main;
import net.simplyrin.bungeeguilds.exceptions.GuildNotJoinedException;

/**
 * Created by SimplyRin on 2019/06/22.
 *
 * Copyright (c) 2019 SimplyRin
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
public class GuildManager {

	private Main plugin;

	private HashMap<UUID, GuildUtils> maps = new HashMap<>();

	public GuildManager(Main plugin) {
		this.plugin = plugin;
	}

	public GuildUtils getPlayer(ProxiedPlayer player) {
		return new GuildUtils(player.getUniqueId());
	}

	public GuildUtils getPlayer(String uuid) {
		return new GuildUtils(UUID.fromString(uuid));
	}

	public GuildUtils getPlayer(UUID uniqueId) {
		return this.maps.get(uniqueId);
	}

	public class GuildUtils {

		private UUID uuid;
		private String joinedGuild;

		public GuildUtils(UUID uuid) {
			this.uuid = uuid;

			ProxiedPlayer player = plugin.getProxy().getPlayer(this.uuid);
			plugin.set("Name." + player.getName().toLowerCase(), player.getUniqueId().toString());
			plugin.set("UUID." + player.getUniqueId().toString(), player.getName().toLowerCase());

			String object = plugin.getString("Player." + this.uuid.toString() + ".Name");
			if (object == null || object.equals("")) {
				plugin.info("Creating data for player " + player.getName() + "...");

				plugin.set("Name." + player.getName().toLowerCase(), player.getUniqueId().toString());
				plugin.set("UUID." + player.getUniqueId().toString(), player.getName().toLowerCase());

				plugin.set("Player." + uuid.toString() + ".Name", player.getName());
				plugin.set("Player." + uuid.toString() + ".Language", plugin.getConfigManager().getConfig().getString("Plugin.Default-Language"));
				plugin.set("Player." + uuid.toString() + ".Toggle", true);
			}
		}

		public String getGuildName() {
			return plugin.getString("Player." + this.uuid.toString() + ".Joined-Guild");
		}

		public UUID getGuildOwner() {
			try {
				return UUID.fromString(plugin.getString("Guild." + this.getGuildName() + ".Owner"));
			} catch (Exception e) {
				return null;
			}
		}

		public boolean findGuild(String guildName) {
			guildName = guildName.toUpperCase();
			return !plugin.getString("Guild." + guildName + ".Name").equals("");
		}

		public void joinGuild(String guildName) /* throws GuildNotFoundException */ {
			guildName = guildName.toUpperCase();
			plugin.set("Player." + this.uuid.toString() + ".Joined-Guild", guildName);
			String key = "Guild." + guildName + ".Members";
			plugin.set(key, plugin.getStringList(key).add(this.uuid.toString()));
		}

		public void quitGuild() throws GuildNotJoinedException {
			String guild = this.getGuildName();
			if (guild == null || guild.equals("")) {
				throw new GuildNotJoinedException("Commands.Join.Not-Joined", "Nothing");
			}
			plugin.set("Player." + this.uuid.toString() + ".Joined-Guild", "");
		}

		public void disband() {

		}

		public String getDisplayName() {
			return net.simplyrin.bungeefriends.Main.getInstance().getFriendManager().getPlayer(this.uuid).getDisplayName();
		}
	}

}
