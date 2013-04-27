package gg.mc.plugin;

import gg.mc.Player;
import gg.mc.PowerBlock;
import gg.mc.events.*;

import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Plugin {

	private PluginManager pluginManager;
	private Scriptable scope;
	private String name;
	private String author;
	private String version;
	private String code;
	
	public Plugin(PluginManager pluginManager, String name, String author, String version, ArrayList<String> lines) {
		this.pluginManager = pluginManager;
		this.name = name;
		this.author = author;
		this.version = version;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			sb.append(lines.get(i) + " ");
		}
		sb.setLength(sb.length() - 1);
		this.code = sb.toString();
		scope = pluginManager.getContext().initStandardObjects();
		
		// Global variables
		Object printWrapper = Context.javaToJS(new PrintWrapper(this, System.out), scope);
		ScriptableObject.putProperty(scope, "console", printWrapper);
		Object self = Context.javaToJS(this, scope);
		ScriptableObject.putProperty(scope, "instance", self);
		Object server = Context.javaToJS(PowerBlock.getServer(), scope);
		ScriptableObject.putProperty(scope, "$", server);
	}
	
	public void onEnable() {
		pluginManager.getContext().evaluateString(scope, code, name, 1, null);
		try {
			Function onEnable = (Function) scope.get("onEnable", scope);
			onEnable.call(pluginManager.getContext(), scope, scope, new Object[] { });
		}
		catch (ClassCastException ex) {
			System.out.println("Nag plugin author " + getAuthor() + " for not having an onEnable() function!");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onDisable() {
		try {
			Function onDisable = (Function) scope.get("onDisable", scope);
			onDisable.call(pluginManager.getContext(), scope, scope, new Object[] { });
		}
		catch (ClassCastException ex) {
			// No onDisable(), meh, idc
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onPlayerLogin(PlayerLoginEvent e) {
		try {
			Function playerLogin = (Function) scope.get("onPlayerLogin", scope);
			playerLogin.call(pluginManager.getContext(), scope, scope, new Object[] { e });
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onPlayerQuit(PlayerQuitEvent e) {
		try {
			Function playerLogin = (Function) scope.get("onPlayerQuit", scope);
			playerLogin.call(pluginManager.getContext(), scope, scope, new Object[] { e });
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onPlayerKick(PlayerKickEvent e) {
		try {
			Function playerKick = (Function) scope.get("onPlayerKick", scope);
			playerKick.call(pluginManager.getContext(), scope, scope, new Object[] { e });
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onPlayerChat(PlayerChatEvent e) {
		try {
			Function playerChat = (Function) scope.get("onPlayerChat", scope);
			playerChat.call(pluginManager.getContext(), scope, scope, new Object[] { e });
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onHeartBeat(HeartbeatEvent e) {
		try {
			Function heartBeat = (Function) scope.get("onHeartBeat", scope);
			heartBeat.call(pluginManager.getContext(), scope, scope, new Object[] { e });
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onBlockBreak(BlockBreakEvent e) {
		try {
			Function blockBreak = (Function) scope.get("onBlockBreak", scope);
			blockBreak.call(pluginManager.getContext(), scope, scope, new Object[] { e });
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent e) {
		try {
			Function blockPlace = (Function) scope.get("onBlockPlace", scope);
			blockPlace.call(pluginManager.getContext(), scope, scope, new Object[] { e });
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean onPlayerCommand(Player player, String command, String[] args) {
		try {
			Function playerCommand = (Function) scope.get("onPlayerCommand", scope);
			Object result = playerCommand.call(pluginManager.getContext(), scope, scope, new Object[] { player, command, args });
			return Context.toBoolean(result);
		}
		catch (ClassCastException ex) { }
		catch (Exception ex) {
			System.out.println("An error occurred handling the command '" + command + "' for the plugin " + this.getPluginName());
			ex.printStackTrace();
		}
		return false;
	}
	
	public String getPluginName() {
		return name;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getVersion() {
		return version;
	}
}
