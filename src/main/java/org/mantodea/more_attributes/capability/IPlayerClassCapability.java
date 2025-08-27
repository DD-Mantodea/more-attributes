package org.mantodea.more_attributes.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.mantodea.more_attributes.datas.ClassData;

import java.util.Map;

@AutoRegisterCapability
public interface IPlayerClassCapability extends INBTSerializable<CompoundTag> {

    String getClassName();

    ClassData getClassData();

    void setClass(ClassData data);

    void upgrade(String attributeName, int level);

    Map<String, Integer> getAttributes();

    int getLevel(String attributeName);

    void setLevel(String attributeName, int level);
}
