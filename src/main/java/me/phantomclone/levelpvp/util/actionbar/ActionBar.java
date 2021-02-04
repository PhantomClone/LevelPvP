package me.phantomclone.levelpvp.util.actionbar;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;

public class ActionBar {

    private JSONObject json;

    public ActionBar(String text) {
        Preconditions.checkNotNull(text);
        this.json = convert(text);
    }

    public ActionBar(JSONObject json) {
        Preconditions.checkNotNull(json);
        Preconditions.checkArgument(!json.isEmpty());
        this.json = json;
    }

    public static void send(Player player, String message) {
        new ActionBar(message).send(player);
    }

    public static void sendToAll(String message) {
        new ActionBar(message).sendToAll();
    }

    public void send(Player player) {
        Preconditions.checkNotNull(player);
        if (ServerPackage.getServerVersion().contains("v1_8_")) {
            try {
                Class<?> clsIChatBaseComponent = ServerPackage.MINECRAFT.getClass("IChatBaseComponent");
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
                Object chatBaseComponent = ServerPackage.MINECRAFT.getClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, json.toString());
                Object packetPlayOutChat = ServerPackage.MINECRAFT.getClass("PacketPlayOutChat").getConstructor(clsIChatBaseComponent, byte.class).newInstance(chatBaseComponent, (byte) 2);
                playerConnection.getClass().getMethod("sendPacket", ServerPackage.MINECRAFT.getClass("Packet")).invoke(playerConnection, packetPlayOutChat);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else if (ServerPackage.getServerVersion().contains("v1_16_")) {
            try {
                Class<?> clsIChatBaseComponent = ServerPackage.MINECRAFT.getClass("IChatBaseComponent");
                Class<?> clsChatMessageType = ServerPackage.MINECRAFT.getClass("ChatMessageType");
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
                Object chatBaseComponent = ServerPackage.MINECRAFT.getClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, json.toString());
                Object chatMessageType = clsChatMessageType.getMethod("valueOf", String.class).invoke(null, "GAME_INFO");
                Object packetPlayOutChat = ServerPackage.MINECRAFT.getClass("PacketPlayOutChat").getConstructor(clsIChatBaseComponent, clsChatMessageType, UUID.class).newInstance(chatBaseComponent, chatMessageType, player.getUniqueId());
                playerConnection.getClass().getMethod("sendPacket", ServerPackage.MINECRAFT.getClass("Packet")).invoke(playerConnection, packetPlayOutChat);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Class<?> clsIChatBaseComponent = ServerPackage.MINECRAFT.getClass("IChatBaseComponent");
                Class<?> clsChatMessageType = ServerPackage.MINECRAFT.getClass("ChatMessageType");
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
                Object chatBaseComponent = ServerPackage.MINECRAFT.getClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, json.toString());
                Object chatMessageType = clsChatMessageType.getMethod("valueOf", String.class).invoke(null, "GAME_INFO");
                Object packetPlayOutChat = ServerPackage.MINECRAFT.getClass("PacketPlayOutChat").getConstructor(clsIChatBaseComponent, clsChatMessageType).newInstance(chatBaseComponent, chatMessageType);
                playerConnection.getClass().getMethod("sendPacket", ServerPackage.MINECRAFT.getClass("Packet")).invoke(playerConnection, packetPlayOutChat);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player);
        }
    }

    private JSONObject convert(String text) {
        JSONObject json = new JSONObject();
        json.put("text", text);
        return json;
    }

    public void setText(String text) {
        Preconditions.checkNotNull(text);
        this.json = convert(text);
    }

    public void setJsonText(JSONObject json) {
        Preconditions.checkNotNull(json);
        Preconditions.checkArgument(!json.isEmpty());
        this.json = json;
    }

}