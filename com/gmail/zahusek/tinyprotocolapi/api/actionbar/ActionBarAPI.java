package com.gmail.zahusek.tinyprotocolapi.api.actionbar;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocol;
import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.packet.PacketChat;

import static com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.TitleAction.*;

public abstract class ActionBarAPI {
  static final TinyProtocol fb = TinyProtocolAPI.getTinyProtocol();

	public static void hotbar(Player player, String msg)
	{     	
		if(player == null) throw new IllegalArgumentException("Player cannot be null !");
		PacketChat packet = new PacketChat(msg, (byte) 2); 
		fb.sendAbstractPacket(player, packet);
	}
	
	public static void clear(Player player)
	{
		if(player == null) throw new IllegalArgumentException("Player cannot be null !");
		PacketChat packet = new PacketChat("",(byte) 2);
		fb.sendAbstractPacket(player, packet);
	}
}
