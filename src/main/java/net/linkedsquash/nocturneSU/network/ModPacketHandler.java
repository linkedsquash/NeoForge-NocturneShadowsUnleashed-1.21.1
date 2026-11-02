package net.linkedsquash.nocturneSU.network;


import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.network.packets.SweepAttackPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(
        modid = NocturneSU.MOD_ID
)

public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    private ModPacketHandler() {

    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {

        var registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(
                SweepAttackPacket.TYPE,
                SweepAttackPacket.STREAM_CODEC,
                SweepAttackPacket::handle
        );
    }


}