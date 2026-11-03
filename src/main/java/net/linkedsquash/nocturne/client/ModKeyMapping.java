package net.linkedsquash.nocturne.client;

import net.linkedsquash.nocturne.Nocturne;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

// Subscribe on the client mod event bus so Minecraft registers the sweep key with its controls.
@EventBusSubscriber(modid = Nocturne.MOD_ID, value = Dist.CLIENT)
public class ModKeyMapping {

    public static final KeyMapping SWEEP_KEY = new KeyMapping(
            "key.nocturne.sweep_key",
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            "key.categories.nocturne"
    );
        @SubscribeEvent
        public static void register(RegisterKeyMappingsEvent event) {
            event.register(SWEEP_KEY);
        }

}

