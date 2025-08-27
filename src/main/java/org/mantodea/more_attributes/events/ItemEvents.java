package org.mantodea.more_attributes.events;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.datas.ItemModifierData;
import org.mantodea.more_attributes.utils.ModifierUtils;

import java.util.List;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEvents {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        ResourceLocation location = ForgeRegistries.ITEMS.getKey(stack.getItem());

        ItemModifierData data = ModifierUtils.getItemModifierData(location);

        if(data != null) {
            List<Component> tooltips = ModifierUtils.getComponentsForItem(data.modifiers);

            event.getToolTip().addAll(tooltips);
        }
    }
}
