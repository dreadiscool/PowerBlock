package gg.mc;

public class Configuration {

	private String name = "PowerBlock Server";
	private int serverPort = 25565;
	private int maxPlayers = 100;
	private String motd = "Building is fun!";
	
	public Configuration() {
		
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
