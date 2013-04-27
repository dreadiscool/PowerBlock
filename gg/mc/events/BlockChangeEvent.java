package gg.mc.events;

import gg.mc.Position;

public class BlockChangeEvent extends Event{

	Byte b1; //Original block, before the block was changed;
	Byte b2; //New block, the block trying to be changed to;
	Position p; //Position of the block;
	
	public BlockChangeEvent(Position p, Byte b1, Byte b2) {
		this.b1 = b1;
		this.b2 = b2;
		this.p = p;
	}
	
	public Byte getOldBlock() {
		return b1;
	}
}
