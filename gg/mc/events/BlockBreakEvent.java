package gg.mc.events;

import gg.mc.Player;
import gg.mc.Position;

public class BlockBreakEvent extends Event {

	Player p;
	Position pos;
	Byte b1;
	Byte b2;
	
	public BlockBreakEvent(Player p, Position pos, Byte b1, Byte b2) {
		this.p = p;
		this.pos = pos;
		this.b1 = b1;
		this.b2 = b2;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Position getPosition() {
		return pos;
	}
	
	public Byte getOldBlock() {
		return b1;
	}
	
	public Byte getBlockBroken() {
		return b2;
	}
}
