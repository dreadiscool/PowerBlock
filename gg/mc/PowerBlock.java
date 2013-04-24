package gg.mc;

import java.io.File;

import gg.mc.exceptions.ServerRunningException;
import gg.mc.heartbeat.HeartbeatThread;
import gg.mc.network.ConnectionThread;

public class PowerBlock {

	private static PowerBlock instance;
	
	public static PowerBlock getServer() {
		return instance;
	}
	
	public static void main(String[] args) throws ServerRunningException {
		if (instance != null) {
			throw new ServerRunningException();
		}
		File dirWorlds = new File(System.getProperty("user.dir") + File.separator + "worlds");
		File dirPlugins = new File(System.getProperty("user.dir") + File.separator + "plugins");
		if (!dirWorlds.exists()) {
			System.out.println("Creating directory /worlds");
			dirWorlds.mkdir();
		}
		if (!dirPlugins.exists()) {
			System.out.println("Creating directory /plugins");
			dirPlugins.mkdir();
		}
		instance = new PowerBlock();
		instance.startServer();
	}
	
	private Thread connectionThread = new ConnectionThread();
	private Thread serverThread = new ServerThread((ConnectionThread) connectionThread);
	private Thread heartbeatThread = new HeartbeatThread((ConnectionThread) connectionThread);
	private Configuration configuration = new Configuration();
	private WorldManager worldManager;
	
	private void startServer() {
		connectionThread.start();
		serverThread.start();
		heartbeatThread.start();
		try {
			worldManager = new WorldManager();
		}
		catch (ServerRunningException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		if (worldManager.getTotalWorlds() == 0) {
			System.out.println("Generating default world...");
			worldManager.createWorld("world", 64, 64, 64);
		}
	}
	
	public void broadcastMessage(String message) {
		Player[] players = getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			players[i].sendMessage(message);
		}
		System.out.println("[Chat] " + message);
	}
	
	public void stop() {
		connectionThread.interrupt();
		serverThread.interrupt();
		heartbeatThread.interrupt();
		while (connectionThread.isAlive()) { }
		System.out.println("Connection thread shut down");
		while (serverThread.isAlive()) { }
		System.out.println("Event thread shut down");
		while (heartbeatThread.isAlive()) { }
		System.out.println("Heartbeat thread shut down");
	}
	
	public Player getOnlinePlayer(String name) {
		return ((ConnectionThread) connectionThread).getPlayer(name);
	}
	
	public Player[] getOnlinePlayers() {
		return ((ConnectionThread) connectionThread).getOnlinePlayers();
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public WorldManager getWorldManager() {
		return worldManager;
	}
}
