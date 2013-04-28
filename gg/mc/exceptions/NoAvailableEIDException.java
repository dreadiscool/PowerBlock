package gg.mc.exceptions;

import gg.mc.World;

public class NoAvailableEIDException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoAvailableEIDException(World world) {
		super("There are no available EIDs left for the world " + world.toString());
	}
}
