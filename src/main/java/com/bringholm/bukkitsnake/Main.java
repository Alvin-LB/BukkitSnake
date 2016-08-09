package com.bringholm.bukkitsnake;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main main;

    public Main() {
        main = this;
    }

    public static Main getInstance() {
        return main;
    }

    @Override
    public void onEnable() {
        SnakeManager snakeManager = new SnakeManager(this, PacketType.Play.Server.CHAT);
        this.getCommand("snake").setExecutor(snakeManager);
        ProtocolLibrary.getProtocolManager().addPacketListener(snakeManager);
    }
}
