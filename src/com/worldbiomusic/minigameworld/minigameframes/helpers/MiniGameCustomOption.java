package com.worldbiomusic.minigameworld.minigameframes.helpers;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Below custom options are created in `custom-data` section by default<br>
 * Must serialize/deserialize value of each options in get(), set() method<br>
 */
public class MiniGameCustomOption {

	public enum Option {
		/**
		 * Init: true<br>
		 * Description: Chatting in minigame with players
		 */
		CHAT("chat"),
		/**
		 * Init: true<br>
		 * Description: Notify changed score
		 */
		SCORE_NOTIFYING("score-notifying"),
		/**
		 * Init: false<br>
		 * Description: Can break block if true
		 */
		BLOCK_BREAK("block-break"),
		/**
		 * Init: false<br>
		 * Description: Can place block if true
		 */
		BLOCK_PLACE("block-place"),
		/**
		 * Init: false<br>
		 * Description: Players can damage each other if true (contains damage by
		 * projectile)
		 */
		PVP("pvp"),
		/**
		 * Init: true<br>
		 * Description: Players and Mobs can damage each other if true (contains damage
		 * by projectile)
		 */
		PVE("pve"),
		/**
		 * Init: true<br>
		 * Description: Not drops items when die if true
		 */
		INVENTORY_SAVE("inventory-save"),
		/**
		 * Init: true<br>
		 * Description: Will Respawn in location of minigame if true
		 */
		MINIGAME_RESPAWN("minigame-respawn"),
		/**
		 * Init: Survival<br>
		 * Description: GameMode when a player join minigame (used in
		 * {@link MiniGamePlayerData#setLive(boolean)},
		 * {@link MiniGamePlayerState#makePureState()})
		 */
		LIVE_GAMEMODE("live-gamemode"),
		/**
		 * Init: Spectator<br>
		 * Description: GameMode when a player die (used in
		 * {@link MiniGamePlayerData#setLive(boolean)})
		 */
		DEAD_GAMEMODE("dead-gamemode"),
		/**
		 * Init: ChatColor.RESET<br>
		 * Description: MiniGame personal color
		 */
		COLOR("color"),

		/**
		 * Init: true<br>
		 * Description: Whether a player's food level changes
		 */
		FOOD_LEVEL_CHANGE("food-level-change"),

		/**
		 * Init: true<br>
		 * Description: Whether a player damaged by something
		 */
		PLAYER_HURT("player-hurt");

		private String keyString;

		private Option(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}

	private MiniGame minigame;

	public MiniGameCustomOption(MiniGame minigame) {
		this.minigame = minigame;

		// init custom options
		this.set(Option.CHAT, true);
		this.set(Option.SCORE_NOTIFYING, true);
		this.set(Option.BLOCK_BREAK, false);
		this.set(Option.BLOCK_PLACE, false);
		this.set(Option.PVP, false);
		this.set(Option.PVE, true);
		this.set(Option.INVENTORY_SAVE, true);
		this.set(Option.MINIGAME_RESPAWN, true);
		// used in MiniGamePlayerData, MiniGamePlayerState(makePureState())
		this.set(Option.LIVE_GAMEMODE, GameMode.SURVIVAL);
		// used in MiniGamePlayerData
		this.set(Option.DEAD_GAMEMODE, GameMode.SPECTATOR);
		this.set(Option.COLOR, ChatColor.RESET);
		this.set(Option.FOOD_LEVEL_CHANGE, true);
		this.set(Option.PLAYER_HURT, true);
	}

	private void setOptionData(String option, Object data) {
		this.minigame.getCustomData().put(option, data);
	}

	private Object getOptionData(String option) {
		return this.minigame.getCustomData().get(option);
	}

	public void set(Option option, Object value) {
		// Serialize enum type to String
		if (value instanceof Enum) {
			value = ((Enum<?>) value).name();
		}
		this.setOptionData(option.getKeyString(), value);
	}

	public Object get(Option option) {
		Object object = this.getOptionData(option.getKeyString());

		// Deserialize string with fit type
		switch (option) {
		case LIVE_GAMEMODE:
		case DEAD_GAMEMODE:
			return GameMode.valueOf((String) object);
		case COLOR:
			return ChatColor.valueOf((String) object);
		default:
			return object;
		}
	}

	/**
	 * Process event related with custom option before pass to a minigame<br>
	 * 
	 * @param event Event to set cancel or not with options
	 */
	public void processEvent(Event event) {
		if (event instanceof AsyncPlayerChatEvent) {
			((AsyncPlayerChatEvent) event).setCancelled(!(boolean) get(Option.CHAT));
		} else if (event instanceof BlockBreakEvent) {
			((BlockBreakEvent) event).setCancelled(!(boolean) this.get(Option.BLOCK_BREAK));
		} else if (event instanceof BlockPlaceEvent) {
			((BlockPlaceEvent) event).setCancelled(!(boolean) this.get(Option.BLOCK_PLACE));
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			if ((boolean) this.get(Option.INVENTORY_SAVE)) {
				// keep inv
				e.setKeepInventory(true);

				// remove drops
				e.getDrops().clear();
			} else {
				e.setKeepInventory(false);
			}
		} else if (event instanceof PlayerRespawnEvent) {
			if ((boolean) this.get(Option.MINIGAME_RESPAWN)) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				e.setRespawnLocation(this.minigame.getLocation());
			}
		} else if (event instanceof FoodLevelChangeEvent) {
			FoodLevelChangeEvent e = (FoodLevelChangeEvent) event;
			e.setCancelled(!(boolean) get(Option.FOOD_LEVEL_CHANGE));
		} else if (event instanceof EntityDamageEvent) {
			/*
			 * PLAYER_HURT
			 */
			EntityDamageEvent damageEvent = (EntityDamageEvent) event;
			if (damageEvent.getEntity() instanceof Player) {
				damageEvent.setCancelled(!(boolean) get(Option.PLAYER_HURT));
			}

			if (event instanceof EntityDamageByEntityEvent) {
				// cancel damage by entity (
				// when victim == minigame player && damager == minigame player
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

				// PVP
				processPVP(e);

				// PVE
				processPVE(e);
			}

		}
	}

	private void processPVP(EntityDamageByEntityEvent e) {
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		if (victim instanceof Player && this.minigame.containsPlayer((Player) victim)) {
			// direct damage
			if (damager instanceof Player && this.minigame.containsPlayer((Player) damager)) {
				e.setCancelled(!(boolean) this.get(Option.PVP));
			}

			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();
				if (shooter instanceof Player && this.minigame.containsPlayer((Player) shooter)) {
					e.setCancelled(!(boolean) this.get(Option.PVP));
				}
			}
		}
	}

	private void processPVE(EntityDamageByEntityEvent e) {
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		if (victim instanceof Mob) {
			// direct damage
			if (damager instanceof Player) {
				e.setCancelled(!(boolean) this.get(Option.PVE));
			}

			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();
				if (shooter instanceof Player) {
					e.setCancelled(!(boolean) this.get(Option.PVE));
				}
			}
		} else if (victim instanceof Player) {
			// direct damage
			if (damager instanceof Mob) {
				e.setCancelled(!(boolean) this.get(Option.PVE));
			}

			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();
				if (shooter instanceof Mob) {
					e.setCancelled(!(boolean) this.get(Option.PVE));
				}
			}
		}
	}

}
