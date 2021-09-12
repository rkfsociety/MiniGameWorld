package com.wbm.minigamemaker.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.minigamemaker.minigameframes.SoloMiniGame;

public class FitTool extends SoloMiniGame {
	/*
	 * 설명: 제한 시간내에 알맞는 도구를 이용해서 많은 블럭을 캐는 미니게임 타입: Solo
	 */

	List<Material> blocks;

	public FitTool() {
		super("FitTool", 30, 10);
		this.getSetting().setScoreNotifying(true);
	}

	@Override
	protected void initGameSetting() {
		this.blocks = new ArrayList<Material>();
		// 칼 관련
		this.blocks.add(Material.COBWEB);
		// 도끼 관련
		this.blocks.add(Material.OAK_WOOD);
		// 곡괭이 관련
		this.blocks.add(Material.COBBLESTONE);
		// 삽 관련
		this.blocks.add(Material.DIRT);
	}

	private Material getRandomBlock() {
		int r = (int) (Math.random() * this.blocks.size());
		return this.blocks.get(r);
	}

	@Override
	public void processEvent(Event event) {
		if (event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			Player p = e.getPlayer();

			Block b = e.getBlock();

			// 밑의 4개 블럭일 경우에만 점수 +1
			if (this.blocks.contains(b.getType())) {
				e.setCancelled(true);
				this.plusScore(p, 1);
			}

			// 랜덤블럭으로 설정
			b.setType(this.getRandomBlock());
		}
	}

	@Override
	protected void runTaskAfterStart() {
		// 4가지 도구 지급
		for (Player p : this.getPlayers()) {
			p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
			p.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
			p.getInventory().addItem(new ItemStack(Material.IRON_AXE));
			p.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
		}
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Break block: +1");
		return tutorial;
	}

}