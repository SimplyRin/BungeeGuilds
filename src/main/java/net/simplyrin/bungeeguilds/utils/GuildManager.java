package net.simplyrin.bungeeguilds.utils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.simplyrin.bungeeguilds.Main;
import net.simplyrin.bungeeguilds.exceptions.GuildAlreadyJoinedException;
import net.simplyrin.bungeeguilds.exceptions.GuildPersonNotFoundException;
import net.simplyrin.bungeeguilds.utils.LanguageManager.LanguageUtils;

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

	private HashMap<UUID, PlayerUtils> maps = new HashMap<>();

	public GuildManager(Main plugin) {
		this.plugin = plugin;
	}

	public PlayerUtils getPlayer(ProxiedPlayer player) {
		return new PlayerUtils(player.getUniqueId());
	}

	public PlayerUtils getPlayer(String uuid) {
		return new PlayerUtils(UUID.fromString(uuid));
	}

	public PlayerUtils getPlayer(UUID uniqueId) {
		return this.maps.get(uniqueId);
	}

	public Guild getGuildByName(String name) {
		String guildOwner = plugin.getString("Guild." + name.toUpperCase() + ".Owner");

		// Guild に参加またはなかったら null
		if (guildOwner == null || guildOwner.equals("")) {
			return null;
		}

		return new Guild(name.toUpperCase());
	}

	public Guild getGuildByJoinedMember(ProxiedPlayer player) /* throws GuildNotJoinedException */ {
		return this.getGuildByJoinedMember(player.getUniqueId());
	}

	public Guild getGuildByJoinedMember(UUID uniqueId) /* throws GuildNotJoinedException */ {
		String guildName = plugin.getString("Player." + uniqueId.toString() + ".Joined-Guild");

		// Guild に参加していなかったらスロー。
		// これってさ、スローか null 返すかどっちのほうがいいと思います？
		if (guildName == null || guildName.equals("")) {
			// throw new GuildNotJoinedException("Command.Kick.NotJoined", "%DISPLAYNAME% を " + uniqueId.toString() + " に変換する必要あり");
			return null;
		}

		return new Guild(guildName);
	}

	public class Guild {

		private String guildName;

		public Guild(String guildName) {
			this.guildName = guildName.toUpperCase();
		}

		public String getName() {
			return this.guildName;
		}

		public Guild setOwner(ProxiedPlayer player) {
			return this.setOwner(player.getUniqueId());
		}

		public Guild setOwner(UUID uniqueId) {
			this.updateConfig("Owner", uniqueId.toString());
			return this;
		}

		public UUID getOwner() {
			return UUID.fromString(this.getConfig("Owner"));
		}

		public Guild setTag(String tag) {
			this.updateConfig("Tag", tag);
			return null;
		}

		public Guild setTagColor(ChatColor chatColor) {
			this.updateConfig("Tag-Color", chatColor.toString());
			return this;
		}

		public Guild addMember(ProxiedPlayer player) throws GuildAlreadyJoinedException {
			return this.addMember(player.getUniqueId());
		}

		public Guild addMember(UUID uniqueId) throws GuildAlreadyJoinedException {
			List<String> list = plugin.getStringList("Guild." + this.guildName + ".Members");
			if (list.contains(uniqueId.toString())) {
				throw new GuildAlreadyJoinedException("Command.Join.AlreadyJoined", null);
			}
			list.add(uniqueId.toString());
			plugin.set("Guild." + this.guildName + ".Members", list);
			return this;
		}

		public Guild kickMember(ProxiedPlayer player, String reason) throws GuildPersonNotFoundException {
			return kickMember(player.getUniqueId(), reason);
		}

		public Guild kickMember(UUID uniqueId, String reason) throws GuildPersonNotFoundException {
			List<String> list = plugin.getStringList("Guild." + this.guildName + ".Members");
			if (!list.contains(uniqueId.toString())) {
				throw new GuildPersonNotFoundException("Command.Kick.NotJoined", "%DISPLAYNAME% を " + uniqueId.toString() + " に変換する必要あり");
			}
			list.remove(uniqueId.toString());
			plugin.set("Guild." + this.guildName + ".Members", list);
			return this;
		}

		public Guild sendChat(String message) {
			List<String> list = plugin.getStringList("Guild." + this.guildName + ".Members");
			for (String uuid : list) {
				UUID uniqueId = UUID.fromString(uuid);

				PlayerUtils guildUtils = getPlayer(uniqueId);
				LanguageUtils langUtils = plugin.getLanguageManager().getPlayer(uniqueId);
				String l = langUtils.getString("Commands.Chat.Chat");
				l = l.replace("%DISPLAYNAME%", guildUtils.getDisplayName());
				l = l.replace("%MESSAGE%", message);
				l = l.replace("\n", "");
				plugin.info(uniqueId, l);
			}
			return this;
		}

		public Guild updateConfig(String key, String value) {
			plugin.set("Guild." + this.guildName + "." + key, value);
			return this;
		}

		public String getConfig(String key) {
			return plugin.getString("Guild." + this.guildName + "." + key);
		}

	}

	public class PlayerUtils {

		private UUID uuid;
		private String joinedGuild;

		public PlayerUtils(UUID uuid) {
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

		public Guild getGuild() {
			return getGuildByJoinedMember(this.uuid);
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

		public String getDisplayName() {
			return net.simplyrin.bungeefriends.Main.getInstance().getFriendManager().getPlayer(this.uuid).getDisplayName();
		}
	}

}
