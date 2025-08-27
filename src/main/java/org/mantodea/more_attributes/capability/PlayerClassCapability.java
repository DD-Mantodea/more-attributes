package org.mantodea.more_attributes.capability;

import net.minecraft.nbt.CompoundTag;
import org.mantodea.more_attributes.datas.AttributeLoader;
import org.mantodea.more_attributes.datas.ClassData;

import java.util.HashMap;
import java.util.Map;

public class PlayerClassCapability implements IPlayerClassCapability {

    private String className = "";

    private Map<String, Integer> attributes = new HashMap<>();

    public PlayerClassCapability() {
        for (var attribute : AttributeLoader.Attributes) {
            attributes.put(attribute.name, 0);
        }
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public ClassData getClassData() {
        var data = new ClassData();

        data.name = className;

        data.attributes = new HashMap<>(attributes);

        return data;
    }

    @Override
    public void setClass(ClassData data) {
        className = data.name;

        attributes = data.attributes;
    }

    @Override
    public void upgrade(String attributeName, int level) {
        if (attributes.containsKey(attributeName)) {
            attributes.put(attributeName, attributes.get(attributeName) + level);
        }
    }

    @Override
    public Map<String, Integer> getAttributes() {
        return new HashMap<>(attributes);
    }

    @Override
    public int getLevel(String attributeName) {
        if (attributes.containsKey(attributeName)) {
            return attributes.get(attributeName);
        }

        return 0;
    }

    @Override
    public void setLevel(String attributeName, int level) {
        if (attributes.containsKey(attributeName)) {
            attributes.put(attributeName, level);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("className", className);

        for (var entry : attributes.entrySet()) {
            tag.putInt(entry.getKey(), entry.getValue());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        className = nbt.getString("className");

        for (var key : nbt.getAllKeys()) {
            if (key.equals("className")) {
                continue;
            }

            attributes.put(key, nbt.getInt(key));
        }
    }
}
