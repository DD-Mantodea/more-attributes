package org.mantodea.more_attributes.utils;

import net.minecraft.world.entity.EquipmentSlot;
import top.theillusivec4.curios.common.data.CuriosSlotManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotUtils {
    public static List<String> Slots;

    public static void getSlots() {
        Slots = new ArrayList<>(Arrays.stream(EquipmentSlot.values()).map(s -> "minecraft:" + s.getName()).toList());

        if(ModUtils.checkModLoaded("curios"))
        {
            var keys = CuriosSlotManager.CLIENT.getSlots().keySet().stream().map(s -> "curios:" + s).toList();
            Slots.addAll(keys);
        }
    }
}
