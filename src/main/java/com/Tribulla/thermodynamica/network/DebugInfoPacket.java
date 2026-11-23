package com.Tribulla.thermodynamica.network;

import com.Tribulla.thermodynamica.Thermodynamica;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DebugInfoPacket(
        BlockPos pos,
        String blockName,
        double celsius,
        double conductivity,
        double dissipationRate) implements CustomPacketPayload {

    public static final Type<DebugInfoPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Thermodynamica.MODID, "debug_info"));

    public static final StreamCodec<FriendlyByteBuf, DebugInfoPacket> STREAM_CODEC = StreamCodec.of(
            DebugInfoPacket::encode,
            DebugInfoPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(FriendlyByteBuf buf, DebugInfoPacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeUtf(packet.blockName);
        buf.writeDouble(packet.celsius);
        buf.writeDouble(packet.conductivity);
        buf.writeDouble(packet.dissipationRate);
    }

    public static DebugInfoPacket decode(FriendlyByteBuf buf) {
        return new DebugInfoPacket(
                buf.readBlockPos(),
                buf.readUtf(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble());
    }

    public static void handle(DebugInfoPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientHeatCache.setDebugInfo(packet);
        });
    }

    public BlockPos getPos() {
        return pos;
    }

    public String getBlockName() {
        return blockName;
    }

    public double getCelsius() {
        return celsius;
    }

    public double getConductivity() {
        return conductivity;
    }

    public double getDissipationRate() {
        return dissipationRate;
    }
}
