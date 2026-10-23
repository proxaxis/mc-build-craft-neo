package com.buildcraftneo.industry;

import com.buildcraftneo.BCN;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BCNBlockEntities {
    private BCNBlockEntities() {}

    public static BlockEntityType<StirlingEngineBlockEntity> STIRLING_ENGINE;
    public static BlockEntityType<QuarryBlockEntity> QUARRY;
    public static BlockEntityType<MaceratorBlockEntity> MACERATOR;
    public static BlockEntityType<ItemPipeBlockEntity> ITEM_PIPE;

    public static void register() {
        STIRLING_ENGINE = type("stirling_engine", StirlingEngineBlockEntity::new, BCNBlocks.STIRLING_ENGINE);
        QUARRY = type("quarry", QuarryBlockEntity::new, BCNBlocks.QUARRY);
        MACERATOR = type("macerator", MaceratorBlockEntity::new, BCNBlocks.MACERATOR);
        ITEM_PIPE = type("item_pipe", ItemPipeBlockEntity::new, BCNBlocks.ITEM_PIPE);
    }

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> type(
            String name, FabricBlockEntityTypeBuilder.Factory<T> factory,
            net.minecraft.world.level.block.Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BCN.id(name),
                FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }
}
