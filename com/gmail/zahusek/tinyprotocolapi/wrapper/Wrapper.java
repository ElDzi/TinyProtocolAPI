package com.gmail.zahusek.tinyprotocolapi.wrapper;

public abstract class Wrapper {
	
	Object handle;
	
	Wrapper(Object handle)
	{ this.handle = handle; }
	
	public Object getHandle()
	{ return handle; }
}
