package gg.mc;

import gg.mc.network.ConnectionThread;

public class ServerThread extends Thread {

	private ConnectionThread connectionThread;
	
	public ServerThread(ConnectionThread connectionThread) {
		super("PowerBlock Server Thread");
		this.connectionThread = connectionThread;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				connectionThread.tickLogin();
				Player[] players = PowerBlock.getServer().getOnlinePlayers();
				for (int i = 0; i < players.length; i++) {
					players[i].tick();
				}
				// Let's not kill the processor
				Thread.sleep(25);
			}
		}
		catch (InterruptedException ex) {
			// Server is shutting down
		}
	}
	
	public void callEvent(Object event) {
		// This is for later
	}
}
