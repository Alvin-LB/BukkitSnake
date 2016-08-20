package com.bringholm.bukkitsnake;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
            this.getLogger().info("ProtocolLib not found! ProtocolLib should be installed for the optimal BukkitSnake experience!");
        } else {
            ProtocolLibrary.getProtocolManager().addPacketListener(snakeManager);
        }
        this.getCommand("snake").setExecutor(snakeManager);
    }

    public static SnakeManager getSnakeManager() {
        return snakeManager;
    }
}
