package org.mantodea.more_attributes.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.mantodea.more_attributes.datas.*;
import org.mantodea.more_attributes.datas.ClassLoader;
import org.mantodea.more_attributes.utils.ClassUtils;
import org.mantodea.more_attributes.utils.SlotUtils;

import java.util.List;
import java.util.function.Supplier;

public record SyncDataMessage(JsonArray data) {

    public SyncDataMessage(FriendlyByteBuf buf) {
        this(JsonParser.parseString(buf.readUtf()).getAsJsonArray());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(data.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;

            if(level == null) return;

            Gson gson = new GsonBuilder().create();

            ClassUtils.setPlayerClass(mc.player, data.get(0).getAsString());

            ClassLoader.Classes = gson.fromJson(data.get(1), new TypeToken<List<ClassData>>() {}.getType());
            AttributeLoader.Attributes = gson.fromJson(data.get(2), new TypeToken<List<AttributeData>>() {}.getType());
            DetailLoader.Details = gson.fromJson(data.get(3), new TypeToken<List<DetailData>>() {}.getType());
            ItemModifierLoader.Modifiers = gson.fromJson(data.get(4), new TypeToken<List<ItemModifierData>>() {}.getType());

            SlotUtils.getSlots();
        });
    }
}
