package org.mantodea.more_attributes.mixins.vanilla;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.mantodea.more_attributes.events.custom.ItemStackCreatedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {
    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    public void construct(ItemLike p_41604_, int p_41605_, CompoundTag p_41606_, CallbackInfo ci) {
        ItemStackCreatedEvent event = new ItemStackCreatedEvent((ItemStack) (Object) this);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
