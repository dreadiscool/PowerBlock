package gg.mc.events;

import gg.mc.Player;
import gg.mc.Position;

public class BlockPlaceEvent extends Event {

	private Player player;
	private short x;
	private short y;
	private short z;
	private Position position;
	private byte block;
	
	public BlockPlaceEvent(Player player, short x, short y, short z, byte block) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
		this.position = new Position(x, y, z, (byte) 0, (byte) 0);
		this.block = block;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public short getXPos() {
		return x;
	}
	
	public short getYPos() {
		return y;
	}
	
	public short getZPos() {
		return z;
	}
	
	public byte getBlockPlaced() {
		return block;
	}
	
	public Position getPosition() {
		return position;
	}
}
