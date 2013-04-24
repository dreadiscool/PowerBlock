package gg.mc;

public class Position {

	private short posX;
	private short posY;
	private short posZ;
	private byte yaw;
	private byte pitch;
	
	public Position(short posX, short posY, short posZ, byte yaw, byte pitch) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public void add(short x, short y, short z) {
		posX += x;
		posY += y;
		posZ += z;
	}
	
	public void setOrientation(byte yaw, byte pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public short getX() {
		return posX;
	}
	
	public short getY() {
		return posY;
	}
	
	public short getZ() {
		return posZ;
	}
	
	public byte getYaw() {
		return yaw;
	}
	
	public byte getPitch() {
		return pitch;
	}
}
