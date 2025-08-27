package org.mantodea.more_attributes.mixins.vanilla;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.mantodea.more_attributes.attributes.DetailAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float causeFoodExhaustion(final float exhaustion) {
        if(exhaustion < 0) return exhaustion;

        Player player = (Player)(Object)this;

        AttributeInstance instance = player.getAttribute(DetailAttributes.HungerConsumption);

        if(instance == null) return exhaustion;

        return (float) instance.getValue() * exhaustion;
    }
}
