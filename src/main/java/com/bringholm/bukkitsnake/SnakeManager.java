package com.bringholm.bukkitsnake;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_10_R1.Packet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class SnakeManager extends PacketAdapter implements CommandExecutor {

    public SnakeManager(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    private HashMap<UUID, Snake> snakePlayers = new HashMap<>();

    private boolean packetSending = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(args.length > 0 && args.length <= 2)) {
            if (sender instanceof Player) {
                if (snakePlayers.containsKey(((Player) sender).getUniqueId())) {
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "Incorrect arguments!");
            return true;
        }
        switch (args[0]) {
            case "start":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!snakePlayers.containsKey(player.getUniqueId())) {
                        Snake snake = new Snake(player);
                        snake.runTaskTimer(BukkitSnake.getInstance(), 0, 7);
                        snakePlayers.put(player.getUniqueId(), snake);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Only players can do this!");
                }
                break;
            case "stop":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (snakePlayers.containsKey(player.getUniqueId())) {
                        removeSnakeGame(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You aren't playing Snake!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Only players can do this!");
                }
                break;
            case "move":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (snakePlayers.containsKey(player.getUniqueId())) {
                        if (args.length > 1) {
                            switch (args[1]) {
                                case "up":
                                    snakePlayers.get(player.getUniqueId()).movementInput(Snake.Direction.UP);
                                    break;
                                case "down":
                                    snakePlayers.get(player.getUniqueId()).movementInput(Snake.Direction.DOWN);
                                    break;
                                case "left":
                                    snakePlayers.get(player.getUniqueId()).movementInput(Snake.Direction.LEFT);
                                    break;
                                case "right":
                                    snakePlayers.get(player.getUniqueId()).movementInput(Snake.Direction.RIGHT);
                                    break;
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not playing Snake!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Only players can do this!");
                }
                break;
        }
        return true;
    }

    public void removeSnakeGame(Player player) {
        snakePlayers.get(player.getUniqueId()).cancel();
        snakePlayers.remove(player.getUniqueId());
        player.sendMessage(ChatColor.GOLD + "You have stopped playing Snake!");
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if (snakePlayers.containsKey(e.getPlayer().getUniqueId())) {
            if (!packetSending) {
                e.setCancelled(true);
            } else {
                packetSending = false;
            }
        }
    }

    public void sendMessage(Player player, String message) {
        packetSending = true;
        player.sendMessage(message);
    }

    public void sendPacket(Player player, Object packet) {
        packetSending = true;
        Object handle = BukkitReflectionUtils.invokeMethod(player, "getHandle");
        Object playerConnection = BukkitReflectionUtils.getField(handle, "playerConnection");
        try {
            playerConnection.getClass().getMethod("sendPacket", BukkitReflectionUtils.getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void sendPacketWithoutReflection(Player player, Packet packet) {
        packetSending = true;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
