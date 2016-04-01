package com.gmail.zahusek.tinyprotocolapi.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public abstract class PacketEvent implements Cancellable
{
	private final Player player;
	private final Object handle;
	private boolean c;
	
	public PacketEvent(Player player, Object handle)
	{
		this.player = player;
		this.handle = handle;
		c = false;
	}
	
	public Player getPlayer() {
		return player;
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
}
