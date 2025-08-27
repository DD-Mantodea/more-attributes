package org.mantodea.more_attributes.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.datas.ClassLoader;
import org.mantodea.more_attributes.ui.SelectClassScreen;
import org.mantodea.more_attributes.ui.ShowClassScreen;
import org.mantodea.more_attributes.utils.ClassUtils;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    public static final KeyMapping OpenUI = new KeyMapping(
        "key.more_attributes.open_ui",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_H,
        "key.categories." + MoreAttributes.MODID
    );

    @SubscribeEvent
    public static void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        event.register(OpenUI);
    }

    @Mod.EventBusSubscriber(modid = MoreAttributes.MODID, value = Dist.CLIENT)
    public static class InputEvents {
        @SubscribeEvent
        public static void inputEvent(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null || minecraft.screen instanceof SelectClassScreen || minecraft.screen instanceof ShowClassScreen || ClassLoader.Classes.isEmpty()) return;

            if (!ClassUtils.hasSelectClass(player)) {
                minecraft.setScreen(new SelectClassScreen());
            }

            while(OpenUI.consumeClick())
            {
                Screen screen = ClassUtils.hasSelectClass(player) ? new ShowClassScreen() : new SelectClassScreen();

                minecraft.setScreen(screen);
            }
        }
    }
}
