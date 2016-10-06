package com.bringholm.bukkitsnake;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitSnake extends JavaPlugin {
    
    private static BukkitSnake main;
    private static SnakeManager snakeManager;

    public BukkitSnake() {
        main = this;
        snakeManager = new SnakeManager(this, PacketType.Play.Server.CHAT);
    }

    public static BukkitSnake getInstance() {
        return main;
    }

    @Override
    public void onEnable() {
        if (!this.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.getLogger().severe("ProtocolLib not found! Install a working version of ProtocolLib, otherwise this plugin will not work!");
            this.setEnabled(false);
            return;
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(snakeManager);
        this.getCommand("snake").setExecutor(snakeManager);
    }

    @Override
    public void onDisable(){
        main = null;
        snakeManager = null;
    }

    public static SnakeManager getSnakeManager() {
        return snakeManager;
    }
}
