package com.gmail.zahusek.test.example;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.gmail.zahusek.tinyprotocolapi.listener.PacketHandler;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketListener;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketPriority;

public class ExampleListener implements Listener, PacketListener //implement PakcetListeer
{
	
	//PacketHandler like EventHandler
	@PacketHandler(priority = PacketPriority.LOW)
	public void slot(ExamplePacketListener e)
	{
		Player player = e.getPlayer();
		int slot = e.getSlot();
		if(slot == 0)
		{
			player.sendMessage("§cYou don't have permission to first slot");
			e.setSlot(++slot);
		}
	}
}
