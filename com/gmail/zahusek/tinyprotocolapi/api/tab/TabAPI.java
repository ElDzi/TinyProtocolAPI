package com.gmail.zahusek.tinyprotocolapi.api.tab;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocol;
import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.api.Preference;
import com.gmail.zahusek.tinyprotocolapi.api.SettingAPI;

public abstract class TabAPI
{
	static final Class<TinyProtocolAPI> fa = TinyProtocolAPI.class;	
	static final TabMap fb = new TabMap();
	static final TinyProtocol fc = TinyProtocolAPI.getTinyProtocol();

	
	private static TabHolder a(Player a)
	{ return fb.get(a.getUniqueId());}
	
	public static void refresh(Class<? extends Plugin> plugin, Player player, Preference priority, SettingAPI<TabModify> modify)
	{
		TabHolder a = a(player);
		if(a.e.compareTo(priority) < 0) return;
		if(!a.f.isAssignableFrom(plugin)) 
		{a.mb();}
		modify.call(a);
		fc.sendAbstractPacket(player, a.ma());
		a.vb(priority, plugin);
	}
	
	public static void remove(Class<? extends Plugin> plugin, Player player)
	{
		TabHolder a = a(player);
		if(!a.f.isAssignableFrom(plugin) && !a.f.isAssignableFrom(fa)) return;
		fc.sendAbstractPacket(player, a.mb());
		a.va();
	}
	
	public static boolean hasAPI(Player player)
	{ return a(player).g; }
	
	@Deprecated
	public static boolean isExist(Player player)
	{ return hasAPI(player); }		
}
