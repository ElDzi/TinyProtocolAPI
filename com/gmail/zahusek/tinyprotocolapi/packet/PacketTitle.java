package com.gmail.zahusek.tinyprotocolapi.packet;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperChat;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.TitleAction;

public class PacketTitle extends Packet {
	
	private final static ClassAccess access = new ClassAccess("{nms}.PacketPlayOutTitle");
	
	public PacketTitle(int start, int stay, int end) 
	{ super(2, start, stay, end); }
	
	public PacketTitle(TitleAction action, String chat) 
	{ super(3, action.getHadle(), WrapperChat.toIChat(chat)); }

	@Override
	public ClassAccess access() {
		return access;
	}
}
