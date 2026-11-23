package com.Tribulla.thermodynamica.network;

import com.Tribulla.thermodynamica.Thermodynamica;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class HeatNetwork {

    private static final String PROTOCOL_VERSION = "2";

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Thermodynamica.MODID).versioned(PROTOCOL_VERSION);

        registrar.playToClient(
                HeatSyncPacket.TYPE,
                HeatSyncPacket.STREAM_CODEC,
                HeatSyncPacket::handle);

        registrar.playToClient(
                ChunkHeatSyncPacket.TYPE,
                ChunkHeatSyncPacket.STREAM_CODEC,
                ChunkHeatSyncPacket::handle);

        registrar.playToClient(
                DebugInfoPacket.TYPE,
                DebugInfoPacket.STREAM_CODEC,
                DebugInfoPacket::handle);

        Thermodynamica.LOGGER.debug("Network payloads registered");
    }
}
