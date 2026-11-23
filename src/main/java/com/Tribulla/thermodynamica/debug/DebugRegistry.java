package com.Tribulla.thermodynamica.debug;

import com.Tribulla.thermodynamica.Thermodynamica;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DebugRegistry {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thermodynamica.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thermodynamica.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, Thermodynamica.MODID);

    public static final DeferredBlock<Block> VARIABLE_HEAT_BLOCK = BLOCKS.registerBlock("variable_heat_block",
            props -> new VariableHeatBlock(props
                    .mapColor(MapColor.COLOR_RED)
                    .strength(1.5f)
                    .noOcclusion()));

    public static final DeferredItem<BlockItem> VARIABLE_HEAT_ITEM = ITEMS.registerSimpleBlockItem("variable_heat_block",
            VARIABLE_HEAT_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VariableHeatBlockEntity>> VARIABLE_HEAT_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "variable_heat_block",
            () -> new BlockEntityType<>(VariableHeatBlockEntity::new, VARIABLE_HEAT_BLOCK.get()));

    public static final DeferredItem<Item> HEAT_INSPECTOR = ITEMS.registerItem("heat_inspector",
            props -> new HeatInspectorItem(props.stacksTo(1)));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        Thermodynamica.LOGGER.debug("Debug registries bound to mod bus");
    }
}

