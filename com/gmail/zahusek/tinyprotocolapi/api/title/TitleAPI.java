package com.gmail.zahusek.tinyprotocolapi.api.title;

import org.bukkit.entity.Player;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocol;
import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.packet.PacketChat;
import com.gmail.zahusek.tinyprotocolapi.packet.PacketTitle;

import static com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.TitleAction.*;

public abstract class TitleAPI
{
	
	static final TinyProtocol fb = TinyProtocolAPI.getTinyProtocol();

	public static void title(Player player, String title, int start, int stay, int end)
	{
		PacketTitle a = new PacketTitle(start, stay, end);
		PacketTitle b = new PacketTitle(TITLE, title);
		fb.sendAbstractPacket(player, a, b);
	}
	
	public static void subtitle(Player player, String subtitle, int start, int stay, int end)
	{ 
		PacketTitle a = new PacketTitle(start, stay, end);
		PacketTitle b = new PacketTitle(TITLE, "");
		PacketTitle c = new PacketTitle(SUBTITLE, subtitle);
		fb.sendAbstractPacket(player, a, b, c);
	}
	
	public static void subtitle(Player player, String subtitle)
	{ fb.sendAbstractPacket(player, new PacketTitle(SUBTITLE, subtitle)); }
	
	public static void broadcast(Player player, String title, String subtitle, int start, int stay, int end)
	{ 
		PacketTitle a = new PacketTitle(start, stay, end);
		PacketTitle b = new PacketTitle(TITLE, title);
		PacketTitle c = new PacketTitle(SUBTITLE, subtitle);
		fb.sendAbstractPacket(player, a, b, c);
	}
	
	public static void clear(Player player)
	{fb.sendAbstractPacket(player, new PacketTitle(CLEAR, ""));}
	
	@Deprecated
	public static void publication(Player player, String title, String subtitle, int start, int stay, int end)
	{broadcast(player, title, subtitle, start, stay, end);}
	
	public static void hotbar(Player player, String msg)
	{ new PacketChat(msg, (byte) 2); }
}
