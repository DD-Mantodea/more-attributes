package org.mantodea.more_attributes.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.datas.ClassData;
import org.mantodea.more_attributes.datas.ClassLoader;

import java.util.List;

public class ClassUtils {
    public static List<MutableComponent> getClassDescription(String className, Font font) {
        String key = LangUtils.getClassDescriptionKey(className);

        MutableComponent c = Component.translatable(key);

        String description = c.getString();

        List<String> lines = StringUtils.splitStringByWidth(description, 150, font);

        Style sung = Style.EMPTY.withFont(new ResourceLocation("more_attributes:chusung"));

        return lines.stream().map(str -> Component.literal(str).withStyle(sung)).toList();
    }

    public static ClassData getClassData(String className) {
        return ClassLoader.Classes.stream().filter(c -> c.name.equals(className)).findFirst().orElse(null);
    }

    public static boolean hasSelectClass(Player player) {
        var cap = player.getCapability(MoreAttributes.CLASS_CAPABILITY).resolve().orElse(null);

        if (cap != null) {
            return !cap.getClassName().isEmpty();
        }

        return false;
    }

    public static String getPlayerClass(Player player) {
        var cap = player.getCapability(MoreAttributes.CLASS_CAPABILITY).resolve().orElse(null);

        if (cap != null) {
            return cap.getClassName();
        }

        return null;
    }

    public static void setPlayerClass(Player player, String className) {
        var data = className.isEmpty() ? new ClassData() : ClassLoader.Classes.stream().filter(c -> c.name.equals(className)).findFirst().orElse(null);

        if (data == null) {
            return;
        }

        player.getCapability(MoreAttributes.CLASS_CAPABILITY).resolve().ifPresent(cap -> cap.setClass(data));
    }
}
