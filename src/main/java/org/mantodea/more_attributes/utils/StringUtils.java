package org.mantodea.more_attributes.utils;

import net.minecraft.client.gui.Font;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<String> splitStringByWidth(String input, int width, Font font)
    {
        char[] chars = input.toCharArray();

        List<String> strings = new ArrayList<>();

        StringBuilder line = new StringBuilder();

        int start = 0;

        int index = 0;

        while (index < chars.length - 1)
        {
            while (font.width(line.toString()) < width)
            {
                line.append(chars[index]);
                index++;
                if(index == chars.length)
                    break;
            }
            index--;

            strings.add(input.substring(start, index));

            start = index;

            line = new StringBuilder();
        }

        return strings;
    }
}
