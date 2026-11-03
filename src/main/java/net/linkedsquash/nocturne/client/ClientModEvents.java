package net.linkedsquash.nocturne.client;


import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.client.renderer.WhipEntityRenderer;
import net.linkedsquash.nocturne.client.simulations.ClientWhipPhysicsManager;
import net.linkedsquash.nocturne.client.simulations.WhipPhysics;
import net.linkedsquash.nocturne.world.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Nocturne.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {
    // Forge calls this method when it is time to connect entity types to their client renderers.
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Tell Minecraft to draw whip tip entities using WhipEntityRenderer.
        event.registerEntityRenderer(ModEntities.WHIP_TIPS.get(), WhipEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientWhipPhysicsManager.tick(Minecraft.getInstance().level);

    }

}
