package org.mantodea.more_attributes.events;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.capability.ClassCapabilityProvider;
import org.mantodea.more_attributes.datas.*;
import org.mantodea.more_attributes.datas.ClassLoader;
import org.mantodea.more_attributes.messages.AttributesChannel;
import org.mantodea.more_attributes.messages.SyncDataMessage;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        Player p = event.getEntity();

        if(p instanceof ServerPlayer serverPlayer)
        {
            CompoundTag nbt = serverPlayer.getPersistentData();

            if(!nbt.contains("more_attributes:class"))
                nbt.putString("more_attributes:class", "");

            Gson gson = new Gson();

            JsonArray classes = gson.toJsonTree(ClassLoader.Classes).getAsJsonArray();

            JsonArray attributes = gson.toJsonTree(AttributeLoader.Attributes).getAsJsonArray();

            JsonArray details = gson.toJsonTree(DetailLoader.Details).getAsJsonArray();

            JsonArray itemModifiers = gson.toJsonTree(ItemModifierLoader.Modifiers).getAsJsonArray();

            JsonArray array = new JsonArray();

            array.add(nbt.getString("more_attributes:class"));
            array.add(classes);
            array.add(attributes);
            array.add(details);
            array.add(itemModifiers);

            AttributesChannel.sendToClient(new SyncDataMessage(array), serverPlayer);
        }
    }
}
