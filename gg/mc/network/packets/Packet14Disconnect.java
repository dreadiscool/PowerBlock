package gg.mc.network.packets;

public class Packet14Disconnect extends Packet {

	private String kickReason;
	
	public Packet14Disconnect(String kickReason) {
		super((byte) 0x0e, Packet.getBytes(kickReason));
		this.kickReason = kickReason;
	}
	
	public String getKickReason() {
		return kickReason;
	}
}
