package org.mantodea.more_attributes.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.mantodea.more_attributes.utils.ClassUtils;

import java.util.function.Supplier;

public record ResetClassMessage() {

    public ResetClassMessage(FriendlyByteBuf buf) {
        this();
    }

    public void encode(FriendlyByteBuf buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ClassUtils.setPlayerClass(Minecraft.getInstance().player, "");
        });
    }
}
