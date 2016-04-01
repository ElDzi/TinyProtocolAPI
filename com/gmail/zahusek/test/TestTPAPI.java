package com.gmail.zahusek.test;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.zahusek.test.example.ExampleListener;
import com.gmail.zahusek.test.serverstatus.BetterServerListPingEvent;
import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.api.Preference;
import com.gmail.zahusek.tinyprotocolapi.api.tab.TabAPI;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketHandler;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketListener;

public class TestTPAPI extends JavaPlugin implements PacketListener
{
	
    @Override
    public void onEnable()
    {
    	//register of PacketListener
    	TinyProtocolAPI.registerPackets(this, this);
    	TinyProtocolAPI.registerPackets(new ExampleListener(), this);
    	getServer().getPluginManager().registerEvents(new ExampleListener(), this);
    	
    	//default priority
    	TabAPI.setDefaultPreference(this.getClass(), Preference.NORMAL);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command,
    		String label, String[] args) {
    	TabAPI.refresh(this.getClass(), (Player) sender, api ->
    	{
    		api.setTexture("MHF_Cow");
    		api.setSignal(99999);
    		
    		api.setMessage(0, 0, "test1");
    		api.setSignal(1, 0, 0);
    		api.setTexture(2, 0, "MHF_Slime");
    		
    		Collection<String> list = Arrays.asList("eldo", "eldo", "eldo");
    		api.set(Arrays.asList("elo", "elo", "elo"), list);
    		
    		ArrayList<String> foot = api.getFooter();
    		System.out.println(foot);
    		
    		api.set(0, 1, "test2", 13);
    		api.set(1, 1, "test3", "MHF_Pig");
    		api.set(2, 1, "test4", "MHF_Zombie", 0);
    		System.out.println(api.getMessage(0, 0));
    		System.out.println(api.getTexture(2, 1));
    		System.out.println(api.getSignal(0, 1));
    	});
    	return super.onCommand(sender, command, label, args);
    }
    
    @PacketHandler
    void test(BetterServerListPingEvent e)
    {
    	e.getDefaultJson().addProperty(e.motd(), "§6▓TinyProtocolAPI▓" + "\n" + "§9§k###§eVersion §5§n4.1§6§9§k###");
    	e.getSampleJson().add(e.hoverList(), e.sampleList(Arrays.asList("elo", "elo", "elo")));
    	e.saveJson();
    }
}