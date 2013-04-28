package gg.mc;

import gg.mc.exceptions.NoAvailableEIDException;
import gg.mc.network.packets.Packet;
import gg.mc.network.packets.Packet12DespawnPlayer;
import gg.mc.network.packets.Packet6SetBlock;

public class World {

	private String name;
	private short length;
	private short depth;
	private short height;
	private byte[] data;
	private boolean[] availableEids = new boolean[256];
	
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
		for (int i = 0; i < (length * depth * height * 0.5); i++) {
			data[i] = Block.Grass;
		}
		for (int i = 0; i < availableEids.length; i++) {
			availableEids[i] = true;
		}
	}
	
	public void broadcastWorldPacket(Packet packet) {
		broadcastWorldPacket(packet, null);
	}
	
	public void broadcastWorldPacket(Packet packet, Player exclude) {
		Player[] players = PowerBlock.getServer().getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			if (players[i] != exclude && this.equals(players[i].getWorld())) {
				players[i].push(packet);
			}
		}
	}
	
	private int getDataPosition(short x, short y, short z) {
		return y * (length * depth) + (z * length) + x;
	}
	
	public byte requestEntityId() throws NoAvailableEIDException {
		for (int i = 0; i < availableEids.length; i++) {
			if (availableEids[i]) {
				availableEids[i] = false;
				return (byte) i;
			}
		}
		throw new NoAvailableEIDException(this);
	}
	
	public void reclaimEid(byte id) {
		broadcastWorldPacket(new Packet12DespawnPlayer(id));
		availableEids[(int) id] = true;
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
