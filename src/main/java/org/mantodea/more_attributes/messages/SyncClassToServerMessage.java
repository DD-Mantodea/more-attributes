package org.mantodea.more_attributes.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.mantodea.more_attributes.datas.ClassData;
import org.mantodea.more_attributes.utils.ClassUtils;
import org.mantodea.more_attributes.utils.ModifierUtils;

import java.util.function.Supplier;

public record SyncClassToServerMessage(ClassData data) {

    public SyncClassToServerMessage(FriendlyByteBuf buf) {
        this(getData(buf));
    }

    public static ClassData getData(FriendlyByteBuf buf) {
        var classData = new ClassData();

        classData.name = buf.readUtf();

        var attributeSize = buf.readInt();

        for (int i = 0; i < attributeSize; i++) {
            var attr = buf.readUtf();

            var level = buf.readInt();

            classData.attributes.put(attr, level);
        }

        var startItemSize = buf.readInt();

        for (int i = 0; i < startItemSize; i++) {
            var item = buf.readUtf();

            var stack = buf.readInt();

            classData.startItems.put(item, stack);
        }

        return classData;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(data.name);

        buf.writeInt(data.attributes.size());

        for (var entry : data.attributes.entrySet()) {
            buf.writeUtf(entry.getKey());

            buf.writeInt(entry.getValue());
        }

        buf.writeInt(data.startItems.size());

        for (var entry : data.startItems.entrySet()) {
            buf.writeUtf(entry.getKey());

            buf.writeInt(entry.getValue());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            if (player == null || data == null) return;

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

        ctx.setPacketHandled(true);
    }
}
