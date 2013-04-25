package gg.mc.exceptions;

public class InvalidChunkException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	public InvalidChunkException() {
		super("Chunk size cannot be larger than 1024 bytes!");
	}
}
