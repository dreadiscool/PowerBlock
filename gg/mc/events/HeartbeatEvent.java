package gg.mc.events;

public class HeartbeatEvent extends Event {

	private int port;
	private int maxPlayers;
	private String name;
	private String isPublic;
	private int onlinePlayers;
	
	public HeartbeatEvent(int port, int maxPlayers, String name, String isPublic, int onlinePlayers) {
		this.port = port;
		this.maxPlayers = maxPlayers;
		this.name = name;
		this.isPublic = isPublic;
		this.onlinePlayers = onlinePlayers;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPublic() {
		return isPublic == "True";
	}
	
	public String getPublicName() {
		return isPublic;
	}
	
	public int getPlayerCount() {
		return onlinePlayers;
	}
	
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setIsPublic(boolean isPublic) {
		this.isPublic = (isPublic == true ? "True" : "False");
	}
	
	public void setPlayerCount(int players) {
		this.onlinePlayers = players;
	}
}
