package gg.mc.network.packets;

import java.io.ByteArrayOutputStream;

import gg.mc.exceptions.InvalidChunkException;

public class Packet3Chunk extends Packet {

	public Packet3Chunk(short length, byte[] data, byte percent) {
		super((byte) 0x03);
		if (data.length > 1024) {
			throw new InvalidChunkException();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(Packet.getBytes(length));
			bos.write(data);
			bos.write(percent);
		}
		catch (Exception ex) { /* Should never happen */ }
		this.setPayload(bos.toByteArray());
	}
}
