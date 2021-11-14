package com.worldbiomusic.minigameworld.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.managers.menu.MiniGameMenuManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameCommand implements CommandExecutor {

	private MiniGameManager minigameManager;
	private MiniGameCommandTabCompleter tabCompleter;

	private MiniGamePartyCommand miniGamePartyCommand;
	private MiniGameSettingsConfigCommand miniGameSettingsConfigCommand;
	private MiniGameMinigamesConfigCommand miniGameMinigamesConfigCommand;
	private MiniGameHelpCommand minigameHelpCommand;

	public MiniGameCommand(MiniGameManager minigameM) {
		this.minigameManager = minigameM;

		this.miniGamePartyCommand = new MiniGamePartyCommand(this.minigameManager.getPartyManager());
		this.miniGameSettingsConfigCommand = new MiniGameSettingsConfigCommand(this.minigameManager);
		this.miniGameMinigamesConfigCommand = new MiniGameMinigamesConfigCommand(this.minigameManager);
		this.minigameHelpCommand = new MiniGameHelpCommand();

		// set tab completer
		this.tabCompleter = new MiniGameCommandTabCompleter(minigameM);
		MiniGameWorldMain.getInstance().getCommand("minigame").setTabCompleter(this.tabCompleter);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// only player
		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;

		try {
			// menu
			String menu = args[0];

			switch (menu) {
			case "join":
				return this.join(p, args);
			case "leave":
				return this.leave(p, args);
			case "list":
				return this.list(p, args);
			case "menu":
				return this.menu(p, args);
			case "reload":
				return this.reloadConfig(p, args);
			case "party":
				return this.miniGamePartyCommand.party(p, args);
			case "settings":
				return this.miniGameSettingsConfigCommand.settings(p, args);
			case "minigames":
				return this.miniGameMinigamesConfigCommand.minigames(p, args);
			}
		} catch (Exception e) {
			if (Setting.DEBUG_MODE) {
				e.printStackTrace();
			}

			// print usage
			Utils.debug("help");
			this.minigameHelpCommand.printHelp(p, args);
		}

		return true;
	}

	private boolean join(Player p, String[] args) throws Exception {
		/*
		 * minigame join <title>
		 */
		// check permission
		if (!Utils.checkPerm(p, "play.join")) {
			return true;
		}

		String title = args[1];
		this.minigameManager.joinGame(p, title);
		return true;
	}

	private boolean leave(Player p, String[] args) throws Exception {
		/*
		 * minigame leave
		 */

		// check permission
		if (!Utils.checkPerm(p, "play.leave")) {
			return true;
		}

		this.minigameManager.leaveGame(p);
		return true;
	}

	private boolean list(Player p, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(p, "play.list")) {
			return true;
		}

		List<MiniGame> games = this.minigameManager.getMiniGameList();

		// info
		String info = "\n" + ChatColor.BOLD + "[MiniGame List]";
		info += "\n" + "※ " + ChatColor.RED + "RED" + ChatColor.WHITE + ": already started";
		info += "\n" + "※ " + ChatColor.GREEN + "GREEN" + ChatColor.WHITE + ": can join";
		info += "\n" + "※ " + ChatColor.STRIKETHROUGH + "STRIKETHROUGH" + ChatColor.WHITE + ": inactive";
		Utils.sendMsg(p, info);

		// print mingames
		for (MiniGame game : games) {
			String gameTitle = game.getTitle();
			if (!game.isActive()) {
				Utils.sendMsg(p, "- " + ChatColor.STRIKETHROUGH + gameTitle);
			} else if (game.isStarted()) {
				Utils.sendMsg(p, "- " + ChatColor.RED + gameTitle);
			} else if (!game.isStarted()) {
				Utils.sendMsg(p, "- " + ChatColor.GREEN + gameTitle);
			}
		}

		// test
		p.setHealthScale(30);

		return true;
	}

	private boolean menu(Player p, String[] args) {
		// check permission
		if (!Utils.checkPerm(p, "menu")) {
			return true;
		}

		MiniGameMenuManager menuManager = this.minigameManager.getMiniGameMenuManager();
		menuManager.openMenu(p);
		return true;
	}

	private boolean reloadConfig(Player p, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(p, "config.reload")) {
			return true;
		}

		// reload "setting.yml", "minigames.yml"
		this.minigameManager.reload();

		p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "[ Reload Complete] ");
		p.sendMessage("- " + this.minigameManager.getFileName());
		this.minigameManager.getMiniGameList().forEach(m -> {
			p.sendMessage("- " + m.getTitleWithClassName());
		});
		return true;
	}
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
