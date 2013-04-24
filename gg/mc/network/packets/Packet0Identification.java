package gg.mc.network.packets;

public class Packet0Identification extends Packet {

	private byte version;
	private String username;
	private String verification;
	
	/**
	 * Used for constructing OUTOING packets only
	 * @param version The current protocol version
	 * @param serverName The name of the server
	 * @param motd The server message of the day
	 * @param userType The type of user: 0x64: op, 0x00: regular
	 */
	public Packet0Identification(byte version, String serverName, String motd, byte userType) {
		super((byte) 0x00);
		byte[] buff1 = Packet.getBytes(serverName);
		byte[] buff2 = Packet.getBytes(motd);
		this.payload = new byte[129];
		for (int i = 0; i < 64; i++) {
			this.payload[i] = buff1[i];
		}
		for (int i = 64; i < 128; i++) {
			this.payload[i] = buff2[i - 64];
		}
		this.payload[128] = userType;
	}
	
	public Packet0Identification(byte[] payload) {
		super((byte) 0x00, payload);
		byte[] user = new byte[64];
		byte[] ver = new byte[64];
		for (int i = 0; i < 64; i++) {
			user[i] = payload[i];
		}
		for (int i = 64; i < 128; i++) {
			ver[i - 64] = payload[i];
		}
		this.version = payload[129];
		this.username = Packet.getString(user);
		this.verification = Packet.getString(ver);
	}
	
	public byte getProtocolVersion() {
		return version;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getVerificationKey() {
		return verification;
	}
}
