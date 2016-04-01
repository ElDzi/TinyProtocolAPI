package com.gmail.zahusek.tinyprotocolapi.api.tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.api.Preference;
import com.gmail.zahusek.tinyprotocolapi.packet.Packet;
import com.gmail.zahusek.tinyprotocolapi.packet.PacketHeaderFooter;
import com.gmail.zahusek.tinyprotocolapi.packet.PacketPlayerInfo;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.GameType;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.InfoAction;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperInfoData;
import com.mojang.authlib.GameProfile;

public class TabHolder implements TabModify 
{
	final int x = 4, y = 20;
	final GameProfile[][] profile = new GameProfile[x][y];
	final String uuid = "00000000-0000-%s-0000-000000000000";
	final String token = "!@#$^*";
	final GameType gamemode = GameType.NOT_SET;
	
	Class<?> plugin;
	Preference priority;
	boolean exist = false;
	
	String[][] message = new String[x][y];
	boolean[][] xmessage = new boolean[x][y];
	
	int[][] signal = new int[x][y];
	boolean[][] xsignal = new boolean[x][y];
	
	String[][] texture = new String[x][y];
	boolean[][] xtexture = new boolean[x][y];
	
	String _message = "";
	int _signal = 0;
	String _texture = "MHF_Question";
	
	final PacketPlayerInfo remove = new PacketPlayerInfo(InfoAction.REMOVE_PLAYER);

	final PacketHeaderFooter hnf = new PacketHeaderFooter();
	
	TabHolder() 
	{
		final LinkedList<WrapperInfoData> xdefault = new LinkedList<>();
		for(int x = 0; x < this.x; x++)
		{
			for(int y = 0; y < this.y; y++)
			{
				String idx = digit(x);
				String idy = digit(y);
				GameProfile profile = this.profile[x][y] = new GameProfile(UUID.fromString(String.format(uuid, idx + idy)), token + idx + token + idy);
				xdefault.add(new WrapperInfoData(profile, _signal, gamemode, _message));
			}
		}
		remove.addAll(xdefault);
		reset();
	}
	
	protected Packet[] update()
	{
		final PacketPlayerInfo add = new PacketPlayerInfo(InfoAction.ADD_PLAYER);
		final PacketPlayerInfo display = new PacketPlayerInfo(InfoAction.UPDATE_DISPLAY_NAME);
		final PacketPlayerInfo latency = new PacketPlayerInfo(InfoAction.UPDATE_LATENCY);

		for(int x = 0; x < this.x; x++)
		{
			for(int y = 0; y < this.y; y++)
			{
				WrapperInfoData data = new WrapperInfoData(profile[x][y], signal[x][y], gamemode, message[x][y]);
				if(!exist || !xtexture[x][y])
				{
					data.setTexture(texture[x][y]);
					add.add(data);
					xtexture[x][y] = true;
				}
				if(!xmessage[x][y])
				{
					display.add(data);
					xmessage[x][y] = true;
				}
				if(!xsignal[x][y])
				{
					latency.add(data);
					xsignal[x][y] = true;
				}
			}
		}
		exist = true;
		return new Packet[] {add, display, latency};
	}
	
	protected Packet[] remove()
	{
		reset();
		return new Packet[] {hnf, remove};
	}

	@Override
	public void setMessage(int x, int y, String message) {
		if(this.message[x][y].equals(message)) return;
		this.message[x][y] = message;
		xmessage[x][y] = false;
	}

	@Override
	public void setTexture(int x, int y, String texture) {
		if(this.texture[x][y].equals(texture))return;
		this.texture[x][y] = texture;
		xtexture[x][y] = false;
	}
	
	@Override
	public void setSignal(int x, int y, int signal) 
	{
		if(this.signal[x][y] == signal) return;
		this.signal[x][y] = signal;
		xsignal[x][y] = false;
	}

	@Override
	public void setTexture(String texture) {
		for(int x = 0; x < this.x; x++)
			for(int y = 0; y < this.y; y++)
				setTexture(x, y, texture);
	}

	@Override
	public void setSignal(int signal) {
		for(int x = 0; x < this.x; x++)
			for(int y = 0; y < this.y; y++)
				setSignal(x, y, signal);
	}

	@Override
	public void set(int x, int y, String message, String texture) {
		setMessage(x, y, message);
		setTexture(x, y, texture);
		
	}

	@Override
	public void set(int x, int y, String message, int signal) {
		setMessage(x, y, message);
		setSignal(x, y, signal);
	}

	@Override
	public void set(int x, int y, String message, String texture, int signal) {
		setMessage(x, y, message);
		setSignal(x, y, signal);
		setTexture(x, y, texture);
	}

	@Override
	public void setHeader(Collection<String> header) {
		if(header == null || header.isEmpty()) 
			return;
		String[] head = header.toArray(new String[header.size()]);
		StringBuilder text = new StringBuilder();
		text.append(head[0] == null ?  _message : head[0]);
		for(int i = 1; i < head.length; i++)
			text.append("\n" + head[i]);
		hnf.setHeader(text.toString());
	}

	@Override
	public void setFooter(Collection<String> footer) {
		if(footer == null || footer.isEmpty()) 
			return;
		String[] foot = footer.toArray(new String[footer.size()]);
		StringBuilder text = new StringBuilder();
		text.append(foot[0] == null ?  _message : foot[0]);
		for(int i = 1; i < foot.length; i++)
			text.append("\n" + foot[i]);
		hnf.setFooter(text.toString());
	}

	@Override
	public void set(Collection<String> header, Collection<String> footer) {
		setHeader(header);
		setFooter(footer);
	}

	@Override
	public String getMessage(int x, int y) {
		return message[x][y];
	}

	@Override
	public String getTexture(int x, int y) {
		return texture[x][y];
	}

	@Override
	public int getSignal(int x, int y) {
		return signal[x][y];
	}

	@Override
	public ArrayList<String> getHeader() {
		ArrayList<String> head = new ArrayList<>();
		String[] text = hnf.getHeader().split("\n");
		for(String line : text)
			head.add(line);
		return head;
	}

	@Override
	public ArrayList<String> getFooter() {
		ArrayList<String> foot = new ArrayList<>();
		String[] text = hnf.getFooter().split("\n");
		for(String line : text)
			foot.add(line);
		return foot;
	}
	
	protected void reset()
	{
		plugin = TinyProtocolAPI.class;
		priority = Preference.LOW;
		for(int x = 0; x < this.x; x++)
		{
			for(int y = 0; y < this.y; y++)
			{
				message[x][y] = _message;
				signal[x][y] = _signal;
				texture[x][y] = _texture;
			}
		}
		hnf.setFooter(_message);
		hnf.setHeader(_message);
		exist = false;
	}
	
	protected void takeOver(Class<?> a, Preference b)
	{
		this.plugin = a;
		this.priority = b;
	}
	
    String digit (int i)
    {return i > 9 ? "" + i : "0" + i;}
}