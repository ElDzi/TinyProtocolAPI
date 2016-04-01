package com.gmail.zahusek.tinyprotocolapi.packet;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperChat;

public class PacketChat extends Packet {
	
	private final static ClassAccess access = new ClassAccess("{nms}.PacketPlayOutChat");
	
	public PacketChat(Object ichat, byte type) 
	{ super(2, ichat, type); }
	
	public PacketChat(String chat, byte type) 
	{ super(2, WrapperChat.toIChat(chat), type); }

	@Override
	public ClassAccess access() {
		return access;
	}
}
