package com.gmail.zahusek.tinyprotocolapi.packet;

public abstract class Packet {
	
	Object handle;
	
	Packet(Object handle)
	{ this.handle = handle; }
	
	public Object getHandle()
	{ return handle; }
}
