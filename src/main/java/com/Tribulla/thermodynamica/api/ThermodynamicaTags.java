package com.Tribulla.thermodynamica.api;

import com.Tribulla.thermodynamica.Thermodynamica;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ThermodynamicaTags {
    public static final TagKey<Block> RADIATES_HEAT = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(Thermodynamica.MODID, "radiates_heat"));
}
