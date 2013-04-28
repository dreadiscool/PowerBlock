package gg.mc.events;

import gg.mc.Player;
import gg.mc.Position;

public class PlayerMoveEvent extends Event {

	private Player player;
	private Position oldPos;
	private Position newPos;
	
	public PlayerMoveEvent(Player player, Position oldPos, short x, short y, short z, byte yaw, byte pitch) {
		this.player = player;
		this.oldPos = oldPos;
		this.newPos = new Position(x, y, z, yaw, pitch);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Position getOldPosition() {
		return oldPos;
	}
	
	public Position getNewPosition() {
		return newPos;
	}
}
