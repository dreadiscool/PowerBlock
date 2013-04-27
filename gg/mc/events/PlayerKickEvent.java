package gg.mc.events;

import gg.mc.Player;

public class PlayerKickEvent extends Event {

	public static enum Reason {
		BANNED,
		SERVER_FULL,
		WHITELIST,
		LOST_CONNECTION,
		PLUGIN_KICK
	}
	
	private String kicker;
	private Player player;
	private String reason;
	
	public PlayerKickEvent(String kicker, Player player, String reason) {
		this.kicker = kicker;
		this.player = player;
		this.reason = reason;
	}
	
	public String getKicker() {
		return kicker;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
}
