package com.gmail.zahusek.tinyprotocolapi.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.InfoAction;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperInfoData;

public class PacketPlayerInfo extends Packet
{
	private final static ClassAccess access = new ClassAccess("{nms}.PacketPlayOutPlayerInfo");

	public PacketPlayerInfo() { super(); }
	
	public PacketPlayerInfo(InfoAction action) 
	{ 
		super();
		access.set(handle, 0, action.getHadle());
	}
	
	public void add(WrapperInfoData... info){
		ArrayList<Object> data = new ArrayList<>();
		for(WrapperInfoData i : info)
			data.add(i.getHandle());
		getList().addAll(data);
	}
	
	public void addAll(Collection<WrapperInfoData> info){
		ArrayList<Object> data = new ArrayList<>();
		for(WrapperInfoData i : info)
			data.add(i.getHandle());
		getList().addAll(data);
	}
	
	public void clear()
	{getList().clear();}
	
	public List<Object> getList(){
		return access.get(handle, 1);
	}

	@Override
	public ClassAccess access() {
		return access;
	}
}
