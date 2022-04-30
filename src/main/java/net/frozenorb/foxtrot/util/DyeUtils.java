package net.frozenorb.foxtrot.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class DyeUtils {
    public static Material chatColorToDye(ChatColor chatColor) {
        switch (chatColor) {
            case AQUA:
                return Material.LIGHT_BLUE_DYE;
            case BLACK:
                return Material.INK_SAC;
            case BLUE:
            case DARK_BLUE:
                return Material.BLUE_DYE;
            case DARK_AQUA:
                return Material.CYAN_DYE;
            case DARK_GRAY:
            case GRAY:
                return Material.GRAY_DYE;
            case DARK_GREEN:
            case GREEN:
                return Material.GREEN_DYE;
            case DARK_PURPLE:
                return Material.PURPLE_DYE;
            case DARK_RED:
            case RED:
                return Material.RED_DYE;
            case GOLD:
                return Material.ORANGE_DYE;
            case LIGHT_PURPLE:
                return Material.MAGENTA_DYE;
            case YELLOW:
                return Material.YELLOW_DYE;
            default:
                return Material.WHITE_DYE;
        }
    }

    public static ChatColor stringToChatColor(String color) {
        switch (color.toLowerCase()) {
            case "aqua":
                return ChatColor.AQUA;
            case "black":
                return ChatColor.BLACK;
            case "blue":
                return ChatColor.BLUE;
            case "dark_blue":
                return ChatColor.DARK_BLUE;
            case "dark_aqua":
                return ChatColor.DARK_AQUA;
            case "dark_gray":
                return ChatColor.DARK_GRAY;
            case "gray":
                return ChatColor.GRAY;
            case "dark_green":
                return ChatColor.DARK_GREEN;
            case "green":
                return ChatColor.GREEN;
            case "dark_purple":
                return ChatColor.DARK_PURPLE;
            case "dark_red":
                return ChatColor.DARK_RED;
            case "gold":
                return ChatColor.GOLD;
            case "light_purple":
                return ChatColor.LIGHT_PURPLE;
            case "red":
                return ChatColor.RED;
            case "yellow":
                return ChatColor.YELLOW;
            default:
                return ChatColor.WHITE;
        }
    }
}
