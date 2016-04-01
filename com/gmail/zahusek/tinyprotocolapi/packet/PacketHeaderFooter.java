package com.gmail.zahusek.tinyprotocolapi.packet;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperChat;

public class PacketHeaderFooter extends Packet {
	
	private final static ClassAccess fa = new ClassAccess("{nms}.PacketPlayOutPlayerListHeaderFooter");

	public PacketHeaderFooter() { super(fa.newInstance()); }
	
	public void setHeader(String head)
	{ fa.set(handle, 0, WrapperChat.toIChat(head)); }
	
	public void setFooter(String foot)
	{ fa.set(handle, 1, WrapperChat.toIChat(foot)); }
}
