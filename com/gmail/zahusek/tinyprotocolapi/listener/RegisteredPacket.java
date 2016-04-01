package com.gmail.zahusek.tinyprotocolapi.listener;

import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;

public class RegisteredPacket
{
	private final PacketType type;
    private final PacketListener listener;
    private final PacketPriority priority;
    private final Plugin plugin;
    private final PacketExecutor executor;
    private final boolean ignoreCancelled;

    public RegisteredPacket (PacketType type, PacketListener listener, PacketExecutor executor, PacketPriority priority, Plugin plugin, boolean ignoreCancelled)
    {
    	this.type = type;
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;

    }

    public Class<?> getParentClass () { return listener.getClass(); }
    
    public PacketType getType ()
    { return type; }
    
    public PacketListener getListener ()
    { return listener; }

    public Plugin getPlugin ()
    { return plugin; }

    public PacketPriority getPriority ()
    {return priority;}

    public void callEvent (PacketEvent event)
    {
        if (!(event instanceof Cancellable) || !((Cancellable) event).isCancelled() || !isIgnoringCancelled())
            executor.a(listener, event);
    }

    public boolean isIgnoringCancelled ()
    {return ignoreCancelled;}
}