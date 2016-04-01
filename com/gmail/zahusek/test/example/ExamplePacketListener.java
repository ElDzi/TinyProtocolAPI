package com.gmail.zahusek.test.example;

import io.netty.channel.Channel;

import org.bukkit.entity.Player;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocol;
import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketEvent;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketHandlerList;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketID;
import com.gmail.zahusek.tinyprotocolapi.element.PacketType;

/**
 * IMPORTANT !
 * ID of Packet
 * if class has not identyfikator = will not be registered !
 **/
@PacketID(id = PacketType.PacketPlayInHeldItemSlot)
public class ExamplePacketListener extends PacketEvent {
	
	/**
	 * IMPORTANT !
     * "handler" stores all packetlistener with object of this class
	 **/
	private final static PacketHandlerList handler = new PacketHandlerList();
	/**
	 * 
	 * ClassAccess is better alternative of normal reflections - faster and very simple
	 * first parameter of constructor it is String (class path) or Class<?>
	 * 
	 * {nms} = net.minecraft.server.VERSION
	 * {obc} = org.bukkit.craftbukkit.VERSION
	 *
	 **/
	private final static ClassAccess access = new ClassAccess("{nms}.PacketPlayInHeldItemSlot"); 
	private final static TinyProtocol manager = TinyProtocolAPI.getTinyProtocol();
	/**
	 * IMPORTANT !
	 * Constructor acquires data about Player and Packet(Object)
	 **/
	public ExamplePacketListener(Player player, Channel channel, Object handle) 
	{super(player, channel, handle);}
	
	
	/**
	 * return first(0) object of type primitive Integer
	 **/
	public int getSlot()
	{return access.getInt(handle, 0);}
	
	/**
	 * Create new packet and send to Player
	 **/
	public void setSlot(int index)
	{manager.sendAbstractPacket(channel, new ExamplePacket(index));}

	@Override
	public PacketHandlerList getPacketHandlerList() {
		return handler;
	}

}
