package org.mantodea.more_attributes.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.attributes.DetailAttributes;

import java.util.Random;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    @SubscribeEvent
    public static void livingEntityDamage(LivingDamageEvent event) {
        Entity sourceEntity = event.getSource().getEntity();

        Entity entity = event.getEntity();

        if(sourceEntity instanceof Player player) {
            AttributeInstance meleeDamage = player.getAttribute(DetailAttributes.MeleeDamage);

            if (meleeDamage != null) {
                event.setAmount(event.getAmount() * (float) meleeDamage.getValue());
            }

            AttributeInstance criticalDamage = player.getAttribute(DetailAttributes.CriticalDamage);

            AttributeInstance criticalChance = player.getAttribute(DetailAttributes.CriticalChance);

            if(criticalDamage != null && criticalChance != null) {
                if (new Random().nextDouble() < criticalChance.getValue())
                {
                    event.setAmount(event.getAmount() * (float) criticalDamage.getValue());

                    int particleCount = (int) (15 * entity.getBbWidth() * entity.getBbWidth());

                    ServerLevel server = (ServerLevel) player.level();

                    server.sendParticles(
                        (ServerPlayer) player, ParticleTypes.CRIT, true,
                        entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                        particleCount,
                        entity.getBbWidth() / 2, entity.getBbHeight() / 2, entity.getBbWidth() / 2,
                        0.2
                    );

                    server.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5F, 1.0F);
                }
            }
        }

        if(entity instanceof Player player) {
            AttributeInstance damageReduction = player.getAttribute(DetailAttributes.DamageReduction);

            if (damageReduction != null) {
                event.setAmount(event.getAmount() * (float) (1 - damageReduction.getValue()));
            }
        }
    }
}
