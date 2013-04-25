package gg.mc;

public class World {

	private String name;
	private short length;
	private short depth;
	private short height;
	private byte[] data;
	
	public World(String name, short length, short depth, short height) {
		if (length < 32) {
			length = 32;
		}
		if (depth < 32) {
			depth = 32;
		}
		if (height < 32) {
			height = 32;
		}
		this.name = name;
		this.length = length;
		this.depth = depth;
		this.height = height;
		data = new byte[length * depth * height];
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
	}
	
	private int getDataPosition(short x, short y, short z) {
		return x + (z * depth) + (y * depth * height);
	}
	
	public byte getBlockAt(short x, short y, short z) {
		return data[getDataPosition(x, y, z)];
	}
	
	public void setBlockAt(short x, short y, short z, byte block) {
		data[getDataPosition(x, y, z)] = block;
	}
	
	public byte getBlockAt(Position p) {
		return getBlockAt(p.getX(), p.getY(), p.getZ());
	}
	
	public void setBlockAt(Position p, byte block) {
		setBlockAt(p.getX(), p.getY(), p.getZ(), block);
	}
	
	public String getName() {
		return name;
	}
	
	public short getLength() {
		return length;
	}
	
	public short getDepth() {
		return depth;
	}
	
	public short getHeight() {
		return height;
	}
	
	public byte[] getWorldData() {
		return data;
	}
}
