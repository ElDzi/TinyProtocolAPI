package com.gmail.zahusek.tinyprotocolapi;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketEvent;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketHandlerList;
import com.gmail.zahusek.tinyprotocolapi.listener.RegisteredPacket;
import com.gmail.zahusek.tinyprotocolapi.packet.Packet;
import com.google.common.collect.MapMaker;

import io.netty.channel.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi.Color;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class TinyProtocol implements Listener {

	private final ClassAccess CRAFTPLAYER = new ClassAccess(
			"{obc}.entity.CraftPlayer");
	private final ClassAccess ENTITYPLAYER = new ClassAccess(
			"{nms}.EntityPlayer");

	private final ClassAccess SERVERCONNECTION = new ClassAccess(
			"{nms}.ServerConnection");
	private final ClassAccess PLAYERCONNECTION = new ClassAccess(
			"{nms}.PlayerConnection");
	private final ClassAccess NETWORKMANAGER = new ClassAccess(
			"{nms}.NetworkManager");

	private final ClassAccess CRAFTSERVER = new ClassAccess(
			"{obc}.CraftServer");
	private final ClassAccess MINECRAFTSERVER = new ClassAccess(
			"{nms}.MinecraftServer");

	final TinyProtocolAPI plugin = getPlugin(TinyProtocolAPI.class);
	private Map<UUID, Channel> channelLookup = new MapMaker().weakValues()
			.makeMap();

	private List<Channel> serverChannels = newArrayList();
	private List<Object> networkManagers;
	
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private ChannelInitializer<Channel> beginInitProtocol;
	private ChannelInitializer<Channel> endInitProtocol;
	
	private String handler = "TinyProtocol";

	public TinyProtocol() {
		try {
			registerChannelHandler();
			registerPlayers();
		} catch (IllegalArgumentException ex) {
			plugin.va(Color.YELLOW, "[TinyProtocol] Delaying server channel injection due to late bind.");
			new BukkitRunnable() {
				@Override
				public void run() {
					registerChannelHandler();
					registerPlayers();
					plugin.va(Color.YELLOW,
							"[TinyProtocol] Late bind injection successful.");
				}
			}.runTask(plugin);
		}
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	private void createServerChannelHandler() {
		
		endInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel a) throws Exception {
				try {
					synchronized (networkManagers) {
						final ChannelPipeline b = a.pipeline();
						if (b.get(handler) == null)
							b.addBefore("packet_handler", handler,new PacketInterceptor());
						injectChannelInternal(a);
					}
				} catch (Exception e) {
					plugin.va(Color.RED, "Cannot inject incomming channel " + a);
				}
			}
		};
		
		beginInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast(endInitProtocol);
			}

		};
		
		serverChannelHandler = new ChannelInboundHandlerAdapter() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg)
					throws Exception {
				final Channel channel = (Channel) msg;
				channel.pipeline().addFirst(beginInitProtocol);
				ctx.fireChannelRead(msg);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void registerChannelHandler() {
		Object a = CRAFTSERVER.get(Bukkit.getServer(), MINECRAFTSERVER.getUsedClass(),
				0);
		Object b = MINECRAFTSERVER.get(a, SERVERCONNECTION.getUsedClass(), 0);

		networkManagers = (List<Object>) SERVERCONNECTION.invoke(null, List.class, new Class[] {SERVERCONNECTION.getUsedClass()}, b);
		createServerChannelHandler();

		List<ChannelFuture> c = SERVERCONNECTION.get(b, List.class, 0);
		for (ChannelFuture d : c) {
			Channel e = d.channel();
			serverChannels.add(e);
			e.pipeline().addFirst(serverChannelHandler);
		}
	}

	private void unregisterChannelHandler() {
		if (serverChannelHandler == null)
			return;
		serverChannels.stream().forEach((serverChannel) -> {
			final ChannelPipeline pipeline = serverChannel.pipeline();
			serverChannel.eventLoop().execute(() -> {
				try {
					pipeline.remove(serverChannelHandler);
				} catch (NoSuchElementException e) {
				}
			});
		});
	}

	private void registerPlayers() {
		plugin.getServer().getOnlinePlayers().stream().forEach((player) -> {
			injectPlayer(player);
		});
	}
	
	public void sendAbstractPacket(Player player, Collection<Packet> packets) {
		sendAbstractPacket(player, packets.toArray(new Packet[packets.size()]));
	}
	
	public void sendAbstractPacket(Player player, Packet... packets) {
		ChannelPipeline a = getChannel(player).pipeline();
		for (Packet b : packets)
			a.writeAndFlush(b.getHandle());
	}

	public void sendPacket(Player player, Collection<Object> packets) {
		ChannelPipeline a = getChannel(player).pipeline();
		for (Object b : packets)
			a.writeAndFlush(b);
	}
	
	public void sendPacket(Player player, Object... packets) {
		ChannelPipeline a = getChannel(player).pipeline();
		for (Object b : packets)
			a.writeAndFlush(b);
	}
	
	public void receiveAbstractPacket(Player player, Collection<Packet> packets) {
		receiveAbstractPacket(player, packets.toArray(new Packet[packets.size()]));
	}
	
	public void receiveAbstractPacket(Player player, Packet... packets) {
		ChannelHandlerContext a = getChannel(player).pipeline().context(
				"encoder");
		for (Packet b : packets)
			a.fireChannelRead(b.getHandle());
	}
	
	public void receivePacket(Player player, Collection<Object> packets) {
		ChannelHandlerContext a = getChannel(player).pipeline().context("encoder");
		for (Object b : packets)
			a.fireChannelRead(b);
	}

	public void receivePacket(Player player, Object... packets) {
		ChannelHandlerContext a = getChannel(player).pipeline().context("encoder");
		for (Object b : packets)
			a.fireChannelRead(b);
	}

	void injectPlayer(Player a) {
		UUID b = a.getUniqueId();
		Object c = ENTITYPLAYER.get(CRAFTPLAYER.invoke(a, "getHandle"),"playerConnection");
		Object d = PLAYERCONNECTION.get(c, "networkManager");
		Channel e = NETWORKMANAGER.get(d, "channel");
		ChannelPipeline f = e.pipeline();
		if (f.get(handler) == null)
			f.addBefore("packet_handler", handler,new PacketInterceptor());
		channelLookup.put(b, e);
		injectChannelInternal(getChannel(a)).player = a;
	}

	PacketInterceptor injectChannelInternal(Channel channel) {
		return (PacketInterceptor) channel.pipeline().get(handler);
	}

	Channel getChannel(Player a) {
		return channelLookup.get(a.getUniqueId());
	}

	void uninjectChannel(final Channel channel) {
		channel.eventLoop().execute(() -> {
			channel.pipeline().remove(handler);
		});
	}

	protected void close() {
		plugin.getServer().getOnlinePlayers().stream().forEach((player) -> {
			uninjectChannel(getChannel(player));
		});
		unregisterChannelHandler();
	}

	@EventHandler()
	void join(PlayerJoinEvent e) {
		injectPlayer(e.getPlayer());
	}

	void out(Player a, Object b) {
	}

	void in(Player a, Object b) {
	}

	class PacketInterceptor extends ChannelDuplexHandler {
		public volatile Player player;

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			for (RegisteredPacket listener : PacketHandlerList.getAllRegisteredPacketListeners())
            {
                if (!listener.getType().name().equals(msg.getClass().getSimpleName())) continue;
                PacketEvent packet = new ClassAccess(listener.getParentClass()).newInstance(0, player, msg);
                listener.callEvent(packet);
                if (packet.isCancelled() || packet.getPacket() == null) 
                	return;

            }
			super.channelRead(ctx, msg);
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg,
				ChannelPromise promise) throws Exception {
			for (RegisteredPacket listener : PacketHandlerList.getAllRegisteredPacketListeners())
            {
                if (!listener.getType().name().equals(msg.getClass().getSimpleName())) continue;
                PacketEvent packet = new ClassAccess(listener.getParentClass()).newInstance(0, player, msg);
                listener.callEvent(packet);
                if (packet.isCancelled() || packet.getPacket() == null) 
                	return;

            }
			super.write(ctx, msg, promise);
		}
	}

}