package com.buildcraftneo.industry;

import com.buildcraftneo.BCN;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class BCNBlocks {
    private BCNBlocks() {}

    public static Block STIRLING_ENGINE;
    public static Block QUARRY;
    public static Block MACERATOR;
    public static Block ITEM_PIPE;
    public static Block TELEPORT_PIPE;
    public static Block NUCLEAR_REACTOR;
    public static Block REQUEST_PIPE;
    public static Block SHOP;
    public static Block DIMENSION_GATE;
    public static Block BOSS_ALTAR;
    public static Block TIMER;
    public static Block LOGIC_GATE;
    public static Block AMBROSIUM_ORE;
    public static Block ZANITE_ORE;
    public static Block LAMP;
    public static Block SUPPLIER_PIPE;
    public static Block SORTING_PIPE;
    public static Block CRAFTING_PIPE;
    public static Block ATM;
    public static Block GRAVITITE_ORE;
    public static Block SKYROOT_LOG;
    public static Block SKYROOT_PLANKS;

    public static void register() {
        STIRLING_ENGINE = machine("stirling_engine", StirlingEngineBlockEntity::new);
        QUARRY = machine("quarry", QuarryBlockEntity::new);
        MACERATOR = machine("macerator", MaceratorBlockEntity::new);
        ITEM_PIPE = machine("item_pipe", ItemPipeBlockEntity::new);
        TELEPORT_PIPE = machine("teleport_pipe", TeleportPipeBlockEntity::new);
        NUCLEAR_REACTOR = machine("nuclear_reactor", NuclearReactorBlockEntity::new);
        REQUEST_PIPE = machine("request_pipe", RequestPipeBlockEntity::new);
        SHOP = machine("shop", ShopBlockEntity::new);
        DIMENSION_GATE = machine("dimensional_gate", DimensionGateBlockEntity::new,
                BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.METAL).noCollision());
        BOSS_ALTAR = machine("boss_altar", BossAltarBlockEntity::new);
        TIMER = block("timer", TimerBlock::new,
                BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.STONE));
        LOGIC_GATE = block("logic_gate", LogicGateBlock::new,
                BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.STONE));
        LAMP = block("lamp", net.minecraft.world.level.block.RedstoneLampBlock::new,
                BlockBehaviour.Properties.of().strength(0.3f)
                        .lightLevel(st -> st.getValue(
                                net.minecraft.world.level.block.state.properties
                                        .BlockStateProperties.LIT) ? 15 : 0));
        ZANITE_ORE = block("zanite_ore", Block::new,
                BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)
                        .requiresCorrectToolForDrops());
        AMBROSIUM_ORE = block("ambrosium_ore", Block::new,
                BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)
                        .requiresCorrectToolForDrops());
        SUPPLIER_PIPE = machine("supplier_pipe", SupplierPipeBlockEntity::new);
        SORTING_PIPE = machine("sorting_pipe", SortingPipeBlockEntity::new);
        CRAFTING_PIPE = machine("crafting_pipe", CraftingPipeBlockEntity::new);
        ATM = machine("atm", AtmBlockEntity::new);
        GRAVITITE_ORE = block("gravitite_ore", GravititeBlock::new,
                BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)
                        .requiresCorrectToolForDrops());
        SKYROOT_LOG = block("skyroot_log", Block::new,
                BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD));
        SKYROOT_PLANKS = block("skyroot_planks", Block::new,
                BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD));
    }

    private static Block block(String name,
            java.util.function.Function<BlockBehaviour.Properties, Block> factory,
            BlockBehaviour.Properties props) {
        Identifier id = BCN.id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        Block block = factory.apply(props.setId(key));
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, id,
                new BlockItem(block, new Item.Properties().setId(itemKey)));
        return block;
    }

    private static Block machine(String name, BlockEntityFactory factory) {
        return machine(name, factory,
                BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.METAL));
    }

    private static Block machine(String name, BlockEntityFactory factory,
            BlockBehaviour.Properties props) {
        Identifier id = BCN.id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        Block block = new MachineBlock(factory, props.setId(key));
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, id,
                new BlockItem(block, new Item.Properties().setId(itemKey)));
        return block;
    }

    @FunctionalInterface
    public interface BlockEntityFactory {
        BlockEntity create(BlockPos pos, BlockState state);
    }

    /** Shared machine block: EntityBlock + ticker + right-click status. */
    private static class MachineBlock extends Block implements EntityBlock {
        private final BlockEntityFactory factory;

        MachineBlock(BlockEntityFactory factory, Properties props) {
            super(props);
            this.factory = factory;
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return factory.create(pos, state);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                BlockEntityType<T> type) {
            return (BlockEntityTicker<T>) (BlockEntityTicker<MachineBlockEntity>) MachineBlock::tick;
        }

        private static void tick(Level level, BlockPos pos, BlockState state, MachineBlockEntity be) {
            // dispatch to the BE's own static tick via instanceof
            switch (be) {
                case StirlingEngineBlockEntity e -> StirlingEngineBlockEntity.tick(level, pos, state, e);
                case QuarryBlockEntity e -> QuarryBlockEntity.tick(level, pos, state, e);
                case MaceratorBlockEntity e -> MaceratorBlockEntity.tick(level, pos, state, e);
                case ItemPipeBlockEntity e -> ItemPipeBlockEntity.tick(level, pos, state, e);
                case TeleportPipeBlockEntity e -> TeleportPipeBlockEntity.tick(level, pos, state, e);
                case NuclearReactorBlockEntity e -> NuclearReactorBlockEntity.tick(level, pos, state, e);
                case RequestPipeBlockEntity e -> RequestPipeBlockEntity.tick(level, pos, state, e);
                case SupplierPipeBlockEntity e -> SupplierPipeBlockEntity.tick(level, pos, state, e);
                case SortingPipeBlockEntity e -> SortingPipeBlockEntity.tick(level, pos, state, e);
                case CraftingPipeBlockEntity e -> CraftingPipeBlockEntity.tick(level, pos, state, e);
                case DimensionGateBlockEntity e -> DimensionGateBlockEntity.tick(level, pos, state, e);
                default -> {}
            }
        }

        @Override
        protected void entityInside(BlockState state, Level level, BlockPos pos,
                net.minecraft.world.entity.Entity entity,
                net.minecraft.world.entity.InsideBlockEffectApplier applier, boolean bl) {
            if (level.getBlockEntity(pos) instanceof DimensionGateBlockEntity gate) {
                gate.onEntityInside(level, entity);
            }
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                Player player, BlockHitResult hit) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MachineBlockEntity be) {
                if (be instanceof TeleportPipeBlockEntity tp) {
                    if (player.isShiftKeyDown()) tp.cycleMode(); else tp.cycleChannel();
                } else if (be instanceof DimensionGateBlockEntity gate) {
                    gate.cycleTarget();
                } else if (be instanceof BossAltarBlockEntity altar
                        && level instanceof net.minecraft.server.level.ServerLevel sl) {
                    if (player.isShiftKeyDown()) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "[BCN] boss -> " + altar.cycleBoss()));
                    } else {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                altar.summonBoss(sl) ? "[BCN] Boss summoned! (" + altar.bossName() + ")"
                                                     : "[BCN] a boss is already nearby"));
                    }
                    return InteractionResult.SUCCESS_SERVER;
                } else if (be instanceof AtmBlockEntity atm
                        && player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            player.isShiftKeyDown()
                                    ? "[BCN ATM] 引き出し " + atm.withdraw(sp, 500) + " MD"
                                    : "[BCN ATM] 入金 " + atm.deposit(sp) + " MD"
                                                    + " (残高=" + com.buildcraftneo.data.Profiles.md(sp) + ")"));
                    return InteractionResult.SUCCESS_SERVER;
                } else if (be instanceof ShopBlockEntity shop && !player.isShiftKeyDown()
                        && player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "[BCN shop] " + shop.sellTo(sp)));
                    return InteractionResult.SUCCESS_SERVER;
                }
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "[BCN] " + be.status()));
            }
            return InteractionResult.SUCCESS_SERVER;
        }
    }

    static {
        // referenced for sound type; keeps import used
        Blocks.AIR.getClass();
    }
}
