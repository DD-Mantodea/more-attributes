package org.mantodea.more_attributes.utils;

import net.minecraft.resources.ResourceLocation;
import org.mantodea.more_attributes.MoreAttributes;

public class ResourceLocationUtils {
    public static class GUI {
        public static final ResourceLocation Background = new ResourceLocation(MoreAttributes.MODID, "textures/gui/background.png");

        public static final ResourceLocation ClassIconBox = new ResourceLocation(MoreAttributes.MODID, "textures/gui/icon_box.png");

        public static final ResourceLocation Select = new ResourceLocation(MoreAttributes.MODID, "textures/gui/select.png");

        public static final ResourceLocation SelectHover = new ResourceLocation(MoreAttributes.MODID, "textures/gui/select_hover.png");

        public static final ResourceLocation PreviousPage = new ResourceLocation(MoreAttributes.MODID, "textures/gui/previous_page.png");

        public static final ResourceLocation PreviousPageHover = new ResourceLocation(MoreAttributes.MODID, "textures/gui/previous_page_hover.png");

        public static final ResourceLocation NextPage = new ResourceLocation(MoreAttributes.MODID, "textures/gui/next_page.png");

        public static final ResourceLocation NextPageHover = new ResourceLocation(MoreAttributes.MODID, "textures/gui/next_page_hover.png");

        public static ResourceLocation getClassIcon(String className) {
            return new ResourceLocation(MoreAttributes.MODID, String.format("textures/gui/class/%s.png", className));
        }

        public static ResourceLocation getAttributeIcon(String attributeName) {
            return new ResourceLocation(MoreAttributes.MODID, String.format("textures/gui/attribute/%s.png", attributeName));
        }
    }

}
