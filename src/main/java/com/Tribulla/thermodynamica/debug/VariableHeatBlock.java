package com.Tribulla.thermodynamica.debug;

import com.Tribulla.thermodynamica.api.HeatAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class VariableHeatBlock extends Block implements EntityBlock {

    public VariableHeatBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VariableHeatBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && level instanceof ServerLevel) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof VariableHeatBlockEntity vhe) {
                HeatAPI.get().setTemperature(level, pos, vhe.getTemperature());
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        HeatAPI.get().setTemperature(level, pos, HeatAPI.get().getAmbientTemperature());
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof VariableHeatBlockEntity vhe) {
            double current = vhe.getTemperature();
            double newTemp = player.isShiftKeyDown() ? current - 50.0 : current + 50.0;
            vhe.setTemperature(newTemp);
            HeatAPI.get().setTemperature(level, pos, newTemp);

            player.sendOverlayMessage(Component.literal("§eTarget Temperature: §b" + newTemp + " °C"));
        }

        return InteractionResult.CONSUME;
    }
}
