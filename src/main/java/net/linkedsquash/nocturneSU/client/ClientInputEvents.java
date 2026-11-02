package net.linkedsquash.nocturneSU.client;


import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.network.packets.SweepAttackPacket;
import net.linkedsquash.nocturneSU.util.ModTags;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
        modid = NocturneSU.MOD_ID,
        value = Dist.CLIENT
)
public class ClientInputEvents {
    public static boolean sweepPacketSent;

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null || !sweepPacketSent) {
            return;
        }

        boolean holdingWhip =
                minecraft.player.getMainHandItem().is(ModTags.Items.WHIPS)
                        || minecraft.player.getOffhandItem().is(ModTags.Items.WHIPS);

        if (holdingWhip && (event.isAttack() || event.isUseItem())) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            sweepPacketSent = false;
            return;
        }

        boolean holdingWhip =
                minecraft.player.getMainHandItem().is(ModTags.Items.WHIPS)
                        || minecraft.player.getOffhandItem().is(ModTags.Items.WHIPS);

        boolean shouldSweep =
                minecraft.screen == null
                        && minecraft.player.isAlive()
                        && holdingWhip
                        && ModKeyMapping.SWEEP_KEY.isDown();

        if (shouldSweep && !sweepPacketSent) {
            PacketDistributor.sendToServer(new SweepAttackPacket(SweepAttackPacket.Behavior.SWEEP)); // Ask the server to start the sweep behavior.
            sweepPacketSent = true;
        } else if (!shouldSweep && sweepPacketSent) {
            PacketDistributor.sendToServer(new SweepAttackPacket(SweepAttackPacket.Behavior.RETRACT)); // Ask the server to retract when the key is released.
            sweepPacketSent = false;
        }
    }
}
