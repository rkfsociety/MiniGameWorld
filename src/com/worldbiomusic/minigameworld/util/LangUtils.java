package com.worldbiomusic.minigameworld.util;

import java.io.File;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.ServerTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;

import me.smessie.MultiLanguage.bukkit.AdvancedMultiLanguageAPI;

public class LangUtils {
	public static void sendMsg(Player p, String messageKey) {
		sendMsg(p, messageKey, true);
	}

	public static void sendMsg(Player p, String messageKey, boolean prefix) {
		sendMsg(p, messageKey, prefix, null);
	}

	public static void sendMsg(Player p, String messageKey, String[][] replaces) {
		sendMsg(p, messageKey, true, replaces);
	}

	public static void sendMsg(Player p, String messageKey, boolean prefix, String[][] replaces) {
		String message = getMsg(p, messageKey, prefix, replaces);
		p.sendMessage(message);
	}

	public static void sendMsgToEveryone(String msg) {
		sendMsgToEveryone(msg, true);
	}

	public static void sendMsgToEveryone(String msg, boolean prefix) {
		sendMsgToEveryone(msg, prefix, null);
	}

	public static void sendMsgToEveryone(String msg, String[][] replaces) {
		sendMsgToEveryone(msg, true, replaces);
	}

	public static void sendMsgToEveryone(String msg, boolean prefix, String[][] replaces) {
		Bukkit.getOnlinePlayers().forEach(p -> sendMsg(p, msg, prefix, replaces));
	}

	public static String getMsg(Player p, String messageKey) {
		return getMsg(p, messageKey, true, null);
	}

	public static String getMsg(Player p, String messageKey, String[][] replaces) {
		return getMsg(p, messageKey, true, replaces);
	}

	public static String getMsg(Player p, String messageKey, boolean prefix) {
		return getMsg(p, messageKey, prefix, null);
	}

	public static String getMsg(Player p, String messageKey, boolean prefix, String[][] replaces) {
		String message = null;

		if (p == null) {
			return message;
		}

		// add default first key
		messageKey = "message." + messageKey;

		// default language
		String language = "EN";

		// if AdvancedMultiLanguage plugin is enabled, select player's language
		if (ServerTool.isPluginEnabled("AdvancedMultiLanguage")) {
			language = AdvancedMultiLanguageAPI.getLanguageOfUuid(p.getUniqueId().toString());
		}

		// get message
		message = getLangMessage(p, language, messageKey);

		// check message is null (cause of message key is not exist in anywhere)
		if (message == null) {
			return message;
		}

		// check prefix
		if (prefix) {
			message = Utils.getMessagePrefix() + message;
		}

		// replace placeholders
		message = replace(message, replaces);

		// replace custom placeholders
		message = replaceCustomPlaceholders(language, message);

		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * [Check list]<br>
	 * 1. className.msgKey<br>
	 * 2. common.msgKey<br>
	 * 3. className.msgKey in "EN.yml"<br>
	 * 4. common.msgKey in "EN.yml"<br>
	 * 
	 * @param p
	 * @param language
	 * @param messageKey
	 * @return
	 */
	private static String getLangMessage(Player p, String language, String messageKey) {
		String message = null;
		String[] keys = messageKey.split(".");
		String commonMsgKey = "message.common." + keys[keys.length - 1];
		try {
			// search in the language flie
			if (ServerTool.isPluginEnabled("AdvancedMultiLanguage")) {
				if (isKeyExist(language, messageKey)) {
					message = getMessage(language, messageKey);
				} else if (isKeyExist(language, commonMsgKey)) {
					message = getMessage(language, commonMsgKey);
				}
			}

			// search in the EN.yml
			if (message == null) {
				language = "EN";
				if (isKeyExist(language, messageKey)) {
					message = getMessage(language, messageKey);
				} else if (isKeyExist(language, commonMsgKey)) {
					message = getMessage(language, commonMsgKey);
				}
			}
		} catch (Exception e) {
			if (Setting.DEBUG_MODE) {
				e.printStackTrace();
			}
			return message;
		}
		return message;
	}

	private static boolean isKeyExist(String language, String msgKey) {
		return getLangYaml(language).contains(msgKey);
	}

	private static String getMessage(String language, String msgKey) {
		return getLangYaml(language).getString(msgKey);
	}

	private static YamlConfiguration getLangYaml(String language) {
		File file = new File(MiniGameWorldMain.getInstance().getDataFolder() + File.separator + "messages"
				+ File.separator + language + ".yml");
		return YamlConfiguration.loadConfiguration(file);
	}

	public static String replaceCustomPlaceholders(String language, String message) {
		YamlConfiguration yaml = getLangYaml(language);
		ConfigurationSection customSection = yaml.getConfigurationSection("message.custom");
		Set<String> keys = customSection.getKeys(true);
		for (String key : keys) {
			String value = customSection.getString(key);
			message = replace(message, key, value);
		}

		return message;
	}

	public static String replace(String message, String placeholder, String value) {
		if (message == null) {
			return message;
		}
		return message.replace("<" + placeholder + ">", value);
	}

	public static String replace(String message, String[][] replaces) {
		if (replaces == null) {
			return message;
		}

		for (int i = 0; i < replaces.length; i++) {
			String[] change = replaces[i];
			if (change.length < 2) {
				continue;
			}
			String placeholder = change[0];
			String value = change[1];
			message = replace(message, placeholder, value);
		}
		return message;
	}

	public static String path(Class<?> c) {
		return c.getSimpleName() + ".";
	}

	public static String path(Object o) {
		return path(o.getClass());
	}
}
