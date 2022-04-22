package net.frozenorb.foxtrot.util;

import java.awt.Color;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.md_5.bungee.api.ChatColor;

public class GradientChatTransformer implements UnaryOperator<String> {

    private final Color left;


    private final Color right;

    public GradientChatTransformer(Color leftColor, Color rightColor) {
        this.left = Objects.requireNonNull(leftColor);
        this.right = Objects.requireNonNull(rightColor);
    }

    private int getChannelValue(float percent, Function<Color, Integer> channelGetter) {
        int leftChannel = channelGetter.apply(left);
        int rightChannel = channelGetter.apply(right);

        float distance = percent * (rightChannel - leftChannel);

        return (int) (leftChannel + distance);
    }

    @Override
    public String apply(String message) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            // Check how far the character is into the string.
            float percent = (float) i / (message.length());

            Color charColor = new Color(
                    getChannelValue(percent, Color::getRed),
                    getChannelValue(percent, Color::getGreen),
                    getChannelValue(percent, Color::getBlue)
            );


            result
                    .append(ChatColor.of(charColor))
                    .append(message.charAt(i));
        }

        return result.toString();
    }
}
