package com.gmail.zahusek.tinyprotocolapi.wrapper;

import static org.bukkit.Bukkit.getOnlineMode;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.GameType;
import com.mojang.authlib.GameProfile;

public class WrapperInfoData extends Wrapper {
	
	private final static ClassAccess fa = new ClassAccess("{nms}.PacketPlayOutPlayerInfo$PlayerInfoData");
	private static final boolean fb = getOnlineMode();
	
	public WrapperInfoData(GameProfile profile, int ping, GameType mode, String displayname) {
		super(0, null, profile, ping, mode.getHadle(), WrapperChat.toIChat(displayname));
	}
	
	public void setTexture(String id)
	{
		if(!fb) return;
		GameProfile confirm = WrapperService.getProfile(id);
		GameProfile toConfirm = fa.get(handle, GameProfile.class, 0);
		toConfirm.getProperties().putAll(confirm.getProperties());
	}

	@Override
	public ClassAccess access() {
		return fa;
	}
}
