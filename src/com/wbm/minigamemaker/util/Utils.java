package com.wbm.minigamemaker.util;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.wbm.minigamemaker.MiniGameMakerMain;

public class Utils {
	public static String messagePrefix = "MiniGameMaker";
	static MiniGameMakerMain main = MiniGameMakerMain.getInstance();
	static Logger logger = main.getLogger();

	private static String getMessagePrefixString() {
		return "[" + messagePrefix + "] ";
	}

	public static void sendMsg(Player p, String msg) {
		p.sendMessage(getMessagePrefixString() + msg);
	}

	public static void info(String msg) {
		logger.info(msg);
	}

	public static void warning(String msg) {
		logger.warning(msg);
	}

	public static void debug(String msg) {
		info(ChatColor.RED + "[DEBUG] " + msg);
	}

	@SuppressWarnings("deprecation")
	public static void broadcast(String msg) {
		Bukkit.broadcastMessage(getMessagePrefixString() + msg);
	}
}