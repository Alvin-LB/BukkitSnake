package com.bringholm.bukkitsnake;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.HashMap;

public class ChatGrid {
    private String[] grid;
    private char gridChar;
    private HashMap<Point, ChatColor> colours = new HashMap<>();
    private int size = 0;

    public ChatGrid(ChatColor backgroundColour, int size) {
        this(backgroundColour, size, '⬛');
    }

    public ChatGrid(ChatColor backgroundColour, int size, char gridChar) {
        this.grid = new String[size];
        this.size = size;
        this.gridChar = gridChar;
        setBackground(backgroundColour);
    }

    private void generateGrid() {
        for (int y = 0; y < size; y++) {
            StringBuilder lineBuilder = new StringBuilder();
            for (int x = 0; x < size; x++) {
                lineBuilder.append(colours.get(new Point(x, y))).append(gridChar);
            }
            grid[y] = lineBuilder.toString();
        }
    }

    public String[] getGrid() {
        generateGrid();
        return grid;
    }

    public void draw(Point p, ChatColor colour) {
        Validate.notNull(p, "Point cannot be null!");
        Validate.notNull(colour, "Colour cannot be null!");
        if (p.getX() > size || p.getY() > size || p.getX() < 0 || p.getY() < 0) {
            throw new IllegalArgumentException("Point specified is out of bounds!");
        }
        colours.put(p, colour);
    }

    public void fillRectangle(Rectangle rectangle, ChatColor colour) {
        for (int x = (int) rectangle.getX(); x < rectangle.getX() + rectangle.getWidth(); x++) {
            for (int y = (int) rectangle.getY(); y < rectangle.getY() + rectangle.getHeight(); y++) {
                this.draw(new Point(x, y), colour);
            }
        }
    }

    public void setBackground(ChatColor colour) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                colours.put(new Point(x, y), colour);
            }
        }
    }
}
