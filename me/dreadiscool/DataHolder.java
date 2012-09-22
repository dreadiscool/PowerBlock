package me.dreadiscool;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class DataHolder {

	private HashMap<String, String> data = new HashMap<String, String>();
	
	public void load(String filePath) {
		try {
			Scanner s = new Scanner(new FileReader(filePath));
			while (s.hasNext()) {
				String[] read = s.next().split("=");
				setDataString(read[0], read[1]);
			}
		}
		catch (Exception ex) { ex.printStackTrace(); }
	}
	
	public void save(String filePath) {
		try {
			synchronized (data) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				Iterator<String> iter = data.keySet().iterator();
				while (iter.hasNext()) {
					String cIndex = iter.next();
					bw.write(cIndex + "=" + getDataString(cIndex) + "\n");
					bw.flush();
				}
				bw.close();
			}
		}
		catch (Exception ex) { ex.printStackTrace(); }
	}
	
	public boolean isDefinedOrSet(String index, String defaultValue) {
		try { if (getDataString(index) != null) return true; }
		catch (Exception ex) { setDataString(index, defaultValue); }
		return false;
	}
	
	public void setDataString(String call, String value) {
		synchronized (data) {
			data.put(call, value);
		}
	}
	
	public String getDataString(String call) {
		synchronized (data) {
			try {
				if (data.get(call) == null)
					throw new Exception("null");
				return data.get(call);
			}
			catch (Exception ex) { return ""; }
		}
	}
	
	public boolean getDataBoolean(String call) {
		try { return Boolean.valueOf(getDataString(call)); }
		catch (Exception ex) { return false; }
	}
	
	public int getDataInteger(String call) {
		try { return Integer.valueOf(getDataString(call)); }
		catch (Exception ex) { return 0; }
	}
	
	public long getDataLong(String call) {
		try { return Long.valueOf(getDataString(call)); }
		catch (Exception ex) { return 0; }
	}
}
