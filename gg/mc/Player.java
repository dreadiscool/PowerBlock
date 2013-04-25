package gg.mc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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
import gg.mc.network.packets.Packet2Initialize;
import gg.mc.network.packets.Packet3Chunk;
import gg.mc.network.packets.Packet4Finalize;
import gg.mc.network.packets.Packet5UpdateBlock;
import gg.mc.network.packets.Packet8Position;
import gg.mc.network.packets.PacketGWomClient;

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
					byte[] token = (connectionThread.getSalt() + ident.getUsername()).getBytes("UTF-8");
					md5.update(token);
					String verificationToken = new BigInteger(1, md5.digest()).toString(16);
					if (verificationToken.equals(ident.getVerificationKey())) {
						username = ident.getUsername();
						loggedIn = true;
						Configuration config = PowerBlock.getServer().getConfiguration();
						packetOutputStream.writePacket(new Packet0Identification((byte) 7, config.getServerName(), config.getMotd(), (byte) 0x00));
						connectionThread.addPlayer(this);
						sendWorld(PowerBlock.getServer().getWorldManager().getMainWorld());
					}
					else {
						kick("Failed to verify username!");
						System.out.println("Computed " + verificationToken + " but got " + ident.getVerificationKey());
					}
				}
				catch (ClassCastException e) {
					kick("Must send identification packet, smart one");
				}
			}
			//packetOutputStream.writePacket(new Packet1Ping());
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
				else if (incoming instanceof PacketGWomClient) {
					kick("Cheater cheater pumpkin eater... No WOM!");
				}
			}
		}
		catch (Exception ex) {
			kick("Failed to handle packet");
			ex.printStackTrace();
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
			dos.close();
			gos.close();
			byte[] gzip = bos.toByteArray();
			bos.close();
			
			packetOutputStream.writePacket(new Packet2Initialize());
			
			int position = 0;
			int length;
			int percent;
			byte[] buffer = new byte[1024];
			while(position != gzip.length) {
				length = Math.min(gzip.length - position, 1024);
				System.arraycopy(gzip, position, buffer, 0, length);
				percent = (int)(((double)(position + length) / (double)gzip.length) * 100);
				packetOutputStream.writePacket(new Packet3Chunk((short) length, buffer, (byte) percent));
				position += length;
			}
			
			packetOutputStream.writePacket(new Packet4Finalize(world.getLength(), world.getHeight(), world.getDepth()));
			packetOutputStream.writePacket(new Packet8Position((byte) -1, (short) 50, (short) 50, (short) 50, (byte) 25, (byte) 25));
			//packetOutputStream.writePacket(new Packet7SpawnPlayer((byte) 0, username, (short) 50, (short) 50, (short) 50, (byte) 25, (byte) 25));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			kick(ex.getMessage());
		}
	}
	
	public void sendMessage(String message) {
		try {
			packetOutputStream.writePacket(new Packet13Message(message));
		} catch (Exception ex) {
			kick();
		}
	}
	
	public void kick() {
		kick("Disconnected from server");
	}
	
	public void kick(String message) {
		if (loggedIn) {
			System.out.println(getUsername() + " [" + getInetAddress() + "] disconnected from server");
		}
		else {
			System.out.println("[" + getInetAddress() + "] lost connection to the server");
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
