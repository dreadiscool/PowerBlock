package gg.mc.exceptions;

public class NoSuchWorldException extends NullPointerException {

	private static final long serialVersionUID = 1L;

	public NoSuchWorldException(String worldName) {
		super("The world '" + worldName + "' could not be found");
	}
}
