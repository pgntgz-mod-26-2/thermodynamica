package com.Tribulla.thermodynamica.simulation;

import com.Tribulla.thermodynamica.Thermodynamica;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class HeatSavedData extends SavedData {

    private final HeatSimulationManager manager;

    public HeatSavedData(HeatSimulationManager manager) {
        this.manager = manager;
    }

    public static SavedDataType<HeatSavedData> type(HeatSimulationManager manager) {
        Codec<HeatSavedData> codec = CompoundTag.CODEC.xmap(
                tag -> load(tag, manager),
                data -> {
                    CompoundTag tag = new CompoundTag();
                    data.manager.saveToNBT(tag);
                    return tag;
                });
        return new SavedDataType<>(
                Identifier.fromNamespaceAndPath(Thermodynamica.MODID, "heat"),
                () -> new HeatSavedData(manager),
                codec);
    }

    public static HeatSavedData load(CompoundTag tag, HeatSimulationManager manager) {
        HeatSavedData data = new HeatSavedData(manager);
        manager.loadFromNBT(tag);
        return data;
    }
}
