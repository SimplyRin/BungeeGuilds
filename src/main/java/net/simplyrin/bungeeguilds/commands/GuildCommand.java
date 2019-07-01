package net.simplyrin.bungeeguilds.commands;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.simplyrin.bungeeguilds.Main;
import net.simplyrin.bungeeguilds.exceptions.GuildAlreadyCreatedException;
import net.simplyrin.bungeeguilds.exceptions.GuildAlreadyJoinedException;
import net.simplyrin.bungeeguilds.exceptions.GuildNameAlreadyUsedException;
import net.simplyrin.bungeeguilds.messages.Messages;
import net.simplyrin.bungeeguilds.messages.Permissions;
import net.simplyrin.bungeeguilds.tools.Request;
import net.simplyrin.bungeeguilds.tools.ThreadPool;
import net.simplyrin.bungeeguilds.utils.GuildManager.Guild;
import net.simplyrin.bungeeguilds.utils.GuildManager.PlayerUtils;
import net.simplyrin.bungeeguilds.utils.LanguageManager.LanguageUtils;
import net.simplyrin.bungeeguilds.utils.MessageBuilder;

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
	private HashMap<String, Request> requestMap = new HashMap<>();
	private HashMap<String, Guild> inviteMap = new HashMap<>();

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
		PlayerUtils myGuilds = this.plugin.getGuildManager().getPlayer(player);
		Guild guild = this.plugin.getGuildManager().getGuildByJoinedMember(player);
		LanguageUtils langUtils = this.plugin.getLanguageManager().getPlayer(player);

		if (!player.hasPermission(Permissions.MAIN)) {
			this.plugin.info(player, langUtils.getString(Messages.NO_PERMISSION) + "(" + Permissions.MAIN + ")");
			return;
		}

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("create")) {
				if (guild != null) {
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Join.AlreadyJoined"));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					return;
				}

				if (args.length > 1) {
					try {
						this.plugin.getGuildManager().createGuild(player.getUniqueId(), args[1].toUpperCase());

						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Create.Created").replace("%GUILD_NAME%", args[1].toUpperCase()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					} catch (GuildAlreadyJoinedException e) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString(e.getKey()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					} catch (GuildNameAlreadyUsedException e) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString(e.getKey()).replace("%GUILD_NAME%", args[1].toUpperCase()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					} catch (GuildAlreadyCreatedException e) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString(e.getKey()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}
				}

				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				this.plugin.info(player, langUtils.getString("Commands.Create.Usage"));
				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				return;
			}

			if (args[0].equalsIgnoreCase("join")) {
				if (args.length > 1) {
					Guild targetGuild = this.plugin.getGuildManager().getGuildByName(args[1]);
					if (targetGuild == null) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Join.GuildNotFound").replace("%NAME%", args[1].toUpperCase()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}

					if (guild != null) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Join.AlreadyJoined"));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}

					this.requestMap.put(player.getName().toLowerCase(), new Request(args[1].toUpperCase(), player.getUniqueId(), myGuilds, targetGuild));
					ThreadPool.run(() -> {
						try {
							TimeUnit.MINUTES.sleep(5);
						} catch (Exception e) {
						}

						// 既に参加済み
						if (this.requestMap.get(player.getName().toLowerCase()) == null) {
							return;
						}

						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Join.Request.Expired").replace("%NAME%", targetGuild.getName()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));

						this.plugin.info(targetGuild.getOwner(), langUtils.getString(Messages.HYPHEN));
						this.plugin.info(targetGuild.getOwner(), langUtils.getString("Commands.Join.Request.Owner-Expired").replace("%DISPLAYNAME%", myGuilds.getDisplayName()));
						this.plugin.info(targetGuild.getOwner(), langUtils.getString(Messages.HYPHEN));
					});

					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Join.Request.Sent").replace("%NAME%", targetGuild.getName()));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));

					String sMessage = langUtils.getString("Commands.Join.Request.Received");
					sMessage = sMessage.replace("%DISPLAYNAME%", myGuilds.getDisplayName());

					String sCommand = sMessage.split("<")[1].split("|")[1].split(">")[0];

					String hover = langUtils.getString("Commands.Join.Request.ClickToRun").replace("%NAME%", player.getName());

					TextComponent t1 = MessageBuilder.get(sMessage.split("<")[0], "", null, "", false);
					t1.addExtra(MessageBuilder.get(langUtils.getString("Commands.Join.Request.ClickHere") + sMessage.split(">")[1], sCommand.replace("%NAME%", player.getName()), null, hover, false));

					this.plugin.info(targetGuild.getOwner(), langUtils.getString(Messages.HYPHEN));
					this.plugin.info(targetGuild.getOwner(), t1);
					this.plugin.info(targetGuild.getOwner(), langUtils.getString(Messages.HYPHEN));
					return;
				}

				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				this.plugin.info(player, langUtils.getString("Commands.Join.Usage"));
				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				return;
			}

			if (args[0].equalsIgnoreCase("invite")) {
				if (guild == null) {
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Leave.Not-Joined"));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					return;
				}

				if (!guild.getOwner().equals(player.getUniqueId())) {
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Invite.NoPermission"));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					return;
				}

				if (args.length > 1) {
					PlayerUtils targetGuild = this.plugin.getGuildManager().getPlayer(args[1]);
					if (targetGuild.getGuild() != null) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Invite.AlreadyOther").replace("%DISPLAYNAME%", targetGuild.getDisplayName()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}

					this.inviteMap.put(player.getName().toLowerCase(), guild);

					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Invite.Invited"));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));

					this.plugin.info(targetGuild.getUniqueId(), langUtils.getString(Messages.HYPHEN));
					this.plugin.info(targetGuild.getUniqueId(), langUtils.getString("Commands.Invite.Invite-Received"));
					this.plugin.info(targetGuild.getUniqueId(), langUtils.getString(Messages.HYPHEN));
					return;
				}

				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				this.plugin.info(player, langUtils.getString("Commands.Join.Usage"));
				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				return;
			}

			if (args[0].equalsIgnoreCase("accept")) {
				if (args.length > 1) {
					// Guild 既に参加してるマン
					if (guild != null) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Accept.NotSent"));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}

					Request request = this.requestMap.get(args[1].toLowerCase());
					if (request == null) {
						Guild targetGuild = this.inviteMap.get(args[1].toLowerCase());
						if (targetGuild != null) {
							try {
								List<UUID> mList = targetGuild.getMembers();

								targetGuild.addMember(player);

								this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
								this.plugin.info(player, langUtils.getString("Commands.Join.Joined").replace("%NAME%", targetGuild.getName()));
								this.plugin.info(player, langUtils.getString(Messages.HYPHEN));

								for (UUID uniqueId : mList) {
									LanguageUtils targetLangUtils = this.plugin.getLanguageManager().getPlayer(uniqueId);
									this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
									this.plugin.info(uniqueId, targetLangUtils.getString("Commands.Join.OtherJoined").replace("%DISPLAYNAME%", myGuilds.getDisplayName()));
									this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
								}
								return;
							} catch (GuildAlreadyJoinedException e) {
								this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
								this.plugin.info(player, langUtils.getString("Commands.Join.AlreadyJoined"));
								this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
								return;
							}
						}

						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Accept.NotSent"));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}

					if (!player.getUniqueId().equals(request.getGuild().getOwner())) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Accept.NotOwner"));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}

					List<UUID> mList = request.getGuild().getMembers();

					try {
						request.getGuild().addMember(request.getUniqueId());
					} catch (GuildAlreadyJoinedException e) {
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(player, langUtils.getString("Commands.Accept.AlreadyJoined").replace("%DISPLAYNAME%", request.getGuildUtils().getDisplayName()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						return;
					}

					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					this.plugin.info(player, langUtils.getString("Commands.Join.Joined").replace("%NAME%", request.getGuild().getName()));
					this.plugin.info(player, langUtils.getString(Messages.HYPHEN));

					for (UUID uniqueId : mList) {
						LanguageUtils targetLangUtils = this.plugin.getLanguageManager().getPlayer(uniqueId);
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
						this.plugin.info(uniqueId, targetLangUtils.getString("Commands.Join.OtherJoined").replace("%DISPLAYNAME%", request.getGuildUtils().getDisplayName()));
						this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
					}
					return;
				}

				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				this.plugin.info(player, langUtils.getString("Commands.Accept.Usage"));
				this.plugin.info(player, langUtils.getString(Messages.HYPHEN));
				return;
			}
		}
	}

}
