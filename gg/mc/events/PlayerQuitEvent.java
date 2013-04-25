package gg.mc.events;

import gg.mc.Player;
import gg.mc.exceptions.CannotCancelEventException;

public class PlayerQuitEvent extends Event {

	private Player player;
	
	public PlayerQuitEvent(Player player) {
		this.player = player;
	}
	
	@Override
	public void setCancelled(boolean b) {
		throw new CannotCancelEventException();
	}
	
	public Player getPlayer() {
		return player;
	}
}
