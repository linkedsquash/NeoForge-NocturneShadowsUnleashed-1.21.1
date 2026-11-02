package net.linkedsquash.nocturneSU.client;


import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.client.renderer.WhipEntityRenderer;
import net.linkedsquash.nocturneSU.world.entity.ModEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = NocturneSU.MOD_ID)
public class ClientModEvents {
    // Forge calls this method when it is time to connect entity types to their client renderers.
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Tell Minecraft to draw whip tip entities using WhipEntityRenderer.
        event.registerEntityRenderer(ModEntities.WHIP_TIPS.get(), WhipEntityRenderer::new);
    }

}
