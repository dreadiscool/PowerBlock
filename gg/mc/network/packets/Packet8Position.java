package gg.mc.network.packets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Packet8Position extends Packet {

	private byte entityId;
	private short xPos;
	private short yPos;
	private short zPos;
	private byte yaw;
	private byte pitch;
	
	public Packet8Position(byte[] payload) {
		super((byte) 0x08, payload);
		ByteArrayInputStream bis = new ByteArrayInputStream(payload);
		DataInputStream dis = new DataInputStream(bis);
		try {
			this.entityId = dis.readByte();
			this.xPos = dis.readShort();
			this.yPos = dis.readShort();
			this.zPos = dis.readShort();
			this.yaw = dis.readByte();
			this.pitch = dis.readByte();
		}
		catch (Exception ex) { /* Should never happen */ }
	}
	
	public Packet8Position(byte entityId, short xPos, short yPos, short zPos, byte yaw, byte pitch) {
		super((byte) 0x08);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(entityId);
			dos.writeShort(xPos);
			dos.writeShort(yPos);
			dos.writeShort(zPos);
			dos.writeByte(yaw);
			dos.writeByte(pitch);
		}
		catch (Exception ex) { /* Should never happen */ }
		this.setPayload(bos.toByteArray());
	}
	
	public byte getEntityId() {
		return entityId;
	}
	
	public short getXPos() {
		return xPos;
	}
	
	public short getYPos() {
		return yPos;
	}
	
	public short getZPos() {
		return zPos;
	}
	
	public byte getYaw() {
		return yaw;
	}
	
	public byte getPitch() {
		return pitch;
	}
}
