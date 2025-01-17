package com.worldbiomusic.minigameworld.customevents.player;

import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.customevents.minigame.MinigGameEvent;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Playing minigame player event
 *
 */
public abstract class MiniGamePlayerEvent extends MinigGameEvent {
	private Player player;

	public MiniGamePlayerEvent(MiniGame minigame, Player player) {
		super(minigame);
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

}
