package me.dreadiscool;

import java.io.File;
import java.util.ArrayList;

import me.dreadiscool.wrapper.BlockType;
import me.dreadiscool.wrapper.Point3D;

public class PowerWorld {

	private String worldName;
	private byte[] levelData;
	private int length;
	private int depth;
	private int height;
	private ArrayList<String> players;
	
	public PowerWorld(String worldName) {
		this.worldName = worldName;
		this.players = new ArrayList<String>();
		File f = new File(System.getProperty("user.dir") + File.separator + "worlds" + File.separator + worldName + ".pb");
		if (f.exists() && f.isFile())
			load();
		else {
			generateWorld();
			save();
		}
	}
	
	public void setBlockAt(byte blockType, int x, int y, int z) {
		if (x > length || y > height || z > depth)
			return;
		levelData[posToInt(x, y, z)] = blockType;
	}
	
	public int posToInt(int x, int y, int z) {
		return y * (length * depth) + (z * depth) + x;
	}
	
	public Point3D intToPos(int i) {
		double x;
		double y;
		double z;
		int leftOver = i % (length * depth);
		y = Double.valueOf((i - leftOver) / (length * depth));
		int leftOver2 = leftOver % length;
		z = Double.valueOf((leftOver - leftOver2) / length);
		x = Double.valueOf(leftOver - (z * length));
		return new Point3D(x, y, z);
	}
	
	public void generateWorld() {
		int totalSize = length * depth * height;
		int halfSize = (int) Math.floor(totalSize / 2);
		for (int i = 0; i < totalSize; i++) {
			if (i <= halfSize)
				levelData[i] = BlockType.DIRT;
			else if (i <= (halfSize + (length * depth)))
				levelData[i] = BlockType.GRASS;
			else
				levelData[i] = BlockType.AIR;
		}
	}
	
	public void load() {
		
	}
	
	public void save() {
		
	}
	
	public String getWorldName() { return worldName; }
	public int getLength() { return length; }
	public int getDepth() { return depth; }
	public int getHeight() { return height; }
}
