package org.mantodea.more_attributes.datas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.utils.AttributeUtils;
import org.mantodea.more_attributes.utils.ModUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailLoader extends SimpleJsonResourceReloadListener {
    public static Gson GSON = new GsonBuilder().create();

    public static List<DetailData> Details = new ArrayList<>();

    public DetailLoader() {
        super(GSON, "details");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Details.clear();
        for (JsonElement jsonElement : map.values()) {
            DetailData data = GSON.fromJson(jsonElement, DetailData.class);

            if (data == null || !ModUtils.checkCondition(data.displayCondition))
                continue;

            if (!data.mod.equals("more_attributes"))
            {
                var attribute = ForgeRegistries.ATTRIBUTES.getEntries().stream().filter(
                    entry -> {
                        ResourceLocation location = entry.getKey().location();
                        return location.getNamespace().equals(data.mod) && location.getPath().contains(data.name);
                    }
                ).findFirst().orElse(null);

                if (attribute == null)
                    continue;

                AttributeUtils.CustomDetailAttributes.put(data.mod + ":" + data.name, attribute.getValue());
            }
            else if (!ForgeRegistries.ATTRIBUTES.containsKey(ResourceLocation.fromNamespaceAndPath(data.mod, data.name)))
            {
                MoreAttributes.LOGGER.error("Couldn't find attribute " + data.name);
                continue;
            }

            Details.add(data);
        }

        AttributeUtils.AllDetailAttributes.clear();

        AttributeUtils.AllDetailAttributes.putAll(AttributeUtils.MyDetailAttributes);

        AttributeUtils.AllDetailAttributes.putAll(AttributeUtils.CustomDetailAttributes);

        AttributeUtils.AllAttributes.clear();

        AttributeUtils.AllAttributes.putAll(AttributeUtils.AllDetailAttributes);
    }
}
