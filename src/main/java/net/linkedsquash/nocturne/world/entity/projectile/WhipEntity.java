package net.linkedsquash.nocturne.world.entity.projectile;


import net.linkedsquash.nocturne.util.ModTags;
import net.linkedsquash.nocturne.world.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class WhipEntity extends Projectile {

    // Maximum distance, in blocks, that the whip tip may travel away from where it launched.
    public static final double MAX_REACH = 8; // Maximum distance the tip travels from its launch point.
    // Names used to store the whip's state and launch coordinates in a saved entity.
    private static final String TAG_STATE = "WhipState";
    private static final String TAG_SPAWN_X = "SpawnX";
    private static final String TAG_SPAWN_Y = "SpawnY";
    private static final String TAG_SPAWN_Z = "SpawnZ";

    //checks if the whip has been discarded
    public boolean isDiscarded = false;

    // Synchronize held-sweep mode so client movement and rendering follow the server's attack state.
    private static final EntityDataAccessor<Boolean> DATA_SWEEPING =
            SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.BOOLEAN);

    // Shared key used to synchronise the whip state between the server and client.
    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.INT);

    // Position where this whip tip started, used to measure its travel distance.
    private Vec3 spawnPosition = Vec3.ZERO;
    private static final int SWEEP_HIT_COOLDOWN = 20;
    private static final double SWEEP_MAX_SPEED = 2.5;
    private static final double SWEEP_ACCELERATION = 1.0;
    private static final double SWEEP_ARRIVAL_SPEED_FACTOR = 0.75;
    // Vertical steering is gentler than horizontal steering so aim changes do not make the tip pop up or down.
    private static final double SWEEP_VERTICAL_ARRIVAL_SPEED_FACTOR = 0.2;
    // Applied after movement, preserving the first launch step while bending later flight downward.
    private static final double LASH_GRAVITY = 0.08;
    private static final double AIR_DRAG = 0.99;
    private static final double WATER_DRAG = 0.55;
    // Squared speed cutoff (0.1 blocks/tick) used to let a nearly stopped tip fall and settle.
    private static final double MIN_FLIGHT_MOMENTUM_SQR = 0.01;
    // A held sweep can leave DROPPING once its 8-block aim target moves by more than 0.05 blocks.
    private static final double SWEEP_RESTART_AIM_MOVEMENT_SQR = 0.0025;
    // Successful sweep hits are rate-limited per target, allowing one sweep to keep crossing other entities.
    private final Map<UUID, Long> lastSweepHitTick = new HashMap<>();


    @Nullable
    private Vec3 previousSweepLookAngle;
    @Nullable
    private Vec3 previousSweepTargetPosition;
    private long previousSweepTick;

    public float sweepDamage = 4F;
    public float lashDamage = 8F;


    public double getSweepDamage() {
        return this.sweepDamage;
    }

    public double getLashDamage() {
        return this.lashDamage;
    }


    // Constructor used by Minecraft when loading or creating this entity by type.
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {
        super(entityType, level); // Initialise the inherited Projectile state.
        this.noCulling = false; // Allow Minecraft to cull this entity when it is outside the view.
    }

    public WhipEntityState getCurrentState() {
        int value = this.entityData.get(DATA_STATE);
        // Convert the saved number into one of the named states, such as EXTENDING or RETRACTING.
        WhipEntityState[] states = WhipEntityState.values();
        // Check the number first so an unexpected value cannot cause an array-index error.
        // If it is outside the valid range, use EXTENDING as a safe default.
        return value >= 0 && value < states.length ? states[value] : WhipEntityState.EXTENDING;
    }

    public void setCurrentState(WhipEntityState state) {
        this.entityData.set(DATA_STATE, state.ordinal());
    }

    private boolean isSweepHeld() {
        return this.entityData.get(DATA_SWEEPING);
    }

    public boolean checkRETRACTING() {
        // Return true only when the tip is currently travelling back to its owner.
        return getCurrentState() == WhipEntityState.RETRACTING;
    }

    // Returns the vector from the tip to the owner's eyes; it is not normalised.
    public Vec3 getRetractionDirection() {
        Player player = getPlayerOwner();
        if (player == null) return Vec3.ZERO; // Without a player owner, no return direction is available.
        return player.getEyePosition().subtract(this.position());
    }

    // Gives a retracting tip velocity toward its player's eyes; discards it if no player owns it.
    public void retractWhip() {

        Player player = getPlayerOwner();

        if (player == null) {
            this.discardWhip();
            return;
        }

        if (!checkRETRACTING()) {
            return;
        }

        Vec3 toPlayerDirection = getRetractionDirection();

        // Discard the tip when it is already within one block of the player's eyes.
        if (toPlayerDirection.lengthSqr() < 1.0) {
            this.discardWhip();
            return;
        }
        double distanceToPlayer = toPlayerDirection.length();
        double returnSpeed = Math.min(1.0, distanceToPlayer);
        this.setDeltaMovement(toPlayerDirection.normalize().scale(returnSpeed));
    }

    // Creates a whip tip at the player's eyes and launches it in their look direction.
    public WhipEntity(Player player, Level level) {
        this(ModEntities.WHIP_TIPS.get(), level); // Create the entity using the type-based constructor.
        this.setOwner(player); // Record which player launched this whip tip.
        float playerXRot = player.getXRot(); // Player pitch.
        float playerYRot = player.getYRot(); // Player yaw.
        Vec3 lookDirection = player.getLookAngle(); // The player's normalised viewing direction.
        Vec3 lookPosition = player.getEyePosition().add(lookDirection.scale(0.5)); // Start half a block ahead of the player's eyes.
        this.moveTo( // Set the whip tip's starting position and orientation.
                lookPosition.x,
                lookPosition.y - 0.5,
                lookPosition.z,
                playerYRot,
                playerXRot
        );
        this.spawnPosition = this.position(); // Save the launch point for the reach calculation.
        this.setDeltaMovement(lookDirection.scale(2.5));
        // Launch the tip at 1.5 blocks per tick along the player's look direction.

    }

        @Override
        protected void readAdditionalSaveData (CompoundTag tag){
            // Let the parent projectile restore its own saved data first.
            super.readAdditionalSaveData(tag);
            // Only try to restore the movement state if the save contains it.
            if (tag.contains(TAG_STATE)) {
                try {
                    // Convert the saved state name back into a WhipEntityState value.
                    setCurrentState(WhipEntityState.valueOf(tag.getString(TAG_STATE)));
                } catch (IllegalArgumentException ignored) {
                    // If the saved name is unknown, start in the safe default state instead of failing to load.
                    setCurrentState(WhipEntityState.EXTENDING);
                }
            }
            // Restore the launch position only when all three coordinates are present in the save.
            if (tag.contains(TAG_SPAWN_X) && tag.contains(TAG_SPAWN_Y) && tag.contains(TAG_SPAWN_Z)) {
                // Combine the saved X, Y, and Z numbers into one 3D position.
                spawnPosition = new Vec3(tag.getDouble(TAG_SPAWN_X), tag.getDouble(TAG_SPAWN_Y), tag.getDouble(TAG_SPAWN_Z));
            } else {
                // Older or incomplete saves have no launch position, so use the entity's current position.
                spawnPosition = position();
            }
        }

        public static WhipEntity findActive (Player player){ // Finds the player's first active whip tip.
            List<WhipEntity> tips = player.level().getEntitiesOfClass(
                    WhipEntity.class, // Search for whip tips.
                    player.getBoundingBox().inflate(32.0D), // Expand the player's bounding box by 32 blocks on each axis.
                    tip -> tip.getOwner() == player); // Keep only tips owned by this player.
            return tips.isEmpty() ? null : tips.get(0); // Return null when none are found, otherwise return the first match.

        }

        // Immediately recalls the tip, preserving the original second-use behavior.
        public int retrieve () {
            this.discardWhip();
            return 1;
        }

        public boolean mustRetractWhip (Player player)
        { // Discard the tip if its owner is invalid or no longer holds a whip.
            ItemStack itemStack = player.getMainHandItem(); // Get the main-hand item.
            ItemStack itemStack2 = player.getOffhandItem(); // Get the off-hand item.
            boolean bl = itemStack.is(ModTags.Items.WHIPS); // Check whether the main hand holds the whip.
            boolean bl2 = itemStack2.is(ModTags.Items.WHIPS); // Check whether the whip is in the offhand.
            // Retracting tips may be farther away than the extension limit.
            // This method only checks whether the owner is valid and still holding
            // the whip. The distance limit is checked in tick(), where reaching it
            // changes the state to RETRACTING instead of deleting the tip.
            if (!player.isRemoved() && player.isAlive() && (bl || bl2)) {
                return false; // The owner is valid and still holds the whip.
            }
            this.discardWhip(); // Remove the tip when the owner is invalid or has put the whip away.
            return true;
        }

        private void applyLashForces() {
            double drag = this.isInWater() ? WATER_DRAG : AIR_DRAG;
            Vec3 velocity = this.getDeltaMovement().scale(drag);
            this.setDeltaMovement(velocity.add(0.0, -LASH_GRAVITY, 0.0));
        }

        // DROPPING retains the whip entity and its rope so a later held sweep can steer it again.
        private void beginDropping() {
            setCurrentState(WhipEntityState.DROPPING);
        }

        // Compare the player's current reach target with the previous one to detect renewed sweep input.
        private boolean hasSweepAimMoved(Player player) {
            Vec3 targetPosition = player.getEyePosition().add(player.getLookAngle().scale(MAX_REACH));
            if (previousSweepTargetPosition == null) {
                previousSweepTargetPosition = targetPosition;
                return false;
            }

            boolean moved = previousSweepTargetPosition.distanceToSqr(targetPosition)
                    > SWEEP_RESTART_AIM_MOVEMENT_SQR;
            previousSweepTargetPosition = targetPosition;
            return moved;
        }

        @Override
        protected void defineSynchedData (SynchedEntityData.Builder builder){
            // Register the state before getCurrentState() or setCurrentState() accesses it.
            builder.define(DATA_STATE, WhipEntityState.EXTENDING.ordinal());
            builder.define(DATA_SWEEPING, false);
        }

        @Override
        public void tick () {
            super.tick();

            Player player = this.getPlayerOwner(); // Get the player that owns this tip.

            if (player == null) {
                this.discardWhip();
                return;
            }
            boolean extending = getCurrentState() == WhipEntityState.EXTENDING;
            boolean sweeping = isSweepHeld();
            boolean dropping = getCurrentState() == WhipEntityState.DROPPING;

            // Check the current lash path before adding forces; this keeps the initial launch velocity intact.
            if (!this.level().isClientSide && extending && !sweeping) {
                this.checkCollision();
            }
            boolean sweepAimMoved = dropping && sweeping && hasSweepAimMoved(player);
            boolean guidedSweep = sweeping && (extending || (dropping && sweepAimMoved));
            if (guidedSweep) {
                if (!this.level().isClientSide && dropping && sweepAimMoved) {
                    setCurrentState(WhipEntityState.EXTENDING);
                }
                if (!this.level().isClientSide) {
                    updateSweep(player);
                } else {
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }
            } else {
                if (dropping) {
                    if (this.onGround()) {
                        // Stop ground jitter, but keep DROPPING so a moved sweep aim can restart the tip.
                        this.setDeltaMovement(Vec3.ZERO);
                    } else {
                        // A stopped airborne tip continues to fall until it lands.
                        applyLashForces();
                    }
                }
                this.move(MoverType.SELF, this.getDeltaMovement()); // Move the tip using its current velocity.
            }
            if (getCurrentState() == WhipEntityState.DROPPING && this.onGround() && !guidedSweep) {
                this.setDeltaMovement(Vec3.ZERO);
            }

            // Apply forces after movement so the launch step remains unchanged and later ticks form a projectile arc.
            if (extending && !sweeping && getCurrentState() == WhipEntityState.EXTENDING) {
                applyLashForces();
            }

            // Only enter the falling state after motion is nearly exhausted; moving the held sweep aim can revive it.
            if (!this.level().isClientSide
                    && getCurrentState() == WhipEntityState.EXTENDING
                    && this.getDeltaMovement().lengthSqr() <= MIN_FLIGHT_MOMENTUM_SQR
                    && !(guidedSweep && sweepAimMoved)) {
                beginDropping();
            }

            if (!this.level().isClientSide) {
                Vec3 playerEyePosition = player.getEyePosition();
                double distanceTravelled = this.position().distanceTo(spawnPosition);

                if (!isSweepHeld() && getCurrentState() == WhipEntityState.EXTENDING && (distanceTravelled >= MAX_REACH
                        || this.distanceToSqr(playerEyePosition) >= MAX_REACH * MAX_REACH)) {
                    setCurrentState(WhipEntityState.RETRACTING);
                }

                // Discard the tip if its owner is invalid or has switched away from the whip.
                if (this.mustRetractWhip(player)) {
                    return;
                }

                // Retraction can be requested at any time, including after a collision
                // or before the tip reaches its maximum range.
                if (getCurrentState() == WhipEntityState.RETRACTING) {
                    if (this.distanceToSqr(playerEyePosition) < 1) {
                        this.discardWhip();
                    } else {
                        retractWhip();
                    }
                }
            }
        }

        private void checkCollision () {
            HitResult hitResult =
                    ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            this.onHit(hitResult);
        }

        /** Steers the tip toward the player's aim and damages entities intersected by its actual path. */
        private void updateSweep (Player player){
            Vec3 previousPosition = this.position();
            Vec3 targetPosition = player.getEyePosition().add(player.getLookAngle().scale(MAX_REACH));
            previousSweepTargetPosition = targetPosition;
            Vec3 toTarget = targetPosition.subtract(previousPosition);
            // Steer horizontally toward the live aim while using a lower vertical response for controlled arcs.
            Vec3 horizontalToTarget = new Vec3(toTarget.x, 0.0, toTarget.z);
            double horizontalDistance = horizontalToTarget.length();
            double horizontalSpeed = Math.min(
                    SWEEP_MAX_SPEED,
                    horizontalDistance * SWEEP_ARRIVAL_SPEED_FACTOR
            );
            Vec3 horizontalVelocity = horizontalDistance < 1.0E-6
                    ? Vec3.ZERO
                    : horizontalToTarget.scale(horizontalSpeed / horizontalDistance);
            double verticalSpeed = Mth.clamp(
                    toTarget.y * SWEEP_VERTICAL_ARRIVAL_SPEED_FACTOR,
                    -SWEEP_MAX_SPEED,
                    SWEEP_MAX_SPEED
            );
            Vec3 desiredVelocity = new Vec3(horizontalVelocity.x, verticalSpeed, horizontalVelocity.z);
            Vec3 velocity = getDeltaMovement();
            Vec3 velocityChange = desiredVelocity.subtract(velocity);

            // Accelerate toward live aim over several ticks instead of snapping the tip to each new target.
            if (velocityChange.lengthSqr() > SWEEP_ACCELERATION * SWEEP_ACCELERATION) {
                velocity = velocity.add(velocityChange.normalize().scale(SWEEP_ACCELERATION));
            } else {
                velocity = desiredVelocity;
            }

            // Gravity also bends the guided sweep; clamp afterward so steering and gravity respect the same speed cap.
            velocity = velocity.add(0.0, -LASH_GRAVITY, 0.0);
            if (velocity.lengthSqr() > SWEEP_MAX_SPEED * SWEEP_MAX_SPEED) {
                velocity = velocity.normalize().scale(SWEEP_MAX_SPEED);
            }

            setDeltaMovement(velocity);
            this.move(MoverType.SELF, velocity);
            Vec3 currentPosition = this.position();

            long now = level().getGameTime();
            Vec3 currentLookAngle = player.getLookAngle();
            double angularVelocity = 0.0;
            if (previousSweepLookAngle != null) {
                long elapsedTicks = now - previousSweepTick;
                if (elapsedTicks > 0) {
                    double dot = Mth.clamp(previousSweepLookAngle.dot(currentLookAngle), -1.0, 1.0);
                    angularVelocity = Math.acos(dot) / elapsedTicks;
                }
            }
            previousSweepLookAngle = currentLookAngle;
            previousSweepTick = now;

            var searchBox = new net.minecraft.world.phys.AABB(previousPosition, currentPosition).inflate(1.0);

            // Test the tip's traveled segment. Per-UUID cooldowns prevent repeat hits without ending the sweep,
            // and deliberately continuing the loop allows one sweep step to hit multiple different targets.
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, searchBox,
                    entity -> canHitEntity(entity))) {
                boolean onPath = target.getBoundingBox().inflate(0.3).clip(previousPosition, currentPosition).isPresent();
                boolean atTip = target.getBoundingBox().intersects(getBoundingBox());
                if (!onPath && !atTip) continue;
                Long lastHit = lastSweepHitTick.get(target.getUUID());
                if (lastHit != null && now - lastHit < SWEEP_HIT_COOLDOWN) continue;

                Mth.clamp(sweepDamage, sweepDamage, 40F);
                float damage = (float) (sweepDamage + (angularVelocity * 20));
                if (target.hurt(damageSources().playerAttack(player), damage)) {
                    playHitFeedback(target);
                    lastSweepHitTick.put(target.getUUID(), now);
                    damageWhip(player);
                }
            }
        }


        @Override
        protected void onHitEntity (EntityHitResult hit){
            super.onHitEntity(hit);
            if (isSweepHeld()) return;
            // A normal lash returns immediately after its first entity contact; held sweeps use the path loop above.
            this.setCurrentState(WhipEntityState.RETRACTING);
            Entity target = hit.getEntity();
            float damage = lashDamage;
            // Keep the owner in a variable so we can check that it really is a player before using it.
            Player playerOwner = getPlayerOwner();
            // A player is needed to create this type of damage source, so only apply damage when one exists.
            if (playerOwner != null) {
                DamageSource damageSource = this.damageSources().playerAttack(playerOwner);
                if (target.hurt(damageSource, damage)) {
                    playHitFeedback(target);
                    damageWhip(playerOwner);
                }
            } else {
                // This whip has no player owner, so remove it instead of attempting player-sourced damage.
                this.discardWhip();
            }
        }


        @Override
        // Render when the squared camera distance is less than 64 squared (4096), or 64 blocks.
        public boolean shouldRenderAtSqrDistance ( double distance){
            return distance < 4096.0;
        }

        @Nullable
        // Return the owner as a Player, or null when the owner is another entity or absent.
        public Player getPlayerOwner () {
            Entity entity = getOwner();
            return entity instanceof Player ? (Player) entity : null;
        }
        @Override
        public boolean canHitEntity (Entity entity){
            return entity != getOwner() && entity instanceof LivingEntity livingEntity
                    && livingEntity.isAlive() && !livingEntity.isSpectator();
        }

        public boolean fireImmune () {
            return true;
        }

    private void playHitFeedback(Entity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        // Use vanilla hit cues only after damage succeeds, avoiding client/server duplicate effects and custom assets.
        double hitY = target.getY() + target.getBbHeight() * 0.55;
        serverLevel.playSound(
                null,
                target.getX(),
                hitY,
                target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT,
                SoundSource.PLAYERS,
                0.8F,
                1.15F
        );
        serverLevel.sendParticles(
                ParticleTypes.CRIT,
                target.getX(),
                hitY,
                target.getZ(),
                4,
                0.15,
                0.2,
                0.15,
                0.02
        );
    }

    private void damageWhip(Player player) {
        ItemStack stack = player.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;

        if (!stack.is(ModTags.Items.WHIPS)) {
            stack = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
        }

        if (stack.is(ModTags.Items.WHIPS)) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND;

            stack.hurtAndBreak(1, player, slot);
        }
    }
        public final void discardWhip () {
            this.remove(RemovalReason.DISCARDED);
            isDiscarded = true;
        }

        public void startSweep () {
            lastSweepHitTick.clear();
            // Reset aim history so the new press starts cleanly, including when restarting from a dropped tip.
            previousSweepTargetPosition = null;
            this.entityData.set(DATA_SWEEPING, true);
            setCurrentState(WhipEntityState.EXTENDING); // Keep the whip in its outbound state while sweeping.
            // Play the same vanilla whoosh used for a normal lash when a held sweep begins.
            this.level().playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.1F
            );
        }

        public void releaseSweep () {
            this.entityData.set(DATA_SWEEPING, false);
            setCurrentState(WhipEntityState.RETRACTING); // Tell the server-side entity to return to its owner.
        }


        public enum WhipEntityState { // The three movement states used by the whip tip.
            // The tip is travelling away from the player.
            EXTENDING,
            // The tip is travelling back toward the player.
            RETRACTING,
            // Reserved state for a tip that is not extending or retracting.
            ONCOOLDOWN,
            //
            HELD,
            // The tip has nearly stopped; it falls to the ground but can resume when a held sweep aim moves.
            DROPPING
        }

    }

