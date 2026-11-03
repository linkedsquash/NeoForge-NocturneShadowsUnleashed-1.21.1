package net.linkedsquash.nocturne.client;

import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.client.models.WhipModel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Nocturne.MOD_ID)
public final class ModModelLayers {
    private ModModelLayers() {
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WhipModel.LAYER_LOCATION, WhipModel::createBodyLayer);
    }
}
