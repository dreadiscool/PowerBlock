package gg.mc.network.packets;

public class Packet6BlockChange extends Packet {

	/**
	 * Can only go server -> client
	 */
	public Packet6BlockChange(short x, short y, short z, byte blockType) {
		super((byte) 0x06);
		this.payload = new byte[7];
		byte[] w = Packet.getBytes(x);
		byte[] h = Packet.getBytes(y);
		byte[] d = Packet.getBytes(z);
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
		this.payload[6] = blockType;
	}
}
