package com.Tribulla.thermodynamica.network;

import com.Tribulla.thermodynamica.Thermodynamica;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record ChunkHeatSyncPacket(ChunkPos chunkPos, Map<BlockPos, HeatData> temperatures) implements CustomPacketPayload {

    public static final Type<ChunkHeatSyncPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Thermodynamica.MODID, "chunk_heat_sync"));

    public static final StreamCodec<FriendlyByteBuf, ChunkHeatSyncPacket> STREAM_CODEC = StreamCodec.of(
            ChunkHeatSyncPacket::encode,
            ChunkHeatSyncPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record HeatData(double celsius, BlockPos renderStatePos, Vec3 worldCenter) {
    }

    public static void encode(FriendlyByteBuf buf, ChunkHeatSyncPacket packet) {
        buf.writeInt(packet.chunkPos.x());
        buf.writeInt(packet.chunkPos.z());
        buf.writeVarInt(packet.temperatures.size());
        for (Map.Entry<BlockPos, HeatData> entry : packet.temperatures.entrySet()) {
            buf.writeBlockPos(entry.getKey());
            buf.writeDouble(entry.getValue().celsius());
            buf.writeBlockPos(entry.getValue().renderStatePos());
            buf.writeDouble(entry.getValue().worldCenter().x);
            buf.writeDouble(entry.getValue().worldCenter().y);
            buf.writeDouble(entry.getValue().worldCenter().z);
        }
    }

    public static ChunkHeatSyncPacket decode(FriendlyByteBuf buf) {
        int cx = buf.readInt();
        int cz = buf.readInt();
        int count = buf.readVarInt();
        Map<BlockPos, HeatData> temps = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            BlockPos pos = buf.readBlockPos();
            double celsius = buf.readDouble();
            BlockPos renderStatePos = buf.readBlockPos();
            Vec3 worldCenter = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            temps.put(pos, new HeatData(celsius, renderStatePos, worldCenter));
        }
        return new ChunkHeatSyncPacket(new ChunkPos(cx, cz), temps);
    }

    public static void handle(ChunkHeatSyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            for (Map.Entry<BlockPos, HeatData> entry : packet.temperatures.entrySet()) {
                HeatData data = entry.getValue();
                ClientHeatCache.update(entry.getKey(), data.celsius(), data.renderStatePos(), data.worldCenter());
            }
        });
    }
}
