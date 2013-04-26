package gg.mc.exceptions;

import gg.mc.events.Event;

public class InvalidEventException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidEventException(Event event) {
		super("Invalid event " + event.getClass().getCanonicalName());
	}
}
