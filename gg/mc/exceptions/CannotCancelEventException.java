package gg.mc.exceptions;

public class CannotCancelEventException extends NullPointerException {

	private static final long serialVersionUID = 1L;

	public CannotCancelEventException() {
		super("This event may not be cancelled!");
	}
}
