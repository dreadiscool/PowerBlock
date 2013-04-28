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
				
				// Scheduler
				ArrayList<Task> tasks = PowerBlock.getServer().getScheduler().checkFunctions();
				for (int i = 0; i < tasks.size(); i++) {
					Task t = tasks.get(i);
					t.getPlugin().callFunction(t.getFunctionName());
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
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A fatal error occurred - please restart your server and report this to dreadiscool!");
			System.out.println("A temporary fix has been applied.");
			PowerBlock.getServer().getPluginManager().unload();
			run();
		}
	}
	
	public void dispatchCommand(String command) {
		synchronized (consoleCommandsLeft) {
			consoleCommandsLeft.add(command);
		}
	}
}
