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
import org.mantodea.more_attributes.utils.ModUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeLoader extends SimpleJsonResourceReloadListener {
    public static Gson GSON = new GsonBuilder().create();

    public static List<AttributeData> Attributes = new ArrayList<>();

    public AttributeLoader() {
        super(GSON, "attributes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Attributes.clear();
        for(JsonElement jsonElement : map.values()) {

            AttributeData data = GSON.fromJson(jsonElement, AttributeData.class);

            if(data == null || !ModUtils.checkCondition(data.displayCondition))
                continue;

            for (var modifier : data.modifiers)
            {
                if(!ForgeRegistries.ATTRIBUTES.containsKey(ResourceLocation.fromNamespaceAndPath(modifier.mod, modifier.attribute)))
                    MoreAttributes.LOGGER.error("Couldn't find detail attribute " + modifier.attribute);
            }

            Attributes.add(data);
        }
    }
}
