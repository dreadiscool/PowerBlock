package gg.mc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.zip.GZIPOutputStream;

import gg.mc.events.BlockBreakEvent;
import gg.mc.events.BlockPlaceEvent;
import gg.mc.events.PlayerChatEvent;
import gg.mc.events.PlayerKickEvent;
import gg.mc.events.PlayerKickEvent.Reason;
import gg.mc.events.PlayerLoginEvent;
import gg.mc.events.PlayerMoveEvent;
import gg.mc.events.PlayerQuitEvent;
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
import gg.mc.network.packets.Packet6SetBlock;
import gg.mc.network.packets.Packet7SpawnPlayer;
import gg.mc.network.packets.Packet8Position;
import gg.mc.network.packets.PacketGWomClient;

public class Player {
	
	private ConnectionThread connectionThread;
	private String inetAddress;
	private PacketInputStream packetInputStream;
	private PacketOutputStream packetOutputStream;
	
	private String username;
	private boolean loggedIn = false;
	// In the future, just remove them. This is used to hide logged off spam.
	private boolean disconnected = false;
	
	private byte entityId;
	private World world;
	private Position position = new Position(0, 0, 0, (byte) 0, (byte) 0);
	
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
						username = ident.getUsername().trim();
						loggedIn = true;
						Configuration config = PowerBlock.getServer().getConfiguration();
						packetOutputStream.writePacket(new Packet0Identification((byte) 7, config.getServerName(), config.getMotd(), (byte) 0x00));
						connectionThread.addPlayer(this);
						sendWorld(PowerBlock.getServer().getWorldManager().getMainWorld());
						
