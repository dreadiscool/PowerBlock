package gg.mc.network.packets;

import java.io.ByteArrayOutputStream;

import gg.mc.exceptions.InvalidChunkException;

public class Packet3Chunk extends Packet {

	public Packet3Chunk(byte[] data, byte percent) {
		super((byte) 0x03);
		if (data.length > 1024) {
			throw new InvalidChunkException();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(data);
			if (data.length < 1024) {
				byte[] padding = new byte[1024 - data.length];
				for (int i = 0; i < padding.length; i++) {
					padding[i] = 0;
				}
				bos.write(padding);
			}
			bos.write(percent);
		}
		catch (Exception ex) { /* Should never happen */ }
		this.setPayload(bos.toByteArray());
	}
}
