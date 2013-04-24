package gg.mc.network.packets;

public class Packet2Initialize extends Packet {

	public Packet2Initialize() {
		super((byte) 0x02);
	}
	
	@Override
	public byte[] getBytes() {
		return new byte[] {
			this.header
		} ;
	}
}
