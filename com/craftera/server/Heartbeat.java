package com.craftera.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

public class Heartbeat {

	private int port;
	private int max;
	private String name;
	private String isPublic;
	private String salt;
	private int users;
	private URL url;
	
	public Heartbeat() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("http://www.minecraft.net/heartbeat.jsp?");
			sb.append("name=" + name + "&");
			sb.append("port=" + String.valueOf(port) + "&");
			sb.append("max=" + String.valueOf(max) + "&");
			sb.append("public=" + isPublic + "&");
			sb.append("version=7&");
			sb.append("salt=" + salt + "&");
			sb.append("users=" + String.valueOf(users));
			url = new URL(sb.toString());
		}
		catch (Exception ex) { }
	}
	
	public String send() {
		String response = null;
		try {
			response = new BufferedReader(new InputStreamReader(url.openStream())).readLine();
			if (!(response.indexOf("bad heartbeat") >= 0))
				System.out.println("Heartbeat sent! " + response + "URL also saved to /game-url.txt");
			else
				throw new Exception();
		}
		catch (Exception ex) { System.out.println("Could not send heartbeat! Is minecraft.net unavailable?"); }
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + "game-url.txt"));
			bw.write(response + "\n");
			bw.flush();
			bw.close();
		}
		catch (Exception ex) { }
		return response;
	}
}
