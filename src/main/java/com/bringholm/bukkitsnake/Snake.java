package com.bringholm.bukkitsnake;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Snake extends BukkitRunnable {
    public static boolean isReflectionEnabled = BukkitSnake.getInstance().getConfig().getBoolean("reflection-enabled");

    private char[] chars = new char[] {'︿', '﹀', '<', '>', '〈', '〉'};
    private Player player;
    private ChatGrid grid;
    private Direction direction;
    private List<Point> snakeParts = new ArrayList<>();
    private Point headPos, cherry;
    private Random random = ThreadLocalRandom.current();
    private int tailLength;
    private boolean paused;

    public Snake(Player player) {
        this.grid = new ChatGrid(ChatColor.GREEN, 20);
        this.player = player;
        this.tailLength = 1;
        this.headPos = new Point(9, 9);
        this.cherry = new Point(random.nextInt(19), random.nextInt(19));
        this.snakeParts.add(new Point(9, 8));
        this.direction = Direction.DOWN;
        this.paused = false;
    }

    @Override
    public void run() {
        if (!paused) {
            snakeParts.add(headPos);
            switch (direction) {
                case UP:
                    headPos = new Point(headPos.x, headPos.y - 1);
                    break;
                case DOWN:
                    headPos = new Point(headPos.x, headPos.y + 1);
                    break;
                case LEFT:
                    headPos = new Point(headPos.x - 1, headPos.y);
                    break;
                case RIGHT:
                    headPos = new Point(headPos.x + 1, headPos.y);
                    break;
            }
            if (snakeParts.size() > tailLength) {
                snakeParts.remove(0);
            }
            if (headPos.equals(cherry)) {
                tailLength++;
                cherry = new Point(random.nextInt(19), random.nextInt(19));
            }
            if (snakeParts.contains(headPos) || headPos.x > 19 || headPos.x < 0 || headPos.y > 19 || headPos.y < 0) {
                BukkitSnake.getSnakeManager().removeSnakeGame(this.player);
                return;
            }
            this.render();
        }
    }

    public void movementInput(Direction direction) {
        if (direction != this.direction.opposite()) {
            this.direction = direction;
        }
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public Direction opposite() {
            Direction direction = null;
            switch (this.ordinal()) {
                case 0:
                    direction = DOWN;
                    break;
                case 1:
                    direction = UP;
                    break;
                case 2:
                    direction = RIGHT;
                    break;
                case 3:
                    direction = LEFT;
                    break;
            }
            return direction;
        }
    }

    private void render() {
        this.grid.setBackground(ChatColor.GREEN);
        this.grid.draw(cherry, ChatColor.RED);
        this.grid.draw(headPos, ChatColor.BLUE);
        for (Point snakePart : snakeParts) {
            this.grid.draw(snakePart, ChatColor.BLUE);
        }
        String[] board = grid.getGrid();
        for (int i = 0; i < board.length; i++) {
            int[] jsonLines = new int[] {9, 10, 11, 18};
            if (ArrayUtils.indexOf(jsonLines, i) != -1) {
                String json = "{\"text\":\"" + board[i] + "\"}";
                switch (i) {
                    case 9:
                        json = "[" + json + ",{\"text\":\"                    \"},{\"text\":\"" + ChatColor.GOLD + ChatColor.BOLD + chars[0] + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/snake move up\"}}]";
                        break;
                    case 10:
                        json = "[" + json + ",{\"text\":\"                  \"},{\"text\":\"" + ChatColor.GOLD + ChatColor.BOLD + chars[2] + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/snake move left\"}},{\"text\":\"   \"},{\"text\":\"" + ChatColor.GOLD + ChatColor.BOLD + chars[3] + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/snake move right\"}}]";
                        break;
                    case 11:
                        json = "[" + json + ",{\"text\":\"                    \"},{\"text\":\"" + ChatColor.GOLD + ChatColor.BOLD + chars[1] + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/snake move down\"}}]";
                        break;
                    case 18:
                        json = "[" + json + ",{\"text\":\"                   \"},{\"text\":\"" + ChatColor.GOLD + ChatColor.UNDERLINE + "Exit\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/snake stop\"}}]";
                }
                if (!isReflectionEnabled) {
                    PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(json));
                    BukkitSnake.getSnakeManager().sendPacketWithoutReflection(player, packet);
                } else {
                    Class<?> chatSerializerClass = BukkitReflectionUtils.getNMSClass("IChatBaseComponent$ChatSerializer");
                    Object chatComponent;
                    try {
                        chatComponent = chatSerializerClass.getMethod("a", String.class).invoke(null, json);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                        chatComponent = null;
                    }
                    Object packet;
                    try {
                        packet = BukkitReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(BukkitReflectionUtils.getNMSClass("IChatBaseComponent")).newInstance(chatComponent);
                    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                        packet = null;
                    }
                    BukkitSnake.getSnakeManager().sendPacket(player, packet);
                }
            } else {
                String message = board[i];
                if (i == 8) {
                    message += "                " + ChatColor.GOLD + "Controls";
                } else if (i == 0) {
                    message += "             " + ChatColor.RED + ChatColor.UNDERLINE + ChatColor.BOLD + "BukkitSnake";
                }
                BukkitSnake.getSnakeManager().sendMessage(player, message);
            }
        }
    }
}
