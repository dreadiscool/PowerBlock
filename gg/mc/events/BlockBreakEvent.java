package gg.mc.events;

import gg.mc.Player;
import gg.mc.Position;

public class BlockBreakEvent extends Event {

	private Player p;
	private Position pos;
	private short x;
	private short y;
	private short z;
	private byte b1;
	private byte b2;
	
	public BlockBreakEvent(Player p, short x, short y, short z, byte broken, byte hand) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.z = z;
		this.b1 = broken;
		this.b2 = hand;
		this.pos = new Position(x, y, z, (byte) 0, (byte) 0);
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Position getPosition() {
		return pos;
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
	
	public byte getBlockBroken() {
		return b1;
	}
	
	public byte getBlockInHand() {
		return b2;
	}
}
