package com.gmail.zahusek.tinyprotocolapi.packet;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import static com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperChat.toIChat;

public class PacketHeaderFooter extends Packet {
	
	private final static ClassAccess access = new ClassAccess("{nms}.PacketPlayOutPlayerListHeaderFooter");

	String head;
	String foot;
	
	public PacketHeaderFooter() { super(); }
	
	public void setHeader(String head)
	{ 
		access.set(handle, 0, toIChat(head)); 
		this.head = head;
	}
	
	public String getHeader()
	{return head;};
	
	public void setFooter(String foot)
	{ 
		access.set(handle, 1, toIChat(foot)); 
		this.foot = foot;
	}
	
	public String getFooter()
	{return foot;};
	
	@Override
	public ClassAccess access() {
		return access;
	}
}
