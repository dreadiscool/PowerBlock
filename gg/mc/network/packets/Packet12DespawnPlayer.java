package gg.mc.network.packets;

public class Packet12DespawnPlayer extends Packet {

	public Packet12DespawnPlayer(byte eid) {
		super((byte) 0x0c, new byte[] { eid });
	}
}
