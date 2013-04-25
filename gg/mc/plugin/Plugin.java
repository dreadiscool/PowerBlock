package gg.mc.plugin;

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
		Object printWrapper = Context.javaToJS(System.out, scope);
		ScriptableObject.putProperty(scope, "console", printWrapper);
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
	}
	
	public void onDisable() {
		
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
