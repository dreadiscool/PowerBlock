package com.craftera.api;

import com.craftera.api.event.Event;
import com.craftera.api.event.WorldGenEvent;

public class JavaPlugin {

	public void onServerTick() { }
	public void onServerCommand(Event e) { }
	public void onServerGenWorld(WorldGenEvent e) { }
	public void onPlayerJoin(Event e) { }
	public void onPlayerQuit(Event e) { }
	public void onPlayerChat(Event e) { }
	public void onPlayerCommand(Event e) { }
	public void onPlayerMove(Event e) { }
	public void onPlayerKicked(Event e) { }
	public void onPlayerBlockPlace(Event e) { }
	public void onPlayerBlockBreak(Event e) { }
}
