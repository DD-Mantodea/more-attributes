package org.mantodea.more_attributes.compats.kubejs.bindings;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.mantodea.more_attributes.utils.ItemUtils;
import org.mantodea.more_attributes.utils.LevelUtils;

import java.util.Objects;

public class MoreAttributesBinding {
    public void upgrade(Player player, String attributeName, int level) {
        LevelUtils.upgrade(player, attributeName, level);
    }

    public int getLevel(Player player, String attributeName) {
        return LevelUtils.getLevel(player, attributeName);
    }

    public int getItemWeight(String item) {
        return ItemUtils.getWeight(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(item)))));
    }

    public void setItemWeight(String item, int weight) {
        ItemUtils.ItemWeights.put(item, weight);
    }
}
