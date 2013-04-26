package gg.mc.network.packets;

public class Packet {
	
	protected static byte[] getBytes(short s) {
		return new byte[] {
			(byte) ((s >> 8) & 0xff),
			(byte) (s & 0xff),
		};
	}
	
	protected static byte[] getBytes(String s) {
		char[] chars = s.toCharArray();
		byte[] buff = new byte[64];
		for (int i = 0; i < 64; i++) {
			if (i < chars.length) {
				buff[i] = (byte) chars[i];
			}
			else {
				buff[i] = (byte) 0x00;
			}
		}
		return buff;
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
