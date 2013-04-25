package gg.mc.exceptions;

public class InvalidPluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPluginException(String pathName) {
		super("Unable to load script from " + pathName + "!");
	}
}
