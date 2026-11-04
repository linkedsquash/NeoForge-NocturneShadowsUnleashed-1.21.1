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
    private static final double DAMPING = 0.94; // Retains visible follow-through while settling oscillation faster.

    private final WhipEntity whip;
    private final List<Vec3> currentPositions = new ArrayList<>(SEGMENTS + 1);
    private final List<Vec3> previousPositions = new ArrayList<>(SEGMENTS + 1);
    // The rope pays out as the tip travels and reels in while the tip returns.
    private Vec3 lastTipPosition;
    private double deployedLength;

    public WhipPhysics(WhipEntity whip) {
        this.whip = whip;

        Vec3 start = getHandPosition();
        Vec3 end = whip.position();
        this.lastTipPosition = end;
        this.deployedLength = Math.min(whip.getSweepReach(), start.distanceTo(end));

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
        double endpointDistance = start.distanceTo(end);
        double tipTravel = lastTipPosition.distanceTo(end);

        // Track rope paid out independently from straight-line distance so it can sag while slack.
        if (whip.getCurrentState() == WhipEntity.WhipEntityState.RETRACTING) {
            deployedLength = Math.max(endpointDistance, deployedLength - tipTravel);
        } else if (whip.getCurrentState() != WhipEntity.WhipEntityState.DROPPING) {
            deployedLength = Math.max(
                    endpointDistance,
                    Math.min(whip.getSweepReach(), deployedLength + tipTravel)
            );
        } else {
            // A falling tip still has rope deployed, so preserve its slack until it begins retracting.
            deployedLength = Math.max(endpointDistance, deployedLength);
        }
        lastTipPosition = end;

        // Keep the endpoints attached to the player and whip.
        currentPositions.set(0, start);
        currentPositions.set(SEGMENTS, end);

        // Verlet integration: new position = current + damped velocity + acceleration.
        // Gravity scales with slack, so a taut rope stays straight while excess deployed length sags.
        double slack = Math.max(0.0, deployedLength - endpointDistance);
        double gravityScale = deployedLength < 1.0E-8 ? 0.0 : Math.min(1.0, slack / deployedLength);
        double restLength = Math.max(endpointDistance, deployedLength) / SEGMENTS;

        for (int i = 1; i < SEGMENTS; i++) {
            Vec3 current = currentPositions.get(i);
            Vec3 previous = previousPositions.get(i);
            Vec3 velocity = current.subtract(previous).scale(DAMPING);

            previousPositions.set(i, current);
            currentPositions.set(i, current.add(velocity).add(0, -GRAVITY * gravityScale, 0));
        }

        // Repeatedly enforce the deployed rope length to stiffen the silhouette without removing all lag.
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
