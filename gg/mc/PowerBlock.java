package gg.mc;

import java.io.File;
import java.util.Scanner;

import gg.mc.events.PlayerKickEvent.Reason;
import gg.mc.exceptions.ServerRunningException;
import gg.mc.heartbeat.HeartbeatThread;
import gg.mc.network.ConnectionThread;
import gg.mc.plugin.PluginManager;

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
		
		Scanner s = new Scanner(System.in);
		s.useDelimiter(System.getProperty("line.separator"));
		while (s.hasNext()) {
			((ServerThread) PowerBlock.getServer().serverThread).dispatchCommand(s.next());
		}
		s.close();
	}
	
	private Configuration configuration = new Configuration();
	private Thread connectionThread = new ConnectionThread();
	private Thread serverThread = new ServerThread((ConnectionThread) connectionThread);
	private Thread heartbeatThread = new HeartbeatThread((ConnectionThread) connectionThread);
	private WorldManager worldManager;
	private PluginManager pluginManager;
	private Scheduler scheduler;
	
	private void startServer() {
		connectionThread.start();
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
		pluginManager = new PluginManager();
		scheduler = new Scheduler();
		serverThread.start();
		heartbeatThread.start();
	}
	
	public void broadcastMessage(String message) {
		Player[] players = getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			players[i].sendMessage(message);
		}
		System.out.println("[Chat] " + message);
	}
	
	public void stop() {
		System.out.println("Server shutting down...");
		Player[] players = getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			players[i].kick("Server is shutting down!", Reason.LOST_CONNECTION);
		}
		pluginManager.unload();
		worldManager.saveAllWorlds();
		connectionThread.interrupt();
		serverThread.interrupt();
		heartbeatThread.interrupt();
		System.out.println("Server stopped!");
		System.exit(0);
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
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
}
