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
		new PacketChat(msg, (byte) 2); 
	}
}