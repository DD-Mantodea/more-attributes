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
        Channel.messageBuilder(SyncDataToClientMessage.class, 1, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncDataToClientMessage::new)
            .encoder(SyncDataToClientMessage::encode)
            .consumerMainThread(SyncDataToClientMessage::handle)
            .add();
        Channel.messageBuilder(SyncClassToServerMessage.class, 2, NetworkDirection.PLAY_TO_SERVER)
            .decoder(SyncClassToServerMessage::new)
            .encoder(SyncClassToServerMessage::encode)
            .consumerMainThread(SyncClassToServerMessage::handle)
            .add();
        Channel.messageBuilder(SyncClassToClientMessage.class, 3, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncClassToClientMessage::new)
            .encoder(SyncClassToClientMessage::encode)
            .consumerMainThread(SyncClassToClientMessage::handle)
            .add();
    }

    public static <MSG> void sendToServer(MSG msg) {
        Channel.sendToServer(msg);
    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayer serverPlayer) {
        Channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }
}
