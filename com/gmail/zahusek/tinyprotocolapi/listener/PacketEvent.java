package com.gmail.zahusek.tinyprotocolapi.listener;

import io.netty.channel.Channel;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public abstract class PacketEvent implements Cancellable
{
	public final Player player;
	public final Channel channel;
	public final Object handle;
	private boolean c;
	
	public PacketEvent(Player player, Channel channel, Object handle)
	{
		this.player = player;
		this.channel = channel;
		this.handle = handle;
		c = false;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Channel getChannel() {
		return channel;
	}
	 
	public Object getPacket() {
		return handle;
	}

	@Override
	public boolean isCancelled() {
		return c;
	}

	@Override
	public void setCancelled(boolean cancel) {
		c = cancel;
	}
	
	abstract public PacketHandlerList getPacketHandlerList();
}
