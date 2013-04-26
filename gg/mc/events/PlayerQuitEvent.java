package gg.mc.events;

import gg.mc.ChatColor;
import gg.mc.Player;
import gg.mc.exceptions.CannotCancelEventException;

public class PlayerQuitEvent extends Event {

	private Player player;
	private String quitMessage;
	
	public PlayerQuitEvent(Player player) {
		this.player = player;
		this.quitMessage = ChatColor.YELLOW + player.getUsername() + " disconnected :(";
	}
	
	@Override
	public void setCancelled(boolean b) {
		throw new CannotCancelEventException();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getQuitMessage() {
		return quitMessage;
	}
	
	public void setQuitMessage(String message) {
		quitMessage = message;
		if (quitMessage == "") {
			quitMessage = null;
		}
	}
}
