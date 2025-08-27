package org.mantodea.more_attributes.mixins.vanilla;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.mantodea.more_attributes.attributes.DetailAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float hurt(float damage, DamageSource source) {
        if (damage < 0) {
            return damage;
        }

        if (source.getEntity() instanceof Player player) {
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                AttributeInstance instance = player.getAttribute(DetailAttributes.RangedDamage);

                if(instance == null) return damage;

                return (float) instance.getValue() * damage;
            }
        }

        return damage;
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float heal(float amount) {
        if (amount < 0) {
            return amount;
        }

        if (((LivingEntity) (Object) this) instanceof Player player) {
            AttributeInstance instance = player.getAttribute(DetailAttributes.LifeRegeneration);

            if(instance == null) return amount;

            return (float) instance.getValue() * amount;
        }
        return amount;
    }

    @Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
    private void getJumpPower(CallbackInfoReturnable<Float> cir) {
        if (((LivingEntity) (Object) this) instanceof Player player) {
            AttributeInstance instance = player.getAttribute(DetailAttributes.JumpForce);

            if(instance == null) return;

            cir.setReturnValue((float) instance.getValue() * cir.getReturnValueF());
        }
    }

    @ModifyVariable(method = "calculateFallDamage", at = @At("STORE"), ordinal = 2)
    private float calculateFallDamage(float reduction) {
        if (((LivingEntity) (Object) this) instanceof Player player) {
            AttributeInstance instance = player.getAttribute(DetailAttributes.JumpForce);

            if(instance == null) return reduction;

            reduction += (float) (instance.getValue() - 1.0f) * 10.0f;
        }
        return reduction;
    }
}
