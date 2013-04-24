package gg.mc.exceptions;

public class WorldExistsException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public WorldExistsException(String worldName) {
		super("Cannot create world '" + worldName + "' - it already exists");
	}
}
