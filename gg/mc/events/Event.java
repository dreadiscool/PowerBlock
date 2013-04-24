package gg.mc.events;

public class Event {

	protected boolean cancelled = false;
	
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	public final boolean isCancelled() {
		return cancelled;
	}
}
