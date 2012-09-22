package me.dreadiscool;

import java.io.File;

public class PowerServer {

	private static PowerServer instance;
	private DataHolder dataHolder = new DataHolder();
	private PowerWorld mainWorld;
	
	public static void main(String[] args) {
		instance = new PowerServer(args);
	}
	
	public static PowerServer getPowerServerInstance() { return instance; }
	
	public PowerServer(String[] args) {
		dataHolder.load(System.getProperty("user.dir") + File.separator + "server.properties");
	}
	
	public void log(String msg) {
		System.out.println("[INFO] " + msg);
	}
	
	public PowerWorld getMainWorld() { return mainWorld; }
}
