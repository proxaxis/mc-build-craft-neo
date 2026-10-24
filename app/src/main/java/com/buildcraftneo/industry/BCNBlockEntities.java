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
    public static BlockEntityType<TeleportPipeBlockEntity> TELEPORT_PIPE;
    public static BlockEntityType<NuclearReactorBlockEntity> NUCLEAR_REACTOR;
    public static BlockEntityType<RequestPipeBlockEntity> REQUEST_PIPE;
    public static BlockEntityType<ShopBlockEntity> SHOP;
    public static BlockEntityType<DimensionGateBlockEntity> DIMENSION_GATE;
    public static BlockEntityType<BossAltarBlockEntity> BOSS_ALTAR;
    public static BlockEntityType<TimerBlockEntity> TIMER;
    public static BlockEntityType<LogicGateBlockEntity> LOGIC_GATE;
    public static BlockEntityType<SupplierPipeBlockEntity> SUPPLIER_PIPE;
    public static BlockEntityType<SortingPipeBlockEntity> SORTING_PIPE;
    public static BlockEntityType<CraftingPipeBlockEntity> CRAFTING_PIPE;
    public static BlockEntityType<AtmBlockEntity> ATM;

    public static void register() {
        STIRLING_ENGINE = type("stirling_engine", StirlingEngineBlockEntity::new, BCNBlocks.STIRLING_ENGINE);
        QUARRY = type("quarry", QuarryBlockEntity::new, BCNBlocks.QUARRY);
        MACERATOR = type("macerator", MaceratorBlockEntity::new, BCNBlocks.MACERATOR);
        ITEM_PIPE = type("item_pipe", ItemPipeBlockEntity::new, BCNBlocks.ITEM_PIPE);
        TELEPORT_PIPE = type("teleport_pipe", TeleportPipeBlockEntity::new, BCNBlocks.TELEPORT_PIPE);
        NUCLEAR_REACTOR = type("nuclear_reactor", NuclearReactorBlockEntity::new, BCNBlocks.NUCLEAR_REACTOR);
        REQUEST_PIPE = type("request_pipe", RequestPipeBlockEntity::new, BCNBlocks.REQUEST_PIPE);
        SHOP = type("shop", ShopBlockEntity::new, BCNBlocks.SHOP);
        DIMENSION_GATE = type("dimensional_gate", DimensionGateBlockEntity::new, BCNBlocks.DIMENSION_GATE);
        BOSS_ALTAR = type("boss_altar", BossAltarBlockEntity::new, BCNBlocks.BOSS_ALTAR);
        TIMER = type("timer", TimerBlockEntity::new, BCNBlocks.TIMER);
        LOGIC_GATE = type("logic_gate", LogicGateBlockEntity::new, BCNBlocks.LOGIC_GATE);
        SUPPLIER_PIPE = type("supplier_pipe", SupplierPipeBlockEntity::new, BCNBlocks.SUPPLIER_PIPE);
        SORTING_PIPE = type("sorting_pipe", SortingPipeBlockEntity::new, BCNBlocks.SORTING_PIPE);
        CRAFTING_PIPE = type("crafting_pipe", CraftingPipeBlockEntity::new, BCNBlocks.CRAFTING_PIPE);
        ATM = type("atm", AtmBlockEntity::new, BCNBlocks.ATM);
    }

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> type(
            String name, FabricBlockEntityTypeBuilder.Factory<T> factory,
            net.minecraft.world.level.block.Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BCN.id(name),
                FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }
}
