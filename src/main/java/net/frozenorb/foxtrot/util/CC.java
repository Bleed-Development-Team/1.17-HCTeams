package net.frozenorb.foxtrot.util;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CC {

    private static final Pattern PATTERN = Pattern.compile(
            "<(#[a-f0-9]{6}|aqua|black|blue|dark_(aqua|blue|gray|green|purple|red)|gray|gold|green|light_purple|red|white|yellow)>",
            Pattern.CASE_INSENSITIVE
    );

    public static String translate(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        final Matcher matcher = PATTERN.matcher(text);

        text = text.replaceAll("&#", "#");

        while (matcher.find()) {
            try {
                final ChatColor chatColor = ChatColor.valueOf(matcher.group(1));

                text = StringUtils.replace(text, matcher.group(), chatColor.toString());
            } catch (IllegalArgumentException ignored) { }
        }


        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> msg){
        List<String> lines = new ArrayList<>();

        for (String line : msg){
            lines.add(CC.translate(line));
        }

        return lines;
    }

    public static void send(List<String> msg, Player player){
        List<String> lines = new ArrayList<>(msg);

        for (String line : lines){
            player.sendMessage(CC.translate(line));
        }
    }
    public static String translateHex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static final String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String OBFUSCATED = MAGIC;
    public static final String B = BOLD;
    public static final String M = MAGIC;
    public static final String O = MAGIC;
    public static final String I = ITALIC;
    public static final String S = STRIKETHROUGH;
    public static final String R = RESET;
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String D_BLUE = DARK_BLUE;
    public static final String D_AQUA = DARK_AQUA;
    public static final String D_GRAY = DARK_GRAY;
    public static final String D_GREEN = DARK_GREEN;
    public static final String D_PURPLE = DARK_PURPLE;
    public static final String D_RED = DARK_RED;
    public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static final String L_PURPLE = LIGHT_PURPLE;
    public static final String PINK = L_PURPLE;
    public static final String SCOREBAORD_SEPARATOR = CC.GRAY + CC.S + "----------------------";
    public static final String HORIZONTAL_SEPARATOR = CC.GRAY + CC.S + Strings.repeat("-", 51);
    public static final String VERTICAL_SEPARATOR = CC.GRAY + StringEscapeUtils.unescapeJava("\u2758");


    public static String LINE = "--------------------------------";

    public static ChatColor LIGHT_GREEN = ChatColor.GREEN;
    public static ChatColor LIGHT_BLUE = ChatColor.AQUA;
    public static ChatColor LIGHT_RED = ChatColor.RED;
    public static ChatColor OBFUSCATION = ChatColor.MAGIC;
    public static ChatColor CYAN = ChatColor.DARK_AQUA;
    public static ChatColor PURPLE = ChatColor.DARK_PURPLE;
    public static ChatColor ORANGE = ChatColor.GOLD;
    public static ChatColor LIGHT_GRAY = ChatColor.GRAY;
    public static ChatColor INDIGO = ChatColor.BLUE;


    public static final String ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
    public static final String ARROW_RIGHT = StringEscapeUtils.unescapeJava("\u25B6");
    public static final String ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
    public static final String ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");


}
