package com.gmail.zahusek.test.example;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.packet.Packet;

public class ExamplePacket extends Packet
{
	private final static ClassAccess accessor = new ClassAccess("{nms}.PacketPlayOutHeldItemSlot");

	public ExamplePacket(final int index)
	{
		//newInstance()
		super();
		/**
		 * newInstance(...) with Parametrs ?
		 * super("elo", 1, true);
		 */
		
		//set first primitive integer of new object
		access().setInt(handle, 0, index);
	}
	
	@Override
	public ClassAccess access() {
		return accessor;
	}
}
