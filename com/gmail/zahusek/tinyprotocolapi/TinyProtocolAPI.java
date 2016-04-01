package com.gmail.zahusek.tinyprotocolapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.gmail.zahusek.tinyprotocolapi.api.Preference;
import com.gmail.zahusek.tinyprotocolapi.api.tab.TabAPI;
import com.gmail.zahusek.tinyprotocolapi.api.tab.TabListener;
import com.gmail.zahusek.tinyprotocolapi.api.title.TitleAPI;
import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketEvent;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketExecutor;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketHandler;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketHandlerList;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketID;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketListener;
import com.gmail.zahusek.tinyprotocolapi.listener.RegisteredPacket;
import com.google.common.collect.Maps;

import static org.bukkit.Bukkit.getBukkitVersion;

public class TinyProtocolAPI extends JavaPlugin
{
	public static void main(String[] args) 
	{
//		long s = System.nanoTime();
//		for(int i = 0; i < 1000000; i++);
//		long e = System.nanoTime();
//		System.out.println(e-s);
	}
	
	private static TinyProtocolAPI plugin;
	private static TinyProtocol manager;
	private static Properties properties;
	private final String config = "Settings.properties";
	
	@Override
	public void onEnable()
	{
		PluginManager a = getServer().getPluginManager();
		if(!compatible())
		{ 
            log("&cUnexpected problem: Your version of engine is not compatible with TinyProtocolAPI");
            a.disablePlugin(this);
            return;
		}
		
		properties = new Properties();
		
		File b = getDataFolder();
		if(!b.exists()) b.mkdirs();
		File c = new File(b, config);
		if(!c.exists())
		{
			InputStream d = getResource(config);
			try {
				FileUtils.copyInputStreamToFile(d, c);
			} catch (IOException e) {
				try {
					properties.load(d);
				} catch (IOException e1) {
					log("&cUnexpected problem: TinyProtocolAPI could not loaded default file - Where is Settings.properties ? ;o");
					a.disablePlugin(this);
					return;
				}
				log("&cUnexpected problem: Java encountered a problem when copying a file - &cused default file");
			}
			finally 
			{
				try { d.close();
				} catch (IOException e) {}
			}
		}
				
		FileInputStream e = null;
		try {
			e = new FileInputStream(c);
			properties.load(e);
		} catch (FileNotFoundException z) {
			throw new NotFoundException("File " + config + " is not exist !");
		} catch (IOException z) {
			log("&cUnexpected problem: TinyProtocolAPI could not loaded default file - Where is Settings.properties ? ;o");
			a.disablePlugin(this);
			return;
		}
		finally
		{
			try { e.close();
			} catch (IOException e1) {}
		}
			
		manager = new TinyProtocol();
		plugin = getPlugin(TinyProtocolAPI.class);
		
		a.registerEvents(new TabListener(), this);
		
		GameMode f = getServer().getDefaultGameMode();
		ChatColor[] g = ChatColor.values();
		Random h = new Random();
		if(getProperties().getProperty("testmode", "false").equals("true"))
		{
			new BukkitRunnable() 
			{
				
				@Override
				public void run() {
					for(Player a : getServer().getOnlinePlayers())
					{
						if(a.isOp() || a.isWhitelisted() || (a.getGameMode() != f && f == GameMode.CREATIVE))
						{
							TitleAPI.broadcast(a, "§eBETA §bTinyProtocolAPI §54.0", g[h.nextInt(g.length)] 
									+ "It is test mode !", 20*5, 20*5, 20*5);
							TabAPI.refresh(TinyProtocolAPI.class, a, Preference.LOW, holder ->
							{
								holder.setTexture("MHF_Exclamation");
								holder.setSignal(99999);
								holder.set(Arrays.asList("§9Last date refresh:", 
										String.format("§3%1$tF §a%1$tT", 
										System.currentTimeMillis())), 
										Arrays.asList("§eBETA §bTinyProtocolAPI §54.0"));
								for (int x = 0; x < 4; x++) 
								{
									for (int y = 0; y < 20; y++) 
									{
										if(y%2 == 0)
											holder.setMessage(x, x%2 == 0 ? y : y+1, 
													g[h.nextInt(g.length)] + "It is test mode !");
									}
								}
							});
						}
					}
				}
			}.runTaskTimerAsynchronously(this, 0L, 20*15);
		}
	}
	
	@Override
	public void onDisable() {
		manager.close();
	}
	
	public static TinyProtocol getTinyProtocol()
	{ return manager; }
	
	public static TinyProtocolAPI getTinyProtocolAPI()
	{ return plugin; }
	
	private static Properties getProperties()
	{ return properties; }
	
	
	boolean compatible()
	{
		int a = 0, b = 0, c = 0;
		e:for(char h : getBukkitVersion().toCharArray())
		{
			switch(h)
			{
				case '-': 
					break e;
				case '.':
					a++;
					break;
				default:
					if(a == 1)
						b += (h + 2) % 10;
					else if(a == 2)
						c += (h + 2) % 10;
					else 
						break;
			}
		}
		if(b < 8) 
			return false;
		if(b == 8 && c < 4)
			return false;
		return true;
	}
    
    protected void log(String a)
    {
    	a = ChatColor.translateAlternateColorCodes('&', a);
    	getServer().getConsoleSender().sendMessage(a);}
    
    static Map<Class<?>, Set<RegisteredPacket>> registered(PacketListener a, Plugin b) {
		Map<Class<?>, Set<RegisteredPacket>> c = Maps.newHashMap();

		for (Method d : a.getClass().getDeclaredMethods()) {
			PacketHandler e = d.getAnnotation(PacketHandler.class);
			if (e == null
					|| d.getParameterTypes().length != 1
					|| !PacketEvent.class.isAssignableFrom(d
							.getParameterTypes()[0]))
				continue;
			Class<?> f = d.getParameterTypes()[0].asSubclass(PacketEvent.class);
			PacketID g = f.getAnnotation(PacketID.class);
			if (g == null)
				continue;
			d.setAccessible(true);
			if (!c.containsKey(f))
				c.put(f, new HashSet<RegisteredPacket>());

			PacketExecutor h = new PacketExecutor() {
				@Override
				public void call(PacketListener a, PacketEvent b) {
					try {
						d.invoke(a, b);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException(e);
					} catch (InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			};
			c.get(f).add(
					new RegisteredPacket(g.id(), a, h, e.priority(), b, e
							.ignoreCancelled(), new ClassAccess(f)));
		}
		return c;
	}
	
	public static void registerPackets(PacketListener listener, Plugin plugin) {
		if (!plugin.isEnabled())
			return;
		for (Entry<Class<?>, Set<RegisteredPacket>> c : registered(listener, plugin).entrySet()) {
			PacketHandlerList d = new ClassAccess(c.getKey()).get(null, PacketHandlerList.class, 0);
			if (d == null) continue;
			d.registerAll(c.getValue());
		}
	}
}
