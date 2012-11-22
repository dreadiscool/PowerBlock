package com.craftera.api.event;

public class Event {

	protected boolean cancelled = false;
	
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { this.cancelled = cancel; }
	
	public void handle() { }
}
