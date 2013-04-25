package gg.mc.heartbeat;

import gg.mc.network.ConnectionThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HeartbeatThread extends Thread {

	private ConnectionThread connectionThread;
	private boolean hasSuccess = false;
	
	public HeartbeatThread(ConnectionThread connectionThread) {
		super("PowerBlock Heartbeat Thread");
		this.connectionThread = connectionThread;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				try {
					StringBuilder sb = new StringBuilder();
					sb.append("https://minecraft.net/heartbeat.jsp?");
					sb.append("port=25565");
					sb.append("&max=9001");
					sb.append("&name=PowerBlock");
					sb.append("&public=True");
					sb.append("&version=7");
					sb.append("&salt=" + connectionThread.getSalt());
					sb.append("&users=8999");
					URL url = new URL(sb.toString().replace(" ", "%20"));
					BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
					StringBuilder respo = new StringBuilder();
					String in = br.readLine();
					while (in != null) {
						respo.append(in);
						in = br.readLine();
					}
					br.close();
					if (!hasSuccess) {
						System.out.println("Server says: " + respo.toString());
						hasSuccess = true;
					}
				}
				catch (IOException ex) {
					System.out.println("Failed to send heartbeat to minecraft.net, is it down?");
				}
				Thread.sleep(15000);
			}
		}
		catch (InterruptedException ex) {
			// Server shutting down
		}
	}
}
