package com.gmail.zahusek.tinyprotocolapi.listener;

import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

public class PacketHandlerList
{
    private static ArrayList<PacketHandlerList> allLists = Lists.newArrayList();
    private final EnumMap<PacketPriority, ArrayList<RegisteredPacket>> handlerslots = Maps.newEnumMap(PacketPriority.class);
    private volatile RegisteredPacket[] handlers = null;

    public PacketHandlerList ()
    {
        PacketPriority[] priority = PacketPriority.values();
        for (int i = 0; i < priority.length; i++)
            this.handlerslots.put(priority[i], new ArrayList<>());
        synchronized (allLists)
        {allLists.add(this);}
    }

    public static void unregisterAll ()
    {
        synchronized (allLists)
        {
            for (PacketHandlerList handler : allLists)
            {
                synchronized (handler)
                {
                    Collection<ArrayList<RegisteredPacket>> list = handler.handlerslots.values();
                    for (ArrayList<RegisteredPacket> packet : list)
                        packet.clear();
                }
                handler.handlers = null;
            }
        }
    }

    public static void unregisterAll (Plugin plugin)
    {
        synchronized (allLists)
        {
            for (PacketHandlerList packet : allLists)
                packet.unregister(plugin);
        }
    }


    public synchronized void register (RegisteredPacket listener)
    {
        if (handlerslots.get(listener.getPriority()).contains(listener))
        	return;
         handlers = null;
         handlerslots.get(listener.getPriority()).add(listener);
    }

    public void registerAll (Collection<RegisteredPacket> listeners)
    {listeners.stream().forEach(this::register);}

    public synchronized void unregister (Plugin plugin)
    {
        boolean changed = false;
        for (List<RegisteredPacket> priority : handlerslots.values())
        {
            ListIterator<RegisteredPacket> i = priority.listIterator();
            while (i.hasNext())
            {
                if (i.next().getPlugin().equals(plugin))
                {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) this.handlers = null;
    }

    public RegisteredPacket[] getRegisteredPacketListeners ()
    {
        while (true)
        {
            RegisteredPacket[] handlers = this.handlers;
            if (this.handlers != null) return handlers;
            
            ArrayList<RegisteredPacket> entries = Lists.newArrayList();
            this.handlerslots.entrySet().stream().forEach((map) -> entries.addAll(map.getValue()));
            return this.handlers = entries.toArray(new RegisteredPacket[entries.size()]);
        }
    }
    
	public static ArrayList<RegisteredPacket> getAllRegisteredPacketListeners ()
    {
        ArrayList<RegisteredPacket> listeners = Lists.newArrayList();
        synchronized (allLists)
        {
            for (PacketHandlerList handler : allLists)
                listeners.addAll(Arrays.asList(handler.getRegisteredPacketListeners()));
        }
        return listeners;
    }
}
