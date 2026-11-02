
package net.linkedsquash.nocturneSU.network.packets;

import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.util.ModTags;
import net.linkedsquash.nocturneSU.world.entity.projectile.WhipEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.linkedsquash.nocturneSU.world.entity.projectile.WhipEntity;

import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Sent by the client to tell the server which whip behavior the player requested. */
public record SweepAttackPacket(Behavior behavior) implements CustomPacketPayload {

    /** The two server actions requested by pressing or releasing the sweep key. */
    public enum Behavior {
        SWEEP,
        RETRACT
    }

    // Unique identifier for this packet.
    public static final Type<SweepAttackPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    NocturneSU.MOD_ID,
                    "sweep_attack"
            ));

    // Converts the packet into network data and reconstructs it when received.
    public static final StreamCodec<RegistryFriendlyByteBuf, SweepAttackPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(
                            i -> Behavior.values()[i],
                            Behavior::ordinal
                    ),
                    SweepAttackPacket::behavior,
                    SweepAttackPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            SweepAttackPacket packet,
            IPayloadContext context
    ) {
        // Get the player who sent the packet.
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.isAlive() || player.isSpectator()) {
            return;
        }

        WhipEntity active = WhipEntity.findActive(player);

        // A release request ends the held sweep.
        if (packet.behavior() == Behavior.RETRACT) {
            if (active != null) {
                active.releaseSweep();
            }
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        // Verify that the player actually holds a whip.
        if (!mainHand.is(ModTags.Items.WHIPS)
                && !offHand.is(ModTags.Items.WHIPS)) {
            return;
        }

        if (active == null) {
            Level level = player.level();

            WhipEntity whip = new WhipEntity(player, level);

            level.addFreshEntity(whip);

            active = whip;
        }

        active.startSweep();
    }
}
