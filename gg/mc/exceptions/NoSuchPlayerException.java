package gg.mc.exceptions;

public class NoSuchPlayerException extends NullPointerException {

	private static final long serialVersionUID = 1L;

	public NoSuchPlayerException() {
		super("Player was being added to global list but did not begin login sequence!");
	}
}
