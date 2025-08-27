package org.mantodea.more_attributes.messages;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.mantodea.more_attributes.MoreAttributes;

public class AttributesChannel {
    private static String ProtocolVersion = "1";

    public static SimpleChannel Channel = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MoreAttributes.MODID, "more_attributes"),
        () -> ProtocolVersion,
        ProtocolVersion::equals,
        ProtocolVersion::equals
    );

    public static void RegisterMessages()
    {
        Channel.messageBuilder(SyncDataMessage.class, 1, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncDataMessage::new)
            .encoder(SyncDataMessage::encode)
            .consumerMainThread(SyncDataMessage::handle)
            .add();
        Channel.messageBuilder(SelectClassMessage.class, 2, NetworkDirection.PLAY_TO_SERVER)
            .decoder(SelectClassMessage::new)
            .encoder(SelectClassMessage::encode)
            .consumerMainThread(SelectClassMessage::handle)
            .add();
        Channel.messageBuilder(ResetClassMessage.class, 3, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(ResetClassMessage::new)
            .encoder(ResetClassMessage::encode)
            .consumerMainThread(ResetClassMessage::handle)
            .add();
    }

    public static <MSG> void sendToServer(MSG msg) {
        Channel.sendToServer(msg);
    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayer serverPlayer) {
        Channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }
}
