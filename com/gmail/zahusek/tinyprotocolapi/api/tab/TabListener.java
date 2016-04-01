package com.gmail.zahusek.tinyprotocolapi.api.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;

public class TabListener implements Listener 
{
	
	@EventHandler
	void quit(PlayerQuitEvent e)
	{
		TabAPI.remove(TinyProtocolAPI.class, e.getPlayer());
	}
	
	@EventHandler
	void disable(PluginDisableEvent e)
	{
		if(!e.getPlugin().getClass().equals(TinyProtocolAPI.class)) return;
		for(Player player : Bukkit.getServer().getOnlinePlayers())
			TabAPI.remove(TinyProtocolAPI.class, player);
	}
}
