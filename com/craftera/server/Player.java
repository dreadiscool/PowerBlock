package com.craftera.server;

import java.net.Socket;

public class Player {

	private Socket socket;
	
	public Player(String name, Socket socket) {
		this.socket = socket;
	}
}
