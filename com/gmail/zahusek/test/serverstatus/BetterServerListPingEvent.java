package com.gmail.zahusek.test.serverstatus;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

import org.bukkit.entity.Player;

import com.gmail.zahusek.tinyprotocolapi.asm.reflection.ClassAccess;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketEvent;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketHandlerList;
import com.gmail.zahusek.tinyprotocolapi.listener.PacketID;
import com.gmail.zahusek.tinyprotocolapi.element.PacketType;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static java.util.UUID.randomUUID;
import static javax.imageio.ImageIO.write;
import static io.netty.buffer.Unpooled.buffer;
import static io.netty.handler.codec.base64.Base64.encode;

@PacketID(id = PacketType.PacketStatusOutServerInfo)
public class BetterServerListPingEvent extends PacketEvent
{

    public static final PacketHandlerList handler = new PacketHandlerList();

    private static final String DEFID = randomUUID().toString();
    private static final ClassAccess fa = new ClassAccess("{nms}.PacketStatusOutServerInfo");
    private static final Class<?> fb = fa.getCanonicalClass("{nms}.ServerPing");
    private static final Gson fc = fa.get(null, Gson.class, 0);

    private final JsonObject json;

    public BetterServerListPingEvent(Player player, Channel channel, Object handle) {
        super(player, channel, handle);
        json = new JsonParser().parse(fc.toJson(handle)).getAsJsonObject().getAsJsonObject("b");
    }

    public JsonArray sampleList(Collection<String> list)
    {
        JsonArray array = new JsonArray();
        for(String player : list)
        {
            JsonObject data = new JsonObject();
            data.addProperty("name", player);
            data.addProperty("id", DEFID);
            array.add(data);
        }
        return array;
    }

    public JsonObject getDefaultJson () { return json; }
    public JsonObject getProtocolJson() { return  json.getAsJsonObject("version"); }
    public JsonObject getSampleJson() { return  json.getAsJsonObject("players"); }
    public void saveJson() { fa.set(getPacket(), 1, fc.fromJson(json, fb)); }

    public String faviconSerializer (BufferedImage image)
    {
        if(image.getWidth() != 64 || image.getHeight() != 64) return json.get("favicon").getAsString();
       ByteBuf buffer = buffer();
       try { write(image, "PNG", new ByteBufOutputStream(buffer)); }
       catch (IOException e) { return json.get("favicon").getAsString(); }
       return "data:image/png;base64," + encode(buffer).toString(Charsets.UTF_8);
    }

    public String motd()
    { return "description";}

    public String icon()
    { return "favicon";}

    public String hoverList()
    { return "sample";}

    public String online()
    { return "online";}

    public String maxOnline()
    { return "max";}

    public String protocolVersion()
    { return "protocol";}

    public String protocolName()
    { return "name";}

	@Override
	public PacketHandlerList getPacketHandlerList() {
		return handler;
	}
}
