package com.gmail.zahusek.tinyprotocolapi.wrapper;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;

public abstract class Wrapper {
	
	public final Object handle;
	
	public Wrapper()
	{ handle = access().newInstance(); }
	
	public Wrapper(int index, Object...args)
	{ handle = access().newInstance(index, args); }
	
	public Object getHandle()
	{ return handle; }
	
	abstract public ClassAccess access();
}
