package com.gmail.zahusek.tinyprotocolapi.listener;

import org.bukkit.plugin.Plugin;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.element.PacketType;

public class RegisteredPacket
{
	private final PacketType type;
    private final PacketListener listener;
    private final PacketExecutor executor;
    private final PacketPriority priority;
    private final Plugin plugin;
    private final boolean cancelled;
    private final ClassAccess access;

    public RegisteredPacket (PacketType a, PacketListener b, PacketExecutor c,
    		PacketPriority d, Plugin e, boolean f,
    		ClassAccess g)
    {
    	this.type = a;
        this.listener = b;
        this.executor = c;
        this.priority = d;
        this.plugin = e;
        this.cancelled = f;
        this.access = g;

    }

    public Class<?> getParentClass () { return listener.getClass(); }
    
    public ClassAccess getAccessor () { return access; }
    
    public PacketType getType ()
    { return type; }
    
    public PacketListener getListener ()
    { return listener; }

    public Plugin getPlugin ()
    { return plugin; }

    public PacketPriority getPriority ()
    {return priority;}

	public void callEvent (PacketEvent event) throws Exception
    {
        if (!event.isCancelled() || !isIgnoringCancelled())
            executor.call(listener, event);
    }

    public boolean isIgnoringCancelled ()
    {return cancelled;}
}