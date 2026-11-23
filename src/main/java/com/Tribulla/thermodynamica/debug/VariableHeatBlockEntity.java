package com.Tribulla.thermodynamica.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class VariableHeatBlockEntity extends BlockEntity {

    private double temperature = 100.0; // Default

    public VariableHeatBlockEntity(BlockPos pos, BlockState state) {
        super(DebugRegistry.VARIABLE_HEAT_BLOCK_ENTITY.get(), pos, state);
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putDouble("Temperature", temperature);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        temperature = input.getDoubleOr("Temperature", 100.0);
    }
}
