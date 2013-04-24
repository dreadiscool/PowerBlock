package gg.mc.network.packets;

public class Packet4Finalize extends Packet {

	public Packet4Finalize(short width, short height, short depth) {
		super((byte) 0x04);
		byte[] w = Packet.getBytes(width);
		byte[] h = Packet.getBytes(height);
		byte[] d = Packet.getBytes(depth);
		this.payload = new byte[6];
		for (int i = 0; i < 2; i++) {
			this.payload[i] = w[i];
		}
		for (int i = 2; i < 4; i++) {
			this.payload[i] = h[i - 2];
		}
		for (int i = 4; i < 6; i++) {
			this.payload[i] = d[i - 4];
		}
	}
}
