package com.gmail.zahusek.tinyprotocolapi.packet;

import java.util.ArrayList;
import java.util.List;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.InfoAction;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperInfoData;

public class PacketPlayerInfo extends Packet {
	
	private final static ClassAccess fa = new ClassAccess("{nms}.PacketPlayOutPlayerInfo");

	public PacketPlayerInfo() { super(fa.newInstance()); }
	
	public PacketPlayerInfo(InfoAction action) 
	{ 
		super(fa.newInstance());
		fa.set(handle, 0, action.getHadle());
	}
	
	public void addInfo(WrapperInfoData... info){
		ArrayList<Object> a = new ArrayList<>();
		for(WrapperInfoData b : info)
			a.add(b.getHandle());
		ma().addAll(a);
	}
	
	protected List<Object> ma(){
		return fa.get(handle, 1);
	}
	@Override
	public String toString() {
		return fa.toString();
	}
}
