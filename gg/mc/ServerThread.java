package gg.mc;

import gg.mc.network.ConnectionThread;
import gg.mc.network.packets.Packet1Ping;

public class ServerThread extends Thread {

	private ConnectionThread connectionThread;
	private long ticks = 0;
	
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
				boolean isPingTick = ticks % 40 == 0;
				for (int i = 0; i < players.length; i++) {
					players[i].tick();
					if (isPingTick) {
						players[i].push(new Packet1Ping());
					}
				}
				// Let's not kill the processor
				Thread.sleep(25);
				ticks++;
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
