package net.linkedsquash.nocturne.network.packets;

import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.util.ModTags;
import net.linkedsquash.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Sent by the client when charging, releasing, or cancelling a charged sweep. */
public record SweepAttackPacket(Behavior behavior) implements CustomPacketPayload {
    private static final int MAX_CHARGE_TICKS = 30;
    private static final int[] CHARGE_THRESHOLD_TICKS = {8, 15, 23, 30};
    private static final float[] CHARGE_THRESHOLD_PITCHES = {0.75F, 0.95F, 1.15F, 1.35F};
    private static final Map<UUID, ChargeStart> CHARGE_STARTS = new HashMap<>();

    public enum Behavior {
        START_CHARGE,
        RELEASE,
        CANCEL
    }

    private record ChargeStart(long gameTime, ServerLevel level, WhipEntity trackedTip, int nextThresholdIndex) {
    }

    public static final Type<SweepAttackPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID, "sweep_attack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SweepAttackPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(
                            index -> Behavior.values()[index],
                            Behavior::ordinal
                    ),
                    SweepAttackPacket::behavior,
                    SweepAttackPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SweepAttackPacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        if (packet.behavior() == Behavior.CANCEL) {
            cancelCharge(player);
            return;
        }

        if (packet.behavior() == Behavior.START_CHARGE) {
            if (!canCharge(player) || CHARGE_STARTS.containsKey(player.getUUID())) {
                return;
            }

            WhipEntity active = WhipEntity.findActive(player);
            CHARGE_STARTS.put(
                    player.getUUID(),
                    new ChargeStart(player.serverLevel().getGameTime(), player.serverLevel(), active, 0)
            );
            return;
        }

        ChargeStart chargeStart = CHARGE_STARTS.remove(player.getUUID());
        if (chargeStart == null) {
            return;
        }

        if (chargeStart.level() != player.serverLevel()) {
            WhipEntity trackedTip = chargeStart.trackedTip();
            if (trackedTip != null && !trackedTip.isRemoved()) {
                trackedTip.discardWhip();
            }
            return;
        }

        if (!canCharge(player)) {
            return;
        }

        WhipEntity active = chargeStart.trackedTip() != null && !chargeStart.trackedTip().isRemoved()
                ? chargeStart.trackedTip()
                : WhipEntity.findActive(player);

        long elapsedTicks = Math.max(0L, player.serverLevel().getGameTime() - chargeStart.gameTime());
        playChargeThresholdSounds(player, chargeStart, elapsedTicks);
        float chargeAmount = Math.min(elapsedTicks, MAX_CHARGE_TICKS) / (float) MAX_CHARGE_TICKS;

        if (active == null) {
            active = new WhipEntity(player, player.level());
            active.fireChargedSweep(player, player.getLookAngle(), chargeAmount, false);
            if (!player.level().addFreshEntity(active)) {
                return;
            }
            return;
        }

        active.fireChargedSweep(player, player.getLookAngle(), chargeAmount, true);
    }

    private static boolean canCharge(ServerPlayer player) {
        return player.isAlive()
                && !player.isSpectator()
                && (player.getMainHandItem().is(ModTags.Items.WHIPS)
                || player.getOffhandItem().is(ModTags.Items.WHIPS));
    }

    private static void cancelCharge(ServerPlayer player) {
        CHARGE_STARTS.remove(player.getUUID());
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ChargeStart chargeStart = CHARGE_STARTS.get(player.getUUID());
        if (chargeStart == null) {
            return;
        }

        if (chargeStart.level() != player.serverLevel()) {
            discardTrackedTip(player.getUUID());
            return;
        }

        if (!canCharge(player)) {
            cancelCharge(player);
            return;
        }

        long elapsedTicks = Math.max(0L, player.serverLevel().getGameTime() - chargeStart.gameTime());
        ChargeStart updated = playChargeThresholdSounds(player, chargeStart, elapsedTicks);
        if (updated.nextThresholdIndex() != chargeStart.nextThresholdIndex()) {
            CHARGE_STARTS.put(player.getUUID(), updated);
        }
    }

    private static ChargeStart playChargeThresholdSounds(
            ServerPlayer player,
            ChargeStart chargeStart,
            long elapsedTicks
    ) {
        int nextThreshold = chargeStart.nextThresholdIndex();
        int chargedTicks = (int) Math.min(elapsedTicks, MAX_CHARGE_TICKS);
        while (nextThreshold < CHARGE_THRESHOLD_TICKS.length
                && chargedTicks >= CHARGE_THRESHOLD_TICKS[nextThreshold]) {
            player.serverLevel().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.PLAYERS,
                    0.5F,
                    CHARGE_THRESHOLD_PITCHES[nextThreshold]
            );
            nextThreshold++;
        }

        return new ChargeStart(
                chargeStart.gameTime(),
                chargeStart.level(),
                chargeStart.trackedTip(),
                nextThreshold
        );
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        discardTrackedTip(event.getEntity().getUUID());
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        discardTrackedTip(event.getEntity().getUUID());
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        discardTrackedTip(event.getEntity().getUUID());
    }

    private static void discardTrackedTip(UUID playerId) {
        ChargeStart chargeStart = CHARGE_STARTS.remove(playerId);
        if (chargeStart != null && chargeStart.trackedTip() != null && !chargeStart.trackedTip().isRemoved()) {
            chargeStart.trackedTip().discardWhip();
        }
    }
}
