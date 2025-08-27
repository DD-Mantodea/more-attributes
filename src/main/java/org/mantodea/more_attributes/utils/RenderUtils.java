package org.mantodea.more_attributes.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

public class RenderUtils {
    public static void renderBorderScaleText(GuiGraphics graphics, Font font, MutableComponent component, int x, int y, int textColor, int borderColor, float scale)
    {
        PoseStack stack = graphics.pose();

        stack.pushPose();

        stack.translate(0.5, 0, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);
        stack.translate(-0.5, 0, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);
        stack.translate(0, 0.5, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);
        stack.translate(0, -0.5, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);

        stack.translate(0.5, 0.5, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);
        stack.translate(-0.5, 0.5, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);
        stack.translate(0.5, -0.5, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);
        stack.translate(-0.5, -0.5, 0);
        renderScaleText(graphics, font, component, x, y, borderColor, scale, false);

        renderScaleText(graphics, font, component, x, y, textColor, scale, false);
        stack.popPose();
    }

    public static void renderScaleText(GuiGraphics graphics, Font font, MutableComponent component, int x, int y, int textColor, float scale, boolean newPose)
    {
        PoseStack stack = graphics.pose();

        if(newPose) stack.pushPose();

        stack.translate(x + font.width(component) * 0.5, y + font.lineHeight * 0.5, 0);
        stack.scale(scale, scale, 1);
        stack.translate(-x - font.width(component) * 0.5, -y - font.lineHeight * 0.5, 0);
        graphics.drawString(font, component, x, y, textColor, false);

        stack.popPose();
        stack.pushPose();
    }
}
