package gg.mc.network.packets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Packet5UpdateBlock extends Packet {

	private short xPos;
	private short yPos;
	private short zPos;
	private byte mode;
	private byte blockType;
	
	public Packet5UpdateBlock(byte[] payload) {
		super((byte) 0x05, payload);
		ByteArrayInputStream bis = new ByteArrayInputStream(payload);
		DataInputStream dis = new DataInputStream(bis);
		try {
			this.xPos = dis.readShort();
			this.yPos = dis.readShort();
			this.zPos = dis.readShort();
			this.mode = dis.readByte();
			this.blockType = dis.readByte();
		}
		catch (Exception ex) { /* Should never happen */ }
	}
	
	public short getXPos() {
		return xPos;
	}
	
	public short getYPos() {
		return yPos;
	}
	
	public short getZPos() {
		return zPos;
	}
	
	public byte getMode() {
		return mode;
	}
	
	public byte getBlockType() {
		return blockType;
	}
}
