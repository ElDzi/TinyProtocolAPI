package com.gmail.zahusek.tinyprotocolapi.listener;

public interface PacketExecutor
{
	void call(PacketListener listen, PacketEvent event) throws Exception;
}