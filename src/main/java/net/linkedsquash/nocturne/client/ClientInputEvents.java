package net.linkedsquash.nocturne.client;


import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.network.packets.SweepAttackPacket;
import net.linkedsquash.nocturne.util.ModTags;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
        modid = Nocturne.MOD_ID,
        value = Dist.CLIENT
)
public class ClientInputEvents {
    private static boolean chargePacketSent;

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null || !chargePacketSent) {
            return;
        }

        boolean holdingWhip =
                minecraft.player.getMainHandItem().is(ModTags.Items.WHIPS)
                        || minecraft.player.getOffhandItem().is(ModTags.Items.WHIPS);

        boolean chargeInputDown = chargePacketSent || ModKeyMapping.SWEEP_KEY.isDown();
        if (holdingWhip && chargeInputDown && (event.isAttack() || event.isUseItem())) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            chargePacketSent = false;
            return;
        }

        boolean holdingWhip =
                minecraft.player.getMainHandItem().is(ModTags.Items.WHIPS)
                        || minecraft.player.getOffhandItem().is(ModTags.Items.WHIPS);

        boolean canCharge =
                minecraft.screen == null
                        && minecraft.player.isAlive()
                        && !minecraft.player.isSpectator()
                        && holdingWhip;
        boolean shouldCharge = canCharge && ModKeyMapping.SWEEP_KEY.isDown();

        if (shouldCharge && !chargePacketSent) {
            PacketDistributor.sendToServer(new SweepAttackPacket(SweepAttackPacket.Behavior.START_CHARGE));
            chargePacketSent = true;
        } else if (!shouldCharge && chargePacketSent) {
            SweepAttackPacket.Behavior behavior = canCharge
                    ? SweepAttackPacket.Behavior.RELEASE
                    : SweepAttackPacket.Behavior.CANCEL;
            PacketDistributor.sendToServer(new SweepAttackPacket(behavior));
            chargePacketSent = false;
        }
    }
}
