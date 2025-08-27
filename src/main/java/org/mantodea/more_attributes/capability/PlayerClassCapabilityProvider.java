package org.mantodea.more_attributes.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mantodea.more_attributes.MoreAttributes;

public class PlayerClassCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private IPlayerClassCapability playerClass = null;

    private final LazyOptional<IPlayerClassCapability> optional = LazyOptional.of(this::getOrCreateCapability);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return MoreAttributes.PLAYER_CLASS.orEmpty(cap, optional);
    }

    IPlayerClassCapability getOrCreateCapability() {
        if (playerClass == null) {
            playerClass = new PlayerClassCapability();
        }
        return playerClass;
    }

    @Override
    public CompoundTag serializeNBT() {
        return getOrCreateCapability().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getOrCreateCapability().deserializeNBT(nbt);
    }
}
