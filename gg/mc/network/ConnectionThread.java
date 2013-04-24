package gg.mc.network;

import gg.mc.Player;
import gg.mc.exceptions.NoSuchPlayerException;
import gg.mc.network.packets.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionThread extends Thread {

	private ServerSocket serverSocket;
	private ConcurrentHashMap<String, Player> clients = new ConcurrentHashMap<String, Player>();
	private ArrayList<Player> loginQueue = new ArrayList<Player>();
	private String salt;
	
	public ConnectionThread() {
		super("PowerBlock Connection Thread");
		try {
			serverSocket = new ServerSocket(25565);
			String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
			Random random = new Random();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 16; i++) {
				sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
			}
			this.salt = sb.toString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed to bind to port 25565 - Is it available?");
		}
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1);
				Socket s = null;
				try {
					s = serverSocket.accept();
					System.out.println(s.getRemoteSocketAddress().toString() + " has connected!");
					synchronized (loginQueue) {
						loginQueue.add(new Player(this, s));
					}
				}
				catch (IOException ex) {
					if (s != null) {
						System.out.println("Failed to handle connection from " + s.getRemoteSocketAddress().toString());
					}
				}
			}
		}
		catch (InterruptedException ex) {
			// Server is aborting
			try {
				// Exception inception
				serverSocket.close();
			}
			catch (IOException e) {
				System.out.println("Socket was already closed, wth, gypsy magic");
			}
		}
	}
	
	public void tickLogin() {
		synchronized (loginQueue) {
			for (int i = 0; i < loginQueue.size(); i++) {
				loginQueue.get(i).tick();
			}
		}
	}
	
	public void broadcastPacket(Packet packet) {
		Player[] players = getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			players[i].push(packet);
		}
	}
	
	public Player[] getOnlinePlayers() {
		Collection<Player> cache = clients.values();
		Player[] players = new Player[cache.size()];
		int i = 0;
		Iterator<Player> iter = cache.iterator();
		while (iter.hasNext()) {
			players[i] = iter.next();
			i++;
		}
		return players;
	}
	
	public void removePlayer(Player p) {
		try {
			synchronized (loginQueue) {
				loginQueue.remove(p);
			}
			clients.remove(p.getUsername());
		}
		catch (Exception ex) {
			// Were never in, or already removed.
		}
	}
	
	public void addPlayer(Player p) {
		synchronized (loginQueue) {
			if (loginQueue.remove(p)) {
				clients.put(p.getUsername(), p);
				System.out.println(p.getUsername() + " has logged in");
			}
			else {
				throw new NoSuchPlayerException();
			}
		}
	}
	
	public Player getPlayer(String name) {
		return clients.get(name);
	}
	
	public String getSalt() {
		return salt;
	}
}
