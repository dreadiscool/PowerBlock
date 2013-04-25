package gg.mc.network;

import gg.mc.network.packets.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketInputStream {

	public static int getPacketLength(byte header) {
		int waitingLength = -1;
		switch (header) {
			case 0x00:
				waitingLength = 130;
				break;
			case 0x05:
				waitingLength = 8;
				break;
			case 0x08:
				waitingLength = 9;
				break;
			case 0x0d:
				waitingLength = 65;
				break;
			case (byte) 'G':
				waitingLength = 0;
				break;
		}
		return waitingLength;
	}
	
	private DataInputStream inputStream;
	private byte currentHeader;
	private byte[] payload;
	
	public PacketInputStream(InputStream inputStream) {
		this.inputStream = new DataInputStream(inputStream);
	}
	
	public boolean hasPacket() throws IOException {
		if (currentHeader == -1) {
			if (inputStream.available() >= 0) {
				currentHeader = inputStream.readByte();
			}
			else {
				return false;
			}
		}
		if (inputStream.available() >= getPacketLength(currentHeader)) {
			return true;
		}
		return false;
	}
	
	public Packet nextPacket() throws IOException {
		while (!hasPacket()) {
			try { Thread.sleep(10); }
			catch (Exception ex) { }
		}
		payload = new byte[getPacketLength(currentHeader)];
		inputStream.read(payload);
		Packet packet = null;
		switch (currentHeader) {
			case 0x00:
				packet = new Packet0Identification(payload);
				break;
			case 0x05:
				packet = new Packet5UpdateBlock(payload);
				break;
			case 0x08:
				packet = new Packet8Position(payload);
				break;
			case 0x0d:
				packet = new Packet13Message(payload);
				break;
			case (byte) 'G':
				packet = new PacketGWomClient();
				break;
		}
		currentHeader = -1;
		return packet;
	}
}
