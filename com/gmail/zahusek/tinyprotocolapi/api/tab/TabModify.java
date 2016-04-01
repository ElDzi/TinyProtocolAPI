package com.gmail.zahusek.tinyprotocolapi.api.tab;

import java.util.List;

public interface TabModify 
{
	
	public void setMessage(int x, int y, String display);
	public void setTexture(int x, int y, String texture);
	public void setTexture(String texture);
	public void setSignal(int x, int y, int ping);
	public void setSignal(int ping);
	
	public void setHeader(List<String> head);
	public void setFooter(List<String> foot);
	
	public void set(int x, int y, String display, String texture);
	public void set(int x, int y, String display, int ping);
	public void set(int x, int y, String display, String texture, int ping);
	
	public void set(List<String> head, List<String> foot);
	
	public String getMessage(int x, int y);
	public String getTexture(int x, int y);
	public int getSignal(int x, int y);
	
	public List<String> getHeader();
	public List<String> getFooter();
}
