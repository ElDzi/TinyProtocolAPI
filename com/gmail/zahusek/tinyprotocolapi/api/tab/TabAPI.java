package com.gmail.zahusek.tinyprotocolapi.api.tab;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocol;
import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.api.Preference;
import com.gmail.zahusek.tinyprotocolapi.api.SettingAPI;

public abstract class TabAPI
{
	static final Class<? extends Plugin> parent = TinyProtocolAPI.class;	
	static final HashMap<UUID, TabHolder> holder = new HashMap<>();
	static final TinyProtocol manager = TinyProtocolAPI.getTinyProtocol();
	static final HashMap<Class<?>, Preference> priorities = new HashMap<>();

	
	protected static TabHolder holder(Player a)
	{
		UUID b = a.getUniqueId();
		if(!holder.containsKey(b))
			holder.put(b, new TabHolder());
		return holder.get(b);
	}
	public static void refresh(Class<? extends Plugin> plugin,
			Player player, Preference priority, SettingAPI<TabModify> api)
	{
    	if(plugin == null) throw new IllegalArgumentException("Class of plugin cannot be null !");
    	if(player == null) throw new IllegalArgumentException("Player cannot be null !");
    	if(priority == null) throw new IllegalArgumentException("Preference cannot be null !");
    	if(api == null) throw new IllegalArgumentException("TabModify cannot be null !");

    	TabHolder hold = holder(player);
		if(hold.priority.compareTo(priority) < 0)
			return;
        if (hold.plugin.isAssignableFrom(plugin)) {
        
		api.call(hold);
		manager.sendAbstractPacket(player, hold.update());
		hold.takeOver(plugin, priority);
		}
	}
	
	public static void refresh(Class<? extends Plugin> plugin, Player player, SettingAPI<TabModify> modify) {
		Preference priority = priorities.getOrDefault(plugin, Preference.LOW);
		refresh(plugin, player, priority, modify);
	}
	
	public static void remove(Class<? extends Plugin> plugin, Player player)
	{
    	if(plugin == null) throw new IllegalArgumentException("Class of plugin cannot be null !");
    	if(player == null) throw new IllegalArgumentException("Player cannot be null !");
    	final TabHolder hold = holder(player);
		Preference priority = priorities.getOrDefault(plugin, Preference.LOW);
    	if(hold.plugin.isAssignableFrom(parent) || hold.plugin.isAssignableFrom(plugin) || hold.priority.compareTo(priority) < 0)
		manager.sendAbstractPacket(player, hold.remove());
	}
	
    public static void setDefaultPreference(Class<? extends Plugin> plugin, Preference priority) 
    {
    	if(plugin == null) throw new IllegalArgumentException("Class of plugin cannot be null !");
    	if(priority == null) throw new IllegalArgumentException("Preference cannot be null !");
    	priorities.put(plugin, priority);
    }
    
    public static boolean hasAPI(Player player) 
    {
    	if(player == null) throw new IllegalArgumentException("Player cannot be null !");
        return holder(player).exist;
    }
}
