package com.Tribulla.thermodynamica.network;

import com.Tribulla.thermodynamica.Thermodynamica;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HeatSyncPacket(BlockPos pos, double celsius) implements CustomPacketPayload {

    public static final Type<HeatSyncPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Thermodynamica.MODID, "heat_sync"));

    public static final StreamCodec<FriendlyByteBuf, HeatSyncPacket> STREAM_CODEC = StreamCodec.of(
            HeatSyncPacket::encode,
            HeatSyncPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(FriendlyByteBuf buf, HeatSyncPacket packet) {
        buf.writeBlockPos(packet.pos);
        buf.writeDouble(packet.celsius);
    }

    public static HeatSyncPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        double celsius = buf.readDouble();
        return new HeatSyncPacket(pos, celsius);
    }

    public static void handle(HeatSyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientHeatCache.update(packet.pos, packet.celsius);
        });
    }

    public BlockPos getPos() {
        return pos;
    }

    public double getCelsius() {
        return celsius;
    }
}
