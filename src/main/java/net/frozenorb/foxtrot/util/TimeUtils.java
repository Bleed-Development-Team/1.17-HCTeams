package net.frozenorb.foxtrot.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {
    private static final ThreadLocal<StringBuilder> mmssBuilder = ThreadLocal.withInitial(StringBuilder::new);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public static String formatIntoHHMMSS(int secs) {
        return formatIntoMMSS(secs);
    }

    public static String formatLongIntoHHMMSS(long secs) {
        int unconvertedSeconds = (int)secs;
        return formatIntoMMSS(unconvertedSeconds);
    }

    public static String formatIntoMMSS(int secs) {
        int seconds = secs % 60;
        long minutesCount = ((secs - seconds) / 60);
        long minutes = minutesCount % 60L;
        long hours = (minutesCount - minutes) / 60L;
        StringBuilder result = mmssBuilder.get();
        result.setLength(0);
        if (hours > 0L) {
            if (hours < 10L)
                result.append("0");
            result.append(hours);
            result.append(":");
        }
        if (minutes < 10L)
            result.append("0");
        result.append(minutes);
        result.append(":");
        if (seconds < 10)
            result.append("0");
        result.append(seconds);
        return result.toString();
    }

    public static String formatLongIntoMMSS(long secs) {
        int unconvertedSeconds = (int)secs;
        return formatIntoMMSS(unconvertedSeconds);
    }

    public static String formatIntoDetailedString(int secs) {
        if (secs == 0)
            return "0 seconds";
        int remainder = secs % 86400;
        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = remainder / 60 - hours * 60;
        int seconds = remainder % 3600 - minutes * 60;
        String fDays = (days > 0) ? (" " + days + " day" + ((days > 1) ? "s" : "")) : "";
        String fHours = (hours > 0) ? (" " + hours + " hour" + ((hours > 1) ? "s" : "")) : "";
        String fMinutes = (minutes > 0) ? (" " + minutes + " minute" + ((minutes > 1) ? "s" : "")) : "";
        String fSeconds = (seconds > 0) ? (" " + seconds + " second" + ((seconds > 1) ? "s" : "")) : "";
        return (fDays + fHours + fMinutes + fSeconds).trim();
    }

    public static String formatLongIntoDetailedString(long secs) {
        int unconvertedSeconds = (int)secs;
        return formatIntoDetailedString(unconvertedSeconds);
    }

    public static String formatIntoCalendarString(Date date) {
        return dateFormat.format(date);
    }

    public static int parseTime(String time) {
        if (time.equals("0") || time.equals(""))
            return 0;
        String[] lifeMatch = { "w", "d", "h", "m", "s" };
        int[] lifeInterval = { 604800, 86400, 3600, 60, 1 };
        int seconds = -1;
        for (int i = 0; i < lifeMatch.length; i++) {
            Matcher matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time);
            while (matcher.find()) {
                if (seconds == -1)
                    seconds = 0;
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }
        }
        if (seconds == -1)
            throw new IllegalArgumentException("Invalid time provided.");
        return seconds;
    }

    public static long parseTimeToLong(String time) {
        return parseTime(time);
    }

    public static int getSecondsBetween(Date a, Date b) {
        return (int)getSecondsBetweenLong(a, b);
    }

    public static long getSecondsBetweenLong(Date a, Date b) {
        long diff = a.getTime() - b.getTime();
        long absDiff = Math.abs(diff);
        return absDiff / 1000L;
    }
}