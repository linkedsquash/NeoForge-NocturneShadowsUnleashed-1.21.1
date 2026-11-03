package net.linkedsquash.nocturne.client.simulations;

import net.linkedsquash.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class WhipPhysics {
    private static final int SEGMENTS = 16;
    private static final int CONSTRAINT_ITERATIONS = 12;
    private static final double GRAVITY = 0.015;
    private static final double DAMPING = 0.96;

    private final WhipEntity whip;
    private final List<Vec3> currentPositions = new ArrayList<>(SEGMENTS + 1);
    private final List<Vec3> previousPositions = new ArrayList<>(SEGMENTS + 1);

    public WhipPhysics(WhipEntity whip) {
        this.whip = whip;

        Vec3 start = getHandPosition();
        Vec3 end = whip.position();

        for (int i = 0; i <= SEGMENTS; i++) {
            double t = i / (double) SEGMENTS;
            Vec3 point = start.lerp(end, t);

            currentPositions.add(point);
            previousPositions.add(point);
        }
    }

    public void tick() {
        if (currentPositions.size() != SEGMENTS + 1) return;

        Vec3 start = getHandPosition();
        Vec3 end = whip.position();

        // Keep the endpoints attached to the player and whip.
        currentPositions.set(0, start);
        currentPositions.set(SEGMENTS, end);

        // Verlet integration: new position = current + damped velocity + acceleration.
        for (int i = 1; i < SEGMENTS; i++) {
            Vec3 current = currentPositions.get(i);
            Vec3 previous = previousPositions.get(i);
            Vec3 velocity = current.subtract(previous).scale(DAMPING);

            previousPositions.set(i, current);
            currentPositions.set(i, current.add(velocity).add(0, -GRAVITY, 0));
        }

        // Keep endpoints fixed while enforcing segment lengths.
        double restLength = start.distanceTo(end) / SEGMENTS;

        for (int iteration = 0; iteration < CONSTRAINT_ITERATIONS; iteration++) {
            currentPositions.set(0, start);
            currentPositions.set(SEGMENTS, end);

            for (int i = 0; i < SEGMENTS; i++) {
                Vec3 a = currentPositions.get(i);
                Vec3 b = currentPositions.get(i + 1);
                Vec3 delta = b.subtract(a);
                double distance = delta.length();

                if (distance < 1.0E-8) continue;

                Vec3 correction = delta.scale((distance - restLength) / distance);

                if (i == 0) {
                    // The first endpoint is fixed.
                    currentPositions.set(i + 1, b.subtract(correction));
                } else if (i + 1 == SEGMENTS) {
                    // The last endpoint is fixed.
                    currentPositions.set(i, a.add(correction));
                } else {
                    // Move both free points toward the target spacing.
                    currentPositions.set(i, a.add(correction.scale(0.5)));
                    currentPositions.set(i + 1, b.subtract(correction.scale(0.5)));
                }
            }
        }
    }

    /** Returns an immutable snapshot of the points from the player's hand to the whip tip. */
    public List<Vec3> getPoints() {
        return List.copyOf(currentPositions);
    }

    private Vec3 getHandPosition () {
        Player owner = whip.getPlayerOwner();
        return owner == null ? whip.position() : owner.getRopeHoldPosition(1.0F);
    }
}
