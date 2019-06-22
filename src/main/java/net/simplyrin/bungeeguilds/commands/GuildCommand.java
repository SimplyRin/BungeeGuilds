package net.simplyrin.bungeeguilds.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.simplyrin.bungeeguilds.Main;
import net.simplyrin.bungeeguilds.exceptions.GuildNotFoundException;
import net.simplyrin.bungeeguilds.exceptions.GuildNotJoinedException;
import net.simplyrin.bungeeguilds.messages.Messages;
import net.simplyrin.bungeeguilds.messages.Permissions;
import net.simplyrin.bungeeguilds.utils.GuildManager.GuildUtils;
import net.simplyrin.bungeeguilds.utils.LanguageManager.LanguageUtils;

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
public class GuildCommand extends Command {

	private Main plugin;

	public GuildCommand(Main plugin) {
		super("guild", null, "g");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			this.plugin.info(Messages.INGAME_ONLY);
			return;
		}

		ProxiedPlayer player = (ProxiedPlayer) sender;
		GuildUtils myGuilds = this.plugin.getGuildManager().getPlayer(player);
		LanguageUtils langUtils = this.plugin.getLanguageManager().getPlayer(player);

		if (!player.hasPermission(Permissions.MAIN)) {
			this.plugin.info(player, langUtils.getString(Messages.NO_PERMISSION) + "(" + Permissions.MAIN + ")");
			return;
		}

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("join")) {
				if (args.length > 1) {
					try {
						myGuilds.joinGuild(args[1]);
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Join.Not-Joined").replace("%NAME%", myGuilds.getGuildName()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					} catch (GuildNotFoundException e) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString(e.getKey().replace("%NAME%", args[1].toUpperCase())));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					}
					return;
				}
				this.plugin.info(player, langUtils.getString("Commands.Join.Usage"));
				return;
			}

			if (args[0].equalsIgnoreCase("leave")) {
				try {
					myGuilds.quitGuild();
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Leave.Left"));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				} catch (GuildNotJoinedException e) {
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Leave.Not-Joined"));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				}
				return;
			}
		}
	}

}
