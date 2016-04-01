package com.gmail.zahusek.tinyprotocolapi.wrapper;

import java.util.Map;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.google.common.collect.MapMaker;

public abstract class WrapperEnum 
{
	public static final ClassAccess infoActionAccess = new ClassAccess("{nms}.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
	private final static Map<Integer, Object> info = new MapMaker().weakValues().makeMap();
	
	public static final ClassAccess gameTypeAccess = new ClassAccess("{nms}.WorldSettings$EnumGamemode");
	private final static Map<Integer, Object> type = new MapMaker().weakValues().makeMap();

	public static final ClassAccess titleActionAccess = new ClassAccess("{nms}.PacketPlayOutTitle$EnumTitleAction");
	private final static Map<Integer, Object> title = new MapMaker().weakValues().makeMap();
	
	static
	{
		for(int i = 0; i < InfoAction.values().length; i++)
			info.put(i, infoActionAccess.getEnum(i));
		for(int i = 0; i < GameType.values().length; i++)
			type.put(i, gameTypeAccess.getEnum(i));
		for(int i = 0; i < TitleAction.values().length; i++)
			title.put(i, titleActionAccess.getEnum(i));
	}
	
	public enum InfoAction 
	{
		 ADD_PLAYER, 
	     UPDATE_GAME_MODE, 
	     UPDATE_LATENCY, 
	     UPDATE_DISPLAY_NAME, 
	     REMOVE_PLAYER;
	    
	    public static InfoAction fromHandle(Enum<?> a)
	    {return values()[a.ordinal()];}
	    
	    public Object getHadle(){
	    	return info.get(ordinal());
	    }
	}
	
	public enum GameType
	{
	    NOT_SET, 
	    SURVIVAL, 
	    CREATIVE, 
	    ADVENTURE, 
	    SPECTATOR;
	    
	    public static GameType fromHandle(Enum<?> a)
	    {return values()[a.ordinal()];}
	    
	    public Object getHadle(){
	    	return type.get(ordinal());
	    }
	}
	
	public enum TitleAction
	{
		TITLE, 
		SUBTITLE, 
		TIMES, 
		CLEAR, 
		RESET;
		
	    public static TitleAction fromHandle(Enum<?> a)
	    {return values()[a.ordinal()];}
	    
	    public Object getHadle(){
	    	return title.get(ordinal());
	    }
	}

}
