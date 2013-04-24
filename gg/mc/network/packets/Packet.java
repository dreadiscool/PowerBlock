package gg.mc.network.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Packet {
	
	protected static byte[] getBytes(short s) {
		return new byte[] {
			(byte) (s & 0xff),
			(byte) ((s >> 8) & 0xff)
		};
	}
	
	protected static byte[] getBytes(String s) {
		StringBuilder sb = new StringBuilder(s);
		if (sb.length() > 64) {
			sb.setLength(64);
		}
		else {
			while (sb.length() < 64) {
				sb.append((byte) 0x00);
			}
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeChars(s);
		}
		catch (Exception ex) { /* Can never happen */ }
		return bos.toByteArray();
	}
	
	protected static String getString(byte[] payload) {
		return new String(payload).trim();
	}
	
	protected byte header;
	protected byte[] payload;
	
	public Packet(byte header) {
		this.header = header;
	}
	
	public Packet(byte header, byte[] payload) {
		this(header);
		setPayload(payload);
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public byte[] getPayload() {
		return this.payload;
	}
	
	public byte[] getBytes() {
		byte[] buff = new byte[this.payload.length + 1];
		buff[0] = this.header;
		for (int i = 0; i < this.payload.length; i++) {
			buff[i + 1] = this.payload[i];
		}
		return buff;
	}
	
	public byte getHeader() {
		return header;
	}
}
