package gg.mc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.zip.GZIPOutputStream;

import gg.mc.network.ConnectionThread;
import gg.mc.network.PacketInputStream;
import gg.mc.network.PacketOutputStream;
import gg.mc.network.packets.Packet;
import gg.mc.network.packets.Packet0Identification;
import gg.mc.network.packets.Packet13Message;
import gg.mc.network.packets.Packet14Disconnect;
import gg.mc.network.packets.Packet1Ping;
import gg.mc.network.packets.Packet2Initialize;
import gg.mc.network.packets.Packet3Chunk;
import gg.mc.network.packets.Packet4Finalize;
import gg.mc.network.packets.Packet5UpdateBlock;
import gg.mc.network.packets.Packet8Position;

public class Player {

	private ConnectionThread connectionThread;
	private String inetAddress;
	private PacketInputStream packetInputStream;
	private PacketOutputStream packetOutputStream;
	
	private String username;
	private boolean loggedIn = false;
	
	public Player(ConnectionThread connectionThread, Socket socket) throws IOException {
		this.connectionThread = connectionThread;
		this.packetInputStream = new PacketInputStream(socket.getInputStream());
		this.packetOutputStream = new PacketOutputStream(socket.getOutputStream());
		this.inetAddress = socket.getRemoteSocketAddress().toString();
	}
	
	public void tick() {
		try {
			if (!packetInputStream.hasPacket()) {
				return;
			}
			if (!loggedIn) {
				try {
					Packet0Identification ident = (Packet0Identification) packetInputStream.nextPacket();
					MessageDigest md5 = MessageDigest.getInstance("MD5");
					byte[] token = ("1234567891234567" + ident.getUsername()).getBytes("UTF-8");
					if (new String(md5.digest(token)).equals(ident.getVerificationKey())) {
						loggedIn = true;
						connectionThread.addPlayer(this);
					}
					else {
						kick("Failed to verify username!");
					}
				}
				catch (ClassCastException e) {
					kick("Must send identification packet, smart one");
				}
			}
			packetOutputStream.writePacket(new Packet1Ping());
			if (packetInputStream.hasPacket()) {
				Packet incoming = packetInputStream.nextPacket();
				if (incoming instanceof Packet5UpdateBlock) {
					
				}
				else if (incoming instanceof Packet8Position) {
					
				}
				else if (incoming instanceof Packet13Message) {
					StringBuilder sb = new StringBuilder();
					sb.append("<");
					sb.append(this.getUsername());
					sb.append("> ");
					sb.append(((Packet13Message) incoming).getMessage());
					PowerBlock.getServer().broadcastMessage(sb.toString());
				}
			}
		}
		catch (Exception ex) {
			kick("Failed to handle packet");
		}
	}
	
	public void sendWorld(World world) {
		try {
			byte[] worldData = world.getWorldData();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(bos);
			DataOutputStream dos = new DataOutputStream(gos);
			
			dos.writeInt(worldData.length);
			dos.write(worldData);
			gos.finish();
			byte[] gzip = bos.toByteArray();
			int packets = (int) Math.ceil(gzip.length / 1024);
			
			packetOutputStream.writePacket(new Packet2Initialize());
			for (int i = 0; i < packets; i++) {
				byte[] buff;
				if (gzip.length - (i * 1024) > 1024) {
					buff = new byte[1024];
				}
				else {
					buff = new byte[gzip.length - (i * 1024)];
					int percent = (i / packets) * 100;
					packetOutputStream.writePacket(new Packet3Chunk(buff, (byte) percent));
				}
				for (int k = 0; k < buff.length; k++) {
					buff[k] = gzip[k + (i * 1024)];
				}
			}
			packetOutputStream.writePacket(new Packet4Finalize(world.getLength(), world.getHeight(), world.getDepth()));
		}
		catch (IOException ex) {
			kick(ex.getMessage());
		}
	}
	
	public void sendMessage(String message) {
		try {
			packetOutputStream.writePacket(new Packet13Message(message));
		}
		catch (Exception ex) {
			kick();
		}
	}
	
	public void kick() {
		kick("Disconnected from server");
	}
	
	public void kick(String message) {
		if (loggedIn) {
			System.out.println("[Server] " + getUsername() + " [" + getInetAddress() + "] disconnected from server");
		}
		else {
			System.out.println("[Server] [" + getInetAddress() + "] lost connection to the server");
		}
		try {
			packetOutputStream.writePacket(new Packet14Disconnect(message));
		}
		catch (Exception ex) {
			// Well hell, they were getting kicked anyway
		}
		connectionThread.removePlayer(this);
	}
	
	/**
	 * Top level handler to push packets. Writes directly to packetstream, but fails silently
	 * @param pack The packet to send
	 */
	public void push(Packet pack) {
		try {
			packetOutputStream.writePacket(pack);
		}
		catch (IOException ex) {
			kick(ex.getMessage());
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getInetAddress() {
		return inetAddress;
	}
}
