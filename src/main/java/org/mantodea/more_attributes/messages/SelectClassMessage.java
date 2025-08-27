package org.mantodea.more_attributes.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.mantodea.more_attributes.datas.ClassData;
import org.mantodea.more_attributes.utils.AttributeUtils;
import org.mantodea.more_attributes.utils.ClassUtils;
import org.mantodea.more_attributes.utils.ModifierUtils;

import java.util.Objects;
import java.util.function.Supplier;

public record SelectClassMessage(String className) {

    public SelectClassMessage(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(className);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ClassData data = ClassUtils.getClassData(className);

            ServerPlayer player = ctx.getSender();

            if (player == null) return;

            ClassUtils.setPlayerClass(player, data.name);

            ModifierUtils.DetailModifiers.Level.rebuildModifiers(player);

            for (var entry : data.startItems.entrySet()) {
                var item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(entry.getKey()));

                if (item != null) {
                    var itemStack = new ItemStack(item, entry.getValue());

                    ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                }
            }
        });
    }
}
