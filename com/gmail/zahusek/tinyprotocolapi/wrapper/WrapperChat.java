package com.gmail.zahusek.tinyprotocolapi.wrapper;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static org.apache.commons.lang.StringUtils.strip;

public class WrapperChat {
	
	private static final ClassAccess fa = new ClassAccess("{nms}.IChatBaseComponent$ChatSerializer");
	private static final Class<?> fb = fa.getCanonicalClass("{nms}.IChatBaseComponent");
	private static final Gson fc = fa.get(null, Gson.class, 0);
	
	public static Object toIChat(String chat) 
	{ return fc.fromJson("{text:'" + chat + "'}", fb); }
	
	public static String toChat(Object ichat) 
	{ return strip(fc.toJson(ichat), "\""); }
	
	public static Object toIChatFromJson(String chat) 
	{ return fc.fromJson(chat, fb); }
	
	public static Object toIChatFromJson(JsonObject chat) 
	{ return fc.fromJson(chat.toString(), fb); }
}
