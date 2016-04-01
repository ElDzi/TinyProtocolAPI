package com.gmail.zahusek.tinyprotocolapi.wrapper;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;

public abstract class WrapperEnum 
{
	private static final ClassAccess fa = new ClassAccess("{nms}.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
	private static final ClassAccess fb = new ClassAccess("{nms}.WorldSettings$EnumGamemode");
	private static final ClassAccess fc = new ClassAccess("{nms}.PacketPlayOutTitle$EnumTitleAction");
	
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
	    	return fa.getEnum(ordinal());
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
	    	return fb.getEnum(ordinal());
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
	    	return fc.getEnum(ordinal());
	    }
	}

}
