package gg.mc.exceptions;

public class ServerRunningException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServerRunningException() {
		super("Cannot reinitialize objects essential to run time while the server is in operation!");
	}
}
