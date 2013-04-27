package gg.mc;

import java.util.ArrayList;

import gg.mc.network.ConnectionThread;
import gg.mc.network.packets.Packet1Ping;

public class ServerThread extends Thread {

	private ConnectionThread connectionThread;
	private ArrayList<String> consoleCommandsLeft = new ArrayList<String>();
	private long ticks = 0;
	
	public ServerThread(ConnectionThread connectionThread) {
		super("PowerBlock Server Thread");
		this.connectionThread = connectionThread;
	}
	
	@Override
	public void run() {
		PowerBlock.getServer().getPluginManager().load();
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
				
				// Console commands. Do it here to keep all script calls on same thread
				synchronized (consoleCommandsLeft) {
					for (int i = 0; i < consoleCommandsLeft.size(); i++) {
						String[] cmdRaw = consoleCommandsLeft.get(i).split(" ");
						String cmd = cmdRaw[0];
						String[] cmdArgs = new String[cmdRaw.length - 1];
						System.arraycopy(cmdRaw, 1, cmdArgs, 0, cmdArgs.length);
						if (cmd.equalsIgnoreCase("stop")) {
							if (cmdArgs.length == 0) {
								PowerBlock.getServer().stop();
								break;
							}
							else {
								System.out.println("Command 'stop' takes no arguments. Type 'stop' to stop the server.");
							}
						}
						PowerBlock.getServer().getPluginManager().callConsoleCommand(cmd, cmdArgs);
					}
					consoleCommandsLeft.clear();
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
	
	public void dispatchCommand(String command) {
		synchronized (consoleCommandsLeft) {
			consoleCommandsLeft.add(command);
		}
	}
}
