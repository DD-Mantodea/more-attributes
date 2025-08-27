package org.mantodea.more_attributes.events.custom;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ItemStackCreatedEvent extends Event {

    private final ItemStack itemStack;

    public ItemStackCreatedEvent(ItemStack stack) {
        itemStack = stack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
