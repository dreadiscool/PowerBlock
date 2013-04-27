package gg.mc.plugin;

import gg.mc.ChatColor;
import gg.mc.Player;
import gg.mc.events.*;
import gg.mc.exceptions.InvalidEventException;
import gg.mc.exceptions.InvalidPluginException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.mozilla.javascript.Context;

public class PluginManager {

	private ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<String, Plugin>();
	private Context context = Context.enter();
	
	public void load() {
		File[] filePlugins = new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator).listFiles();
		for (int i = 0; i < filePlugins.length; i++) {
			if (filePlugins[i].getAbsolutePath().endsWith(".js") && !filePlugins[i].isDirectory()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(filePlugins[i]));
					String author = "Unkown";
					String pluginName = "Unknown";
					String version = "Unknown";
					ArrayList<String> script = new ArrayList<String>();
					String in = br.readLine();
					while (in != null) {
						if (!in.startsWith("#")) {
							script.add(in);
						}
						else if (in.replace('\t', ' ').trim().startsWith("//")) {
							// Comment :o
						}
						else {
							String[] params = in.split(" ");
							if (params[params.length - 2].equals("name")) {
								pluginName = params[params.length - 1];
							}
							else if (params[params.length - 2].equals("author")) {
								author = params[params.length - 1];
							}
							else if (params[params.length - 2].equals("version")) {
								version = params[params.length - 1];
							}
						}
						in = br.readLine();
					}
					br.close();
					if (pluginName.equals("Unknown")) {
						throw new InvalidPluginException(filePlugins[i].getAbsolutePath());
					}
					Plugin plugin = new Plugin(this, pluginName, author, version, script);
					plugins.put(pluginName, plugin);
					plugin.onEnable();
					System.out.println("Enabled plugin " + plugin.getPluginName());
				}
				catch (InvalidPluginException ex) {
					ex.printStackTrace();
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public void unload() {
		Collection<Plugin> plgs = plugins.values();
		Iterator<Plugin> iter = plgs.iterator();
		while (iter.hasNext()) {
			iter.next().onDisable();
		}
		Context.exit();
	}
	
	public Plugin getPlugin(String name) {
		return plugins.get(name);
	}
	
	public void callEvent(Event event) {
		try {
			Collection<Plugin> plgs = plugins.values();
			Iterator<Plugin> iter = plgs.iterator();
			if (event instanceof PlayerChatEvent) {
				while (iter.hasNext()) {
					iter.next().onPlayerChat((PlayerChatEvent) event);
				}
			}
			else if (event instanceof PlayerKickEvent) {
				while (iter.hasNext()) {
					iter.next().onPlayerKick((PlayerKickEvent) event);
				}
			}
			else if (event instanceof PlayerLoginEvent) {
				while (iter.hasNext()) {
					iter.next().onPlayerLogin((PlayerLoginEvent) event);
				}
			}
			else if (event instanceof PlayerQuitEvent) {
				while (iter.hasNext()) {
					iter.next().onPlayerQuit((PlayerQuitEvent) event);
				}
			}
			else if (event instanceof HeartbeatEvent) {
				while (iter.hasNext()) {
					iter.next().onHeartBeat((HeartbeatEvent) event);
				}
			}
			else if (event instanceof BlockBreakEvent) {
				while (iter.hasNext()) {
					iter.next().onBlockBreak((BlockBreakEvent) event);
				}
			}
			else if (event instanceof BlockPlaceEvent) {
				while (iter.hasNext()) {
					iter.next().onBlockPlace((BlockPlaceEvent) event);
				}
			}
			else {
				throw new InvalidEventException(event);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void callPlayerCommand(Player player, String command, String[] args) {
		boolean handled = false;
		try {
			Collection<Plugin> plgs = plugins.values();
			Iterator<Plugin> iter = plgs.iterator();
			while (iter.hasNext()) {
				if (iter.next().onPlayerCommand(player, command, args)) {
					handled = true;
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		if (!handled) {
			player.sendMessage(ChatColor.WHITE + "Unknown command. Type /help for help.");
		}
	}
	
	public Context getContext() {
		return context;
	}
}
