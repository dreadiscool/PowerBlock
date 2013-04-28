package gg.mc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class Configuration {

	private String name = "PowerBlock Server";
	private int serverPort = 25565;
	private int maxPlayers = 100;
	private String motd = "Building is fun!";
	
	public Configuration() {
		try {
			Scanner s = new Scanner(new FileReader(new File(System.getProperty("user.dir") + File.separator + "server.properties")));
			s.useDelimiter(System.getProperty("line.separator"));
			while (s.hasNext()) {
				String[] params = s.next().split("=");
				if (params[0].equals("name")) {
					name = params[1];
				}
				else if (params[0].equals("port")) {
					serverPort = Integer.parseInt(params[1]);
				}
				else if (params[0].equals("maxplayers")) {
					maxPlayers = Integer.parseInt(params[1]);
				}
				else if (params[0].equals("motd")) {
					motd = params[1];
				}
			}
			s.close();
		}
		catch (Exception ex) {
			System.out.println("No config!");
			generateConfig();
		}
	}
	
	private void generateConfig() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir") + File.separator + "server.properties")));
			String s = System.getProperty("line.separator");
			bw.write("name=" + name + s);
			bw.write("port=" + serverPort + s);
			bw.write("maxplayers=" + maxPlayers + s);
			bw.write("motd=" + motd + s);
			bw.flush();
			bw.close();
		}
		catch (Exception ex) {
			System.out.println("Failed to generate config :-(");
			ex.printStackTrace();
		}
	}
	
	public String getServerName() {
		return name;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public String getMotd() {
		return motd;
	}
	
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	
	public void setServerName(String name) {
		this.name = name;
	}
	
	public void setMotd(String motd) {
		this.motd = motd;
	}
}
