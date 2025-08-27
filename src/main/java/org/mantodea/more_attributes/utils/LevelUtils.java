package org.mantodea.more_attributes.utils;

import net.minecraft.world.entity.player.Player;
import org.mantodea.more_attributes.MoreAttributes;

public class LevelUtils {
    public static void upgrade(Player player, String attributeName, int level) {
        var cap = player.getCapability(MoreAttributes.CLASS_CAPABILITY).resolve().orElse(null);

        if (cap == null) {
            return;
        }

        cap.upgrade(attributeName, level);

        ModifierUtils.DetailModifiers.Level.rebuildModifiers(player);
    }

    public static int getLevel(Player player, String attributeName) {
        var cap = player.getCapability(MoreAttributes.CLASS_CAPABILITY).resolve().orElse(null);

        if (cap == null) {
            return 0;
        }

        return cap.getLevel(attributeName);
    }

    public static void setLevel(Player player, String attributeName, int level) {
        var cap = player.getCapability(MoreAttributes.CLASS_CAPABILITY).resolve().orElse(null);

        if (cap == null) {
            return;
        }

        cap.setLevel(attributeName, level);

        ModifierUtils.DetailModifiers.Level.rebuildModifiers(player);
    }
}
