package org.mantodea.more_attributes.utils;

import org.mantodea.more_attributes.MoreAttributes;

public class LangUtils {
    public static String getClassNameKey(String className)
    {
        return String.format("%s.class.%s", MoreAttributes.MODID, className);
    }

    public static String getClassDescriptionKey(String className)
    {
        return String.format("%s.class.description.%s", MoreAttributes.MODID, className);
    }

    public static String getAttributeNameKey(String attributeName)
    {
        return String.format("%s.attribute.%s", MoreAttributes.MODID, attributeName);
    }

    public static String getDetailNameKey(String mod, String detailName, String type)
    {
        return String.format("%s.detail.%s.%s.%s", MoreAttributes.MODID, mod, detailName, type);
    }

    public static String getVanillaSlotKey(String slotName)
    {
        return String.format("%s.slot.vanilla.%s", MoreAttributes.MODID, slotName);
    }

    public static String getCuriosSlotKey(String slotName)
    {
        return String.format("%s.slot.curios.%s", MoreAttributes.MODID, slotName);
    }
}
