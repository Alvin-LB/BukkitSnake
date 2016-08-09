package com.bringholm.bukkitsnake;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class SnakeManager extends PacketAdapter implements CommandExecutor {

    public SnakeManager(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    private HashMap<UUID, Snake> snakePlayers = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equals("start")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (snakePlayers.containsKey(player.getUniqueId())) {
                    snakePlayers.put(player.getUniqueId(), new Snake(player));
                }
            }
        }
        return true;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if (snakePlayers.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