						// Event
						PlayerLoginEvent e = new PlayerLoginEvent(this);
						PowerBlock.getServer().getPluginManager().callEvent(e);
						if (e.getJoinMessage() != null) {
							PowerBlock.getServer().broadcastMessage(e.getJoinMessage());
						}
					}
					else {
						kick("Failed to verify username!", Reason.LOST_CONNECTION);
					}
				}
				catch (ClassCastException e) {
					kick("Must send identification packet, smart one", Reason.LOST_CONNECTION);
				}
			}
			if (packetInputStream.hasPacket()) {
				Packet incoming = packetInputStream.nextPacket();
				if (incoming instanceof Packet5UpdateBlock) {
					Packet5UpdateBlock packet = (Packet5UpdateBlock) incoming;
					byte b1 = PowerBlock.getServer().getWorldManager().getMainWorld().getBlockAt(packet.getXPos(), packet.getYPos(), packet.getZPos());
					if (packet.getMode() == 0x01) {
						// Event
						BlockPlaceEvent e = new BlockPlaceEvent(this, packet.getXPos(), packet.getYPos(), packet.getZPos(), packet.getBlockType());
						PowerBlock.getServer().getPluginManager().callEvent(e);
						if (e.isCancelled()) {
							packetOutputStream.writePacket(new Packet6SetBlock(packet.getXPos(), packet.getYPos(), packet.getZPos(), b1));
							return;
						}
						PowerBlock.getServer().getWorldManager().getMainWorld().setBlockAt(e.getPosition(), e.getBlockPlaced());
					}
					else {
						// Event
						BlockBreakEvent e = new BlockBreakEvent(this, packet.getXPos(), packet.getYPos(), packet.getZPos(), b1, packet.getBlockType());
						PowerBlock.getServer().getPluginManager().callEvent(e);
						if (e.isCancelled()) {
							packetOutputStream.writePacket(new Packet6SetBlock(packet.getXPos(), packet.getYPos(), packet.getZPos(), b1));
							return;
						}
						PowerBlock.getServer().getWorldManager().getMainWorld().setBlockAt(e.getPosition(), Block.Air);
					}
				}
				else if (incoming instanceof Packet8Position) {
					Packet8Position packet = (Packet8Position) incoming;
					
					// Event
					PlayerMoveEvent e = new PlayerMoveEvent(this, position, packet.getXPos(), packet.getYPos(), packet.getZPos(), packet.getYaw(), packet.getPitch());
					PowerBlock.getServer().getPluginManager().callEvent(e);
					if (e.isCancelled()) {
						packetOutputStream.writePacket(new Packet8Position((byte) -1, position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch()));
						return;
					}
					// Update pos to all
					this.position = new Position(packet.getXPos(), packet.getYPos(), packet.getZPos(), packet.getYaw(), packet.getPitch());
					world.broadcastWorldPacket(new Packet8Position(entityId, position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch()), this);
				}
				else if (incoming instanceof Packet13Message) {
					Packet13Message packet = (Packet13Message) incoming;
					if (packet.getMessage().startsWith("/")) {
						String[] rawStuff = packet.getMessage().substring(1).split(" ");
						String cmd = rawStuff[0];
						String[] cmdArgs = new String[rawStuff.length - 1];
						for (int i = 1; i < rawStuff.length; i++) {
							cmdArgs[i - 1] = rawStuff[i];
						}
						PowerBlock.getServer().getPluginManager().callPlayerCommand(this, cmd, cmdArgs);
					}
					else {
						// Event
						PlayerChatEvent e = new PlayerChatEvent(this, packet.getMessage());
						PowerBlock.getServer().getPluginManager().callEvent(e);
						if (!e.isCancelled()) {
							PowerBlock.getServer().broadcastMessage(e.getFormat());
						}
					}
				}
				else if (incoming instanceof PacketGWomClient) {
					kick("Cheater cheater pumpkin eater... No WOM!", Reason.PLUGIN_KICK);
				}
			}
		}
		catch (Exception ex) {
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}
	
	public void sendWorld(World world) {
		try {
			if (this.world != null) {
				world.reclaimEid(entityId);
			}
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
			while (position != gzip.length) {
				length = Math.min(gzip.length - position, 1024);
				System.arraycopy(gzip, position, buffer, 0, length);
				percent = (int) (((double) (position + length) / (double) gzip.length) * 100);
				packetOutputStream.writePacket(new Packet3Chunk((short) length, buffer, (byte) percent));
				position += length;
			}
			
			packetOutputStream.writePacket(new Packet4Finalize(world.getLength(), world.getHeight(), world.getDepth()));
			Position spawn = world.getSpawn();
			packetOutputStream.writePacket(new Packet8Position((byte) -1, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));
			
			this.entityId = world.requestEntityId();
			world.broadcastWorldPacket(new Packet7SpawnPlayer(entityId, getUsername(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));
			Player[] players = PowerBlock.getServer().getOnlinePlayers();
			for (int i = 0; i < players.length; i++) {
				if (players[i].getWorld() == world) {
					Position p = players[i].getPosition();
					packetOutputStream.writePacket(new Packet7SpawnPlayer(players[i].getEntityId(), players[i].getUsername(), p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
				}
			}
			this.world = world;
			this.position = spawn;
		}
		catch (Exception ex) {
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}
	
	public void sendMessage(String message) {
		if (disconnected || !loggedIn) {
			return;
		}
		try {
			packetOutputStream.writePacket(new Packet13Message(message));
		}
		catch (Exception ex) {
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}
	
	public void kick(String message) {
		kick(message, Reason.PLUGIN_KICK);
	}
	
	public void kick(String message, Reason reason) {
		if (reason != Reason.LOST_CONNECTION) {
			if (loggedIn && !disconnected) {
				PlayerKickEvent e = new PlayerKickEvent("Server", this, message);
				PowerBlock.getServer().getPluginManager().callEvent(e);
				if (e.isCancelled()) {
					return;
				}
				if (e.getReason() == null) {
					e.setReason("You were kicked from the server!");
				}
				message = e.getReason();
			}
		}
		try {
			packetOutputStream.writePacket(new Packet14Disconnect(message));
		}
		catch (Exception ex) {
			// Well hell, they were getting kicked anyway
		}
		disconnected = true;
		if (loggedIn) {
			// Event
			PlayerQuitEvent ev = new PlayerQuitEvent(this);
			PowerBlock.getServer().getPluginManager().callEvent(ev);
			if (ev.getQuitMessage() != null) {
				PowerBlock.getServer().broadcastMessage(ev.getQuitMessage());
			}
		}
		
		connectionThread.removePlayer(this);
		
		if (world != null) {
			world.reclaimEid(entityId);
		}
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
			kick(ex.getMessage(), Reason.LOST_CONNECTION);
		}
	}
	
	public World getWorld() {
		return world;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getInetAddress() {
		return inetAddress;
	}
	
	public byte getEntityId() {
		return entityId;
	}
	
	public Position getPosition() {
		return position;
	}
}
