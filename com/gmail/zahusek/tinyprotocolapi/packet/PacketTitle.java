package com.gmail.zahusek.tinyprotocolapi.packet;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperChat;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.TitleAction;

public class PacketTitle extends Packet {
	
	private final static ClassAccess fa = new ClassAccess("{nms}.PacketPlayOutTitle");
	
	public PacketTitle(TitleAction action, String chat) 
	{ super(fa.newInstance(3, action.getHadle(), WrapperChat.toIChat(chat))); }
	
	public PacketTitle(int start, int stay, int end) 
	{ super(fa.newInstance(2, start, stay, end)); }
}
