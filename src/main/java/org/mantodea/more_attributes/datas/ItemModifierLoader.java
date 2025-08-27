package org.mantodea.more_attributes.datas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.mantodea.more_attributes.utils.ModUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemModifierLoader extends SimpleJsonResourceReloadListener {
    public static Gson GSON = new GsonBuilder().create();

    public static List<ItemModifierData> Modifiers = new ArrayList<>();

    public ItemModifierLoader() {
        super(GSON, "modifiers");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Modifiers.clear();
        for(JsonElement jsonElement : map.values()) {
            ItemModifierData data = GSON.fromJson(jsonElement, ItemModifierData.class);

            if(data == null || !ModUtils.checkCondition(data.displayCondition()))
                continue;

            data.modifiers = data.modifiers.stream().filter(m -> ModUtils.checkCondition(m.displayCondition())).toList();

            Modifiers.add(data);
        }
    }
}
