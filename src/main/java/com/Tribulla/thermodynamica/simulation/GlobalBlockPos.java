package com.Tribulla.thermodynamica.simulation;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class GlobalBlockPos {
    final Identifier dimension;
    final BlockPos pos;

    public GlobalBlockPos(Identifier dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos.immutable();
    }

    public GlobalBlockPos(Identifier dimension, long packedPos) {
        this.dimension = dimension;
        this.pos = BlockPos.of(packedPos).immutable();
    }

    public Identifier getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof GlobalBlockPos other))
            return false;
        return dimension.equals(other.dimension) && pos.equals(other.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, pos);
    }
}
