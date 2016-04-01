package com.gmail.zahusek.tinyprotocolapi.api.tab;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocol;
import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;

public class TabMap extends HashMap<UUID, TabHolder> implements Listener
{
	private static final long serialVersionUID = -9166583256315203069L;
	private static final TinyProtocol fa = TinyProtocolAPI.getTinyProtocol(); 
	
	public TabHolder get(UUID id) {
		if(!containsKey(id))
			put(id, new TabHolder());
		return super.get(id);
	}

	@EventHandler
	void va(PlayerQuitEvent e)
	{remove(e.getPlayer().getUniqueId());}
	
	@EventHandler
	void vb(PluginDisableEvent e)
	{
		if(!e.getPlugin().getName().equals("TinyProtocolAPI")) return;
		for(Player a : e.getPlugin().getServer().getOnlinePlayers())
		{
			UUID b = a.getUniqueId();
			fa.sendAbstractPacket(a, get(b).mb());
		}
	}
}
