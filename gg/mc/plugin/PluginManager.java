package gg.mc.plugin;

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
	
	public PluginManager() {
		load();
	}
	
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
	
	public Context getContext() {
		return context;
	}
}