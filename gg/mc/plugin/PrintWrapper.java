package gg.mc.plugin;

import java.io.PrintStream;

public class PrintWrapper extends PrintStream {
	
	private Plugin plugin;
	
	public PrintWrapper(Plugin plugin, PrintStream printStream) {
		super(printStream);
		this.plugin = plugin;
	}
	
	@Override
	public void println(String stuff) {
		this.print(stuff + "\n");
	}
	
	@Override
	public void print(String stuff) {
		System.out.print("[" + plugin.getPluginName() + "] " + stuff);
	}
}
