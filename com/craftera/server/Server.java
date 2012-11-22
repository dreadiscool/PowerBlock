package com.craftera.server;

import com.craftera.api.event.Event;

import java.io.File;
import java.util.ArrayList;

public class Server {

	private static Server instance;
	private DataHolder dataHolder = new DataHolder();
	private World mainWorld;
	private ArrayList<Event> eventList;
	
	public static void main(String[] args) {
		instance = new Server(args);
	}
	
	public static Server getPowerServerInstance() { return instance; }
	
	public Server(String[] args) {
		dataHolder.load(System.getProperty("user.dir") + File.separator + "server.properties");
		eventList = new ArrayList<Event>();
		Runnable r = new Runnable() {
			public void run() {
				while (true) {
					tick();
					try { Thread.sleep(50); }
					catch (Exception ex) { }
				}
			}
		} ;
		Thread tickThread = new Thread(r);
		tickThread.start();
		
	}
	
	public void tick() {
		synchronized (eventList) {
			for (int i = 0; i < eventList.size(); i++) {
				
			}
		}
	}
	
	public void queueEvent(Event e) {
		synchronized (eventList) {
			eventList.add(e);
		}
	}
	
	public World getMainWorld() { return mainWorld; }
}
