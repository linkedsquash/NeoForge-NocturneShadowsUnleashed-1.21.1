package net.linkedsquash.nocturne.client.simulations;

import net.linkedsquash.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public final class ClientWhipPhysicsManager {
    private static final Map<UUID, WhipPhysics> SIMULATIONS = new HashMap<>();

    private ClientWhipPhysicsManager() {
        // This class is a manager; it doesn't need instances of its own.
    }
    public ClientLevel level = Minecraft.getInstance().level;

    @OnlyIn(Dist.CLIENT)
    public static void tick(ClientLevel level) {
        if (level == null) {
            return;
        }
        Set<UUID> activeWhips = new HashSet<>();

        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof WhipEntity whip)) continue;

            UUID id = whip.getUUID();
            activeWhips.add(id);

            WhipPhysics physics =
                    SIMULATIONS.computeIfAbsent(id, ignored -> new WhipPhysics(whip));
            physics.tick();
        }

        // Drop simulations for whip entities that are no longer in this client level.
        SIMULATIONS.keySet().retainAll(activeWhips);
    }

    public static WhipPhysics get(WhipEntity whip) {
        return SIMULATIONS.computeIfAbsent(
                whip.getUUID(),
                ignored -> new WhipPhysics(whip)
        );
    }
}
