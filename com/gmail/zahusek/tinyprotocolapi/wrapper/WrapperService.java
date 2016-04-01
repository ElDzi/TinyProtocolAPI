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

public abstract class WrapperService {
	
	private static final ClassAccess craftonline = new ClassAccess("{obc}.entity.CraftPlayer");
	private static final ClassAccess craftofffline = new ClassAccess("{obc}.CraftOfflinePlayer");
	
	private static final ClassAccess entity = new ClassAccess("{nms}.EntityPlayer");

	private static final ClassAccess craftserver = new ClassAccess("{obc}.CraftServer");
	public static final ClassAccess minecraftserver = new ClassAccess("{nms}.MinecraftServer");	
	public static final Object server = craftserver.invoke(getServer(), "getServer");
	
	private static final boolean mode = getOnlineMode();

	public static GameProfile getProfile (String name)
	{
		Player online = getPlayerExact(name);
	    @SuppressWarnings("deprecation")
	    GameProfile profile = online != null ? entity.invoke(craftonline.invoke(online, "getHandle"), "getProfile"): craftofffline.invoke(getOfflinePlayer(name), "getProfile");
	    if(!mode) return profile;
	    if (getFirst(profile.getProperties().get("textures"), null) == null)
	    {
	    	MinecraftSessionService service = minecraftserver.get(server, MinecraftSessionService.class, 0);
	    	profile = service.fillProfileProperties(profile, true);
	    }
	    return profile;
	}
}
