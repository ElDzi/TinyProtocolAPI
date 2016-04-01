package com.gmail.zahusek.tinyprotocolapi.packet;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;

public abstract class Packet {
	
	public final Object handle;
	
	public Packet()
	{ handle = access().newInstance(); }
	
	public Packet(int index, Object...args)
	{ handle = access().newInstance(index, args); }
	
	public Object getHandle()
	{ return handle; }
	
	abstract public ClassAccess access();
}
