package gg.mc.network.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Packet7SpawnPlayer extends Packet {

	public Packet7SpawnPlayer(byte eid, String name, short x, short y, short z, byte yaw, byte pitch) {
		super((byte) 0x07);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(eid);
			dos.write(Packet.getBytes(name));
			dos.write(Packet.getBytes(x));
			dos.write(Packet.getBytes(y));
			dos.write(Packet.getBytes(z));
			dos.writeByte(yaw);
			dos.writeByte(pitch);
		}
		catch (Exception ex) { /* Can never happen */ }
		this.setPayload(bos.toByteArray());
	}
}
