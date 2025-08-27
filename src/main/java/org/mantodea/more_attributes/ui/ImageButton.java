package org.mantodea.more_attributes.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.mantodea.more_attributes.utils.RenderUtils;

import java.util.function.Supplier;

public class ImageButton extends Button {

    public ImageButton(int x, int y, int width, int height, MutableComponent message, ResourceLocation texture, ResourceLocation hoverTexture) {
        super(x, y, width, height, message, b -> {}, Supplier::get);
        this.texture = texture;
        this.hoverTexture = hoverTexture;
        this.width = width;
        this.height = height;
        this.myOnPress = b -> {};
    }

    protected OnPress myOnPress;

    private ResourceLocation texture;

    private ResourceLocation hoverTexture;

    private int width;

    private int height;

    public void setPress(OnPress onPress) {
        myOnPress = onPress;
    }

    @Override
    public void onPress() {
        myOnPress.onPress(this);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        if (!isHovered())
            graphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);
        else
            graphics.blit(hoverTexture, getX(), getY(), 0, 0, width, height, width, height);

        Style sung = Style.EMPTY.withFont(new ResourceLocation("more_attributes:chusung"));

        MutableComponent component = ((MutableComponent) getMessage()).withStyle(sung);

        int x = getX() + (width - font.width(component)) / 2;
        int y = getY() + (height - font.lineHeight) / 2 + 3;

        if (!component.getVisualOrderText().toString().isEmpty()) {
            if (isHovered())
                RenderUtils.renderBorderScaleText(graphics, font, component, x, y, 0x603B38, 0xFFEAC2, 1);
            else
                RenderUtils.renderBorderScaleText(graphics, font, component, x, y, 0x603B38, 0xF0DCB7, 1);
        }
    }
}
