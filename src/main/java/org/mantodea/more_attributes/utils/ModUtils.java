package org.mantodea.more_attributes.utils;

import net.minecraftforge.fml.ModList;

import java.util.List;

public class ModUtils {
    public static boolean checkCondition(List<String> mods)
    {
        if(mods.isEmpty())
            return true;
        for (var mod : mods)
        {
            if(ModList.get().isLoaded(mod))
                return true;
        }
        return false;
    }

    public static boolean checkModLoaded(String mod)
    {
        return ModList.get().isLoaded(mod);
    }
}
