package gg.mc.network;

import gg.mc.network.packets.Packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream {

	private DataOutputStream outputStream;
	
	public PacketOutputStream(OutputStream outputStream) {
		this.outputStream = new DataOutputStream(outputStream);
	}
	
	public void writePacket(Packet p) throws IOException {
		outputStream.write(p.getBytes());
		outputStream.flush();
	}
}
