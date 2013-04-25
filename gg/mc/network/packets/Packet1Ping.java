package gg.mc.network.packets;

public class Packet1Ping extends Packet {

	public Packet1Ping() {
		super((byte) 0x01);
	}
	
	@Override
	public byte[] getBytes() {
		return new byte[] {
			this.header
		} ;
	}
}
