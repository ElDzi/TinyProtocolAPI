package com.gmail.zahusek.tinyprotocolapi.api.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.plugin.Plugin;

import com.gmail.zahusek.tinyprotocolapi.TinyProtocolAPI;
import com.gmail.zahusek.tinyprotocolapi.api.Preference;
import com.gmail.zahusek.tinyprotocolapi.packet.Packet;
import com.gmail.zahusek.tinyprotocolapi.packet.PacketHeaderFooter;
import com.gmail.zahusek.tinyprotocolapi.packet.PacketPlayerInfo;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.GameType;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperEnum.InfoAction;
import com.gmail.zahusek.tinyprotocolapi.wrapper.WrapperInfoData;
import com.mojang.authlib.GameProfile;

import static java.util.UUID.fromString;
import static java.lang.String.format;

public class TabHolder implements TabModify 
{
	final int x, y;
	final GameProfile[][] a;
	final GameType b;
	final String c, d;//default msg, default texture
	
	Preference e;
	Class<? extends Plugin> f;
	boolean g;//exist
	
	String h, i;//header, footer
	String[][] j, k, l;//msg, texture, utexture
	int[][] m, n;//ping, uping

	public TabHolder() {
		x = 4;
		y = 20;
		a = new GameProfile[x][y];
		b = GameType.NOT_SET;
		c = "";
		d = "MHF_Question";
		
		e = Preference.LOW;
		f = TinyProtocolAPI.class;
		g = false;
		
		h = "";
		i = "";
		
		j = new String[x][y];// msg
		k = new String[x][y];// texture
		l = new String[x][y];// texture ultimate
		m = new int[x][y];// ping
		n = new int[x][y];// ping ultimate
		String o = "00000000-0000-%s-0000-000000000000";
		String p = "!@#$^*";
		for (int x = 0; x < this.x; x++) 
		{
			for (int y = 0; y < this.y; y++) 
			{
				String q = mc(x) + mc(y);
				a[x][y] = new GameProfile(fromString(format(o, q)), p + q);
			}
		}
		va();
	}

	Collection<Packet> ma() {
		PacketHeaderFooter a = new PacketHeaderFooter();
		PacketPlayerInfo b = new PacketPlayerInfo(InfoAction.ADD_PLAYER);
		PacketPlayerInfo c = new PacketPlayerInfo(InfoAction.UPDATE_DISPLAY_NAME);
		PacketPlayerInfo d = new PacketPlayerInfo(InfoAction.UPDATE_LATENCY);

		GameType e = this.b;
		String f = this.c;
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 20; y++) {
				GameProfile h = this.a[x][y];
				String i = this.k[x][y];
				int j = this.m[x][y];
				
				if (!this.l[x][y].equals(i))
				{
					WrapperInfoData z = new WrapperInfoData(h, 0, e, f);
					z.setTexture(this.l[x][y] = i);
					b.addInfo(z);
				}
				if (this.n[x][y] != j)
					d.addInfo(new WrapperInfoData(h, this.n[x][y] = j, e, f));
				c.addInfo(new WrapperInfoData(h, 0, e, this.j[x][y]));
			}
		}
		a.setHeader(h);
		a.setFooter(i);
		this.g = true;
		return Arrays.asList(a, b, c, d);
	}

	Collection<Packet> mb() {
		PacketHeaderFooter a = new PacketHeaderFooter();
		PacketPlayerInfo b = new PacketPlayerInfo(InfoAction.REMOVE_PLAYER);
		GameType c = this.b;
		String d = this.c;

		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 20; y++) {
				b.addInfo(new WrapperInfoData(this.a[x][y], 0, c, d));
			}
		}
		a.setHeader(d);
		a.setFooter(d);
		this.g = false;
		return Arrays.asList(a, b);
	}

	@Override
	public void setMessage(int x, int y, String display) {
		j[x][y] = display == null ? c : display;
	}

	@Override
	public void setTexture(int x, int y, String texture) {
		k[x][y] = texture == null ? d : texture;
	}
	
	@Override
	public void setTexture(String texture) {
		String a = texture == null ? d : texture;
		for (int x = 0; x < 4; x++)
			for (int y = 0; y < 20; y++)
				k[x][y] = a;
	}

	@Override
	public void setSignal(int x, int y, int ping) {
		m[x][y] = ping;
	}
	
	@Override
	public void setSignal(int ping) {
		for (int x = 0; x < 4; x++)
			for (int y = 0; y < 20; y++)
				m[x][y] = ping;
	}

	@Override
	public void setHeader(List<String> head) {
		Iterator<String> a = head.iterator();
		String b = a.next();
		String c = b == null ? this.c : b;
		while (a.hasNext())
			c += '\n' + a.next();
		h = c;
	}
	@Override
	public void setFooter(List<String> foot) {
		Iterator<String> a = foot.iterator();
		String b = a.next();
		String c = b == null ? this.c : b;
		while (a.hasNext())
			c += '\n' + a.next();
		i = c;
	}

	@Override
	public String getMessage(int x, int y) {
		return j[x][y];
	}

	@Override
	public String getTexture(int x, int y) {
		return k[x][y];
	}

	@Override
	public int getSignal(int x, int y) {
		return m[x][y];
	}

	@Override
	public List<String> getHeader() {
		ArrayList<String> a = new ArrayList<>();
		StringBuilder b = new StringBuilder();
		for (char c : h.toCharArray()) {
			switch (c) {
				case '\n' :
					a.add(b.toString());
					b = new StringBuilder();
					break;
				default :
					b.append(c);
					break;
			}

		}
		a.add(b.toString());
		return a;
	}

	@Override
	public List<String> getFooter() {
		ArrayList<String> a = new ArrayList<>();
		StringBuilder b = new StringBuilder();
		for (char c : i.toCharArray()) {
			switch (c) {
				case '\n' :
					a.add(b.toString());
					b = new StringBuilder();
					break;
				default :
					b.append(c);
					break;
			}

		}
		a.add(b.toString());
		return a;
	}

	@Override
	public void set(int x, int y, String display, String texture) {
		setMessage(x, y, display);
		setTexture(x, y, texture);
	}

	@Override
	public void set(int x, int y, String display, int ping) {
		setMessage(x, y, display);
		setSignal(x, y, ping);

	}

	@Override
	public void set(int x, int y, String display, String texture, int ping) {
		setMessage(x, y, display);
		setTexture(x, y, texture);
		setSignal(x, y, ping);
	}

	@Override
	public void set(List<String> head, List<String> foot) {
		setHeader(head);
		setFooter(foot);

	}

	void va() {
		e = Preference.LOW;
		f = TinyProtocolAPI.class;
		for (int x = 0; x < this.x; x++) {
			for (int y = 0; y < this.y; y++) {
				j[x][y] = c;
				k[x][y] = d;
				l[x][y] = c;
				m[x][y] = 0;
				n[x][y] = 0;
			}
		}
	}

	void vb(Preference a, Class<? extends Plugin> b) {
		e = a;
		f = b;
	}
	
    String mc (int a)
    {return a > 9 ? "" +  a : "0" + a;}
}
