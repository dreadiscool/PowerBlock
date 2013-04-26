package gg.mc.events;

import gg.mc.ChatColor;
import gg.mc.Player;

public class PlayerLoginEvent extends Event {

	private Player player;
	private String joinMessage;
	
	public PlayerLoginEvent(Player player) {
		this.player = player;
		this.joinMessage = ChatColor.YELLOW + player.getUsername() + " has connected!";
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getJoinMessage() {
		return joinMessage;
	}
	
	public void setJoinMessage(String message) {
		joinMessage = message;
		if (joinMessage == "") {
			joinMessage = null;
		}
	}
}
