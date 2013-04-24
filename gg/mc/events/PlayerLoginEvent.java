package gg.mc.events;

import gg.mc.Player;

public class PlayerLoginEvent extends Event {

	private Player player;
	
	public PlayerLoginEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}
