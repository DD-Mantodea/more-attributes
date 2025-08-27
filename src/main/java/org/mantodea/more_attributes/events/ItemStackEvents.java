package org.mantodea.more_attributes.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.events.custom.ItemStackCreatedEvent;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemStackEvents {
    @SubscribeEvent
    public static void onItemStackCreated(ItemStackCreatedEvent event) {

    }
}
