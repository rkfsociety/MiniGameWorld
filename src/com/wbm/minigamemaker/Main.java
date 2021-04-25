package com.wbm.minigamemaker;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.games.FitTool;
import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameDataManager;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.json.JsonDataManager;

public class Main extends JavaPlugin {
	private static Main main;
	MiniGameManager minigameManager;
	MiniGameDataManager minigameDataM;
	JsonDataManager jsonDataM;
	
	CommonEventListener commonLis;

	public static Main getInstance() {
		return main;
	}

	@Override
	public void onEnable() {
		main = this;
		BroadcastTool.info(ChatColor.GREEN + "MiniGameMaker ON");

		this.minigameDataM = new MiniGameDataManager();
		this.minigameManager = MiniGameManager.getInstance();
		this.minigameManager.setMiniGameDataManager(this.minigameDataM);

		// setup data
		this.setupData();

		this.commonLis= new CommonEventListener(this.minigameManager);
		getServer().getPluginManager().registerEvents(this.commonLis, this);

		// 예시 미니게임
		this.minigameManager.registerMiniGame(new FitTool());

	}

	void setupData() {
		this.jsonDataM = new JsonDataManager(this.getDataFolder());
		this.jsonDataM.registerMember(this.minigameDataM);
		this.jsonDataM.registerMember(this.minigameManager);

		this.jsonDataM.distributeAllData();
	}

	@Override
	public void onDisable() {

		this.jsonDataM.saveAllData();
		BroadcastTool.warn(ChatColor.RED + "MiniGameMaker OFF");
	}
}
