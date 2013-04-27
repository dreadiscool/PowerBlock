package gg.mc;

import gg.mc.network.packets.Packet;
import gg.mc.network.packets.Packet6SetBlock;

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
		for (int z = 0; z < depth; z++) {
			for (int x = 0; x < length; x++) {
				setBlockAt(x, 1, z, Block.Dirt);
				setBlockAt(x, 2, z, Block.Grass);
			}
		}
	}
	
	public void broadcastWorldPacket(Packet packet) {
		Player[] players = PowerBlock.getServer().getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			if (this.equals(players[i].getWorld())) {
				players[i].push(packet);
			}
		}
	}
	
	private int getDataPosition(short x, short y, short z) {
		return (z * this.height + y) * this.length + x;
	}
	
	public byte getBlockAt(short x, short y, short z) {
		return data[getDataPosition(x, y, z)];
	}
	
	public void setBlockAt(int x, int y, int z, byte block) {
		setBlockAt((short) x, (short) y, (short) z, block);
	}
	
	public void setBlockAt(short x, short y, short z, byte block) {
		data[getDataPosition(x, y, z)] = block;
		Packet6SetBlock update = new Packet6SetBlock(x, y, z, block);
		broadcastWorldPacket(update);
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
