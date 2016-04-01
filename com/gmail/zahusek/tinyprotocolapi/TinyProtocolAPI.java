package com.gmail.zahusek.tinyprotocolapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import com.gmail.zahusek.tinyprotocolapi.api.Preference;
import com.gmail.zahusek.tinyprotocolapi.api.tab.TabAPI;
import com.gmail.zahusek.tinyprotocolapi.api.tab.TabMap;
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
		long s = System.nanoTime();
		for(int i = 0; i < 1000000; i++);
		long e = System.nanoTime();
		System.out.println(e-s);
	}
	
	private final int fa = ma(getBukkitVersion());//1.8.4 /*13*/
	private final Ansi fb = Ansi.ansi();
	private final String fc = fb.a(Ansi.Attribute.RESET).toString();
	private static TinyProtocol fd;
	private Properties fe;
	private final String ff = "Settings.properties";
	
	@Override
	public void onEnable()
	{
		PluginManager a = getServer().getPluginManager();
		va(Color.YELLOW, "Checking version of engine...");
		if(fa < 13)
		{ 
            va(Color.RED, "Unexpected problem: Your version of engine is not compatible with TinyProtocolAPI");
            a.disablePlugin(this);
            return;
		}
		va(Color.GREEN, "Done.\n");
		
		fe = new Properties();
		vb();
		fd = new TinyProtocol();
		a.registerEvents(new TabMap(), this);
		GameMode b = getServer().getDefaultGameMode();
		ChatColor[] c = ChatColor.values();
		Random d = new Random();
		if(fe.getProperty("testmode", "false").equals("true"))
		{
			new BukkitRunnable() 
			{
				
				@Override
				public void run() {
					for(Player a : getServer().getOnlinePlayers())
					{
						if(a.isOp() || a.isWhitelisted() || (a.getGameMode() != b && b == GameMode.CREATIVE))
						{
							TitleAPI.broadcast(a, "§eBETA §bTinyProtocolAPI §54.0", c[d.nextInt(c.length)] 
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
													c[d.nextInt(c.length)] + "It is test mode !");
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
		fd.close();
		HandlerList.unregisterAll(fd);
	}
	
	public static TinyProtocol getTinyProtocol()
	{
		return fd;
	}
	
	public void va(Color a, String b)
	{
		switch (a) {
			case RED:
				getLogger().warning(fb.fg(a).toString() + b + fc);
				break;
			default :
				getLogger().info(fb.fg(a).toString() + b + fc);
				break;
		}
	}
	
	void vb()
	{
		File b = getDataFolder();
		if(!b.exists())
			b.mkdirs();
		File c = new File(b, ff);
		if(!c.exists())
		{
			InputStream d = getResource(ff);
			try {
				FileUtils.copyInputStreamToFile(d, c);
			} catch (IOException e) {
				e.printStackTrace();
				va(Color.RED, "Unexpected problem: " + ff + " will is not copied");
				return;
			}
			finally
			{
				if(d != null)
				{
					try {
						d.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
			
		FileInputStream e = null;
		try { 
			fe.load(e = new FileInputStream(c));
		} catch (IOException e1) {
			e1.printStackTrace();
			va(Color.RED, "Unexpected problem: " + ff +" is not loaded");
			return;
		}
		finally 
		{
			if(c != null)
			{
				try 
				{
					e.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
	
	int ma(String a)
	{
		int b = 0;
		c:for(char d : a.toCharArray())
		{
			switch(d)
			{
				case '-': 
					break c;
				case '.':
				case '0': 
					break;
				default:
					b += (d + 2) % 10;
					break ;
			}
		}
		return b;
	}
	
    static Map<Class<?>, Set<RegisteredPacket>> mb(PacketListener a, Plugin b)
    {
        Map<Class<?>, Set<RegisteredPacket>> c = Maps.newHashMap();
       
        for (Method d : a.getClass().getDeclaredMethods())
        {
            PacketHandler e = d.getAnnotation(PacketHandler.class);
            if (e == null || d.getParameterTypes().length != 1 || !PacketEvent.class.isAssignableFrom(d.getParameterTypes()[0]))
                continue;
            Class<?> f = d.getParameterTypes()[0].asSubclass(PacketEvent.class);
            PacketID g = f.getAnnotation(PacketID.class);
            if (g == null) 
            	continue;
            
            if (!c.containsKey(f)) 
            	c.put(f, new HashSet<RegisteredPacket>());

            PacketExecutor h = new PacketExecutor()
            {
                public void a (PacketListener a, PacketEvent b)
                {
                    try
                    {d.invoke(a, b);} 
                    catch (Exception e)
                    {e.printStackTrace();}
                }
            };
            c.get(f).add(new RegisteredPacket(g.id(), a, h, e.priority(), b, e.ignoreCancelled()));
        }
        return c;
    }

    public static void registerPackets (PacketListener a, Plugin b)
    {
        if (!b.isEnabled()) return;
        for (Entry<Class<?>, Set<RegisteredPacket>> c : mb(a, b).entrySet())
        {
        	PacketHandlerList d = new ClassAccess(c.getKey()).get(null, PacketHandlerList.class, 0);
        	d.registerAll(c.getValue());
        }
    }
    
}
