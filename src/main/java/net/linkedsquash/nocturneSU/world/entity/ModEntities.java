package net.linkedsquash.nocturneSU.world.entity;


import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.world.entity.projectile.WhipEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    // Hold entity registrations for this mod until Forge loads them.
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NocturneSU.MOD_ID);
    // Register the flying whip tip as a miscellaneous entity, rather than a creature.
    public static final DeferredHolder<EntityType<?>, EntityType<WhipEntity>> WHIP_TIPS = ENTITY_TYPES.register(
            "whip_tips",
            () -> EntityType.Builder.<WhipEntity>of(WhipEntity::new, MobCategory.MISC)
                    // Set the tip's HitBox size to one quarter of a block in each direction.
                    .sized(1F, 1F)
                    // Send updates to clients within four chunks of the entity.
                    .clientTrackingRange(4)
                    // Send position updates every five ticks.
                    .updateInterval(1)
                    .build("whip_tips")
    );

    public static void register(IEventBus eventBus) {
        // Attach entity registration to Forge's startup event bus.
        ENTITY_TYPES.register(eventBus);
    }
}

