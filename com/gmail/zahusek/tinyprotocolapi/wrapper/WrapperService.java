package com.gmail.zahusek.tinyprotocolapi.wrapper;

import org.bukkit.entity.Player;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import static com.google.common.collect.Iterables.getFirst;
import static org.bukkit.Bukkit.getOfflinePlayer;
import static org.bukkit.Bukkit.getPlayerExact;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.Bukkit.getOnlineMode;

public class WrapperService {
	
	private static final ClassAccess fa = new ClassAccess("{obc}.entity.CraftPlayer");
	private static final ClassAccess fb = new ClassAccess("{obc}.CraftOfflinePlayer");
	
	private static final ClassAccess fc = new ClassAccess("{nms}.EntityPlayer");

	private static final ClassAccess fd = new ClassAccess("{obc}.CraftServer");
	private static final ClassAccess fe = new ClassAccess("{nms}.MinecraftServer");	
	
	private static final boolean ff = getOnlineMode();
	
	public static GameProfile getPrfile (String profile)
	{
		Player online = getPlayerExact(profile);
	    @SuppressWarnings("deprecation")
	    GameProfile gprofile = online != null ? fc.invoke(fa.invoke(online, "getHandle"), "getProfile"): fb.invoke(getOfflinePlayer(profile), "getProfile");
	    if(!ff) 
	    	return gprofile;
	    if (getFirst(gprofile.getProperties().get("textures"), null) == null)
	    {
	    	MinecraftSessionService service = fe.get(fd.invoke(getServer(), "getServer"), MinecraftSessionService.class, 0);
	    	gprofile = service.fillProfileProperties(gprofile, true);
	    }
	    return gprofile;
	}
}
