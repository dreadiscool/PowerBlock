package gg.mc.exceptions;

public class HeartbeatCancelledException extends Exception {

	private static final long serialVersionUID = 1L;

	public HeartbeatCancelledException() {
		super("Heartbeat cancelled by plugin!");
	}
}
