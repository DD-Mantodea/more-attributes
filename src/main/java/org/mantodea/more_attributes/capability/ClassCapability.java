package org.mantodea.more_attributes.capability;

import net.minecraft.nbt.CompoundTag;
import org.mantodea.more_attributes.datas.ClassData;

import java.util.HashMap;
import java.util.Map;

public class ClassCapability implements IClassCapability {

    private String className;

    private Map<String, Integer> attributes;

    @Override
    public String getClassName() {
        return className;
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
