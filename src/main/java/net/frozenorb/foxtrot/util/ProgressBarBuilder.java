package net.frozenorb.foxtrot.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.Arrays;

@Getter
@Setter
@Accessors(fluent = true)
public class ProgressBarBuilder {
	private static int blocksToDisplay = 10;

	private static char blockChar = StringEscapeUtils.unescapeJava("█").charAt(0);

	private static String completedColor = ChatColor.GREEN.toString();

	private static String uncompletedColor = ChatColor.GRAY.toString();

	public static String build(double percentage) {
		String[] blocks = new String[blocksToDisplay];
		Arrays.fill(blocks, uncompletedColor + blockChar);
		if (percentage > 100.0D)
			percentage = 100.0D;
		for (int i = 0; i < percentage / 10.0D; i++)
			blocks[i] = completedColor + blockChar;
		return StringUtils.join(blocks);
	}

	public static double percentage(int value, int goal) {
		return (value > goal) ? 100.0D : (value / goal * 100.0D);
	}

}
