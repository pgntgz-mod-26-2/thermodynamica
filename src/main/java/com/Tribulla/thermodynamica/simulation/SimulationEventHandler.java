package com.Tribulla.thermodynamica.simulation;

import com.Tribulla.thermodynamica.Thermodynamica;
import com.Tribulla.thermodynamica.api.HeatAPI;
import com.Tribulla.thermodynamica.api.ThermalProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Thermodynamica.MODID)
public class SimulationEventHandler {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {

        Thermodynamica instance = Thermodynamica.getInstance();
        if (instance != null && instance.getSimulationManager() != null) {
            instance.getSimulationManager().tick();
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level &&
                event.getChunk() instanceof LevelChunk chunk) {
            Thermodynamica instance = Thermodynamica.getInstance();
            if (instance != null && instance.getSimulationManager() != null) {
                instance.getSimulationManager().onChunkLoad(level, chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level &&
                event.getChunk() instanceof LevelChunk chunk) {
            Thermodynamica instance = Thermodynamica.getInstance();
            if (instance != null && instance.getSimulationManager() != null) {
                instance.getSimulationManager().onChunkUnload(level, chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        Thermodynamica instance = Thermodynamica.getInstance();
        if (instance == null || instance.getSimulationManager() == null)
            return;

        BlockPos pos = event.getPos();
        BlockState state = event.getPlacedBlock();
        instance.getSimulationManager().onBlockChanged(level, pos);
        checkAndRegisterSource(level, pos, state, instance);
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        Thermodynamica instance = Thermodynamica.getInstance();
        if (instance == null || instance.getSimulationManager() == null)
            return;

        BlockPos pos = event.getPos();
        instance.getSimulationManager().onBlockChanged(level, pos);
        instance.getSimulationManager().markInactive(level, pos);
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        Thermodynamica instance = Thermodynamica.getInstance();
        if (instance == null || instance.getSimulationManager() == null)
            return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        checkAndRegisterSource(level, pos, state, instance);
    }

    private static void checkAndRegisterSource(ServerLevel level, BlockPos pos,
            BlockState state, Thermodynamica instance) {
        // Skip fluid blocks entirely — lava pools underground are the #1 source of
        // simulation lag and fluid heat is not important for gameplay.
        if (!state.getFluidState().isEmpty())
            return;

        HeatSimulationManager sim = instance.getSimulationManager();
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (blockId == null)
            return;

        double ambientTemp = instance.getConfigManager().getSettings().getAmbientTemperature();
        double blockTemp = HeatAPI.get().getBaseCelsiusForState(blockId, state);

        if (Math.abs(blockTemp - ambientTemp) > instance.getConfigManager().getSettings().getDeltaThreshold()) {
            sim.markActive(level, pos);
        } else {
            sim.markInactive(level, pos);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
            Thermodynamica instance = Thermodynamica.getInstance();
            if (instance != null && instance.getSimulationManager() != null) {
                HeatSavedData data = level.getDataStorage().computeIfAbsent(
                        HeatSavedData.type(instance.getSimulationManager()));
                instance.getSimulationManager().setSavedData(data);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelSave(LevelEvent.Save event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
            Thermodynamica instance = Thermodynamica.getInstance();
            if (instance != null && instance.getSimulationManager() != null) {
                HeatSavedData data = instance.getSimulationManager().getSavedData();
                if (data != null) {
                    data.setDirty();
                }
            }
        }
    }
}
