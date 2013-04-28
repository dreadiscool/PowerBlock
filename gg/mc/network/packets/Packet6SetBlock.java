package gg.mc.network.packets;

public class Packet6SetBlock extends Packet {
	
	public Packet6SetBlock(short x, short y, short z, byte block) {
		super((byte) 0x06);
		// Lets do this the long way, save mem. ideally, setblock will be called a lot. primitives are better
		this.payload = new byte[7];
		byte[] xpos = Packet.getBytes(x);
		byte[] ypos = Packet.getBytes(y);
		byte[] zpos = Packet.getBytes(z);
		System.arraycopy(xpos, 0, this.payload, 0, xpos.length);
		System.arraycopy(ypos, 0, this.payload, 2, ypos.length);
		System.arraycopy(zpos, 0, this.payload, 4, zpos.length);
		this.payload[6] = block;
	}
}
