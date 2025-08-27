package org.mantodea.more_attributes.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.capability.PlayerClassCapabilityProvider;
import org.mantodea.more_attributes.messages.AttributesChannel;
import org.mantodea.more_attributes.messages.SyncClassToClientMessage;
import org.mantodea.more_attributes.utils.ModifierUtils;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
    @SubscribeEvent
    public static void onEquipChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            ModifierUtils.DetailModifiers.Equip.rebuildModifiers(player);
        }
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            ModifierUtils.DetailModifiers.Equip.rebuildModifiers(player);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            ModifierUtils.DetailModifiers.Hands.rebuildModifiers(player);

            ModifierUtils.DetailModifiers.EquipLoad.rebuildModifier(player);
        }
    }

    @SubscribeEvent
    public static void onEnterWorld(PlayerEvent.PlayerLoggedInEvent event) {
        ModifierUtils.DetailModifiers.initialize();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();

        if (entity instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(MoreAttributes.MODID, "class"), new PlayerClassCapabilityProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            var originalPlayer = event.getOriginal();
            originalPlayer.reviveCaps();

            var originalCap = originalPlayer.getCapability(MoreAttributes.PLAYER_CLASS).resolve().orElse(null);

            var cloneCap = event.getEntity().getCapability(MoreAttributes.PLAYER_CLASS).resolve().orElse(null);

            if (originalCap != null && cloneCap != null) {
                cloneCap.deserializeNBT(originalCap.serializeNBT());

                var data = cloneCap.getClassData();

                if (data != null && event.getEntity() instanceof ServerPlayer serverPlayer) {
                    AttributesChannel.sendToClient(new SyncClassToClientMessage(data), serverPlayer);
                }
            }

            originalPlayer.invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        event.getEntity().getCapability(MoreAttributes.PLAYER_CLASS).ifPresent(cap -> {

        });
    }
}
