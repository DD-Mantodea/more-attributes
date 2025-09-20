package org.mantodea.more_attributes.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.mantodea.more_attributes.datas.ClassData;
import org.mantodea.more_attributes.utils.ClassUtils;
import org.mantodea.more_attributes.utils.ModifierUtils;

import java.util.function.Supplier;

public record SyncClassToClientMessage(ClassData data) {

    public SyncClassToClientMessage(FriendlyByteBuf buf) {
        this(getData(buf));
    }

    public static ClassData getData(FriendlyByteBuf buf) {
        var classData = new ClassData();

        classData.name = buf.readUtf();

        var size = buf.readInt();

        for (int i = 0; i < size; i++) {
            var attr = buf.readUtf();

            var level = buf.readInt();

            classData.attributes.put(attr, level);
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

        ctx.enqueueWork(() -> handle());

        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handle() {
        Player player = (Player) Minecraft.getInstance().player;

        if (player == null || data == null) return;

        ClassUtils.setPlayerClass(player, data);

        ModifierUtils.DetailModifiers.Level.rebuildModifiers(player);
    }
}
