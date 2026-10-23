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

    public static void register() {
        STIRLING_ENGINE = machine("stirling_engine", StirlingEngineBlockEntity::new);
        QUARRY = machine("quarry", QuarryBlockEntity::new);
        MACERATOR = machine("macerator", MaceratorBlockEntity::new);
        ITEM_PIPE = machine("item_pipe", ItemPipeBlockEntity::new);
        TELEPORT_PIPE = machine("teleport_pipe", TeleportPipeBlockEntity::new);
        NUCLEAR_REACTOR = machine("nuclear_reactor", NuclearReactorBlockEntity::new);
        REQUEST_PIPE = machine("request_pipe", RequestPipeBlockEntity::new);
    }

    private static Block machine(String name, BlockEntityFactory factory) {
        Identifier id = BCN.id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        Block block = new MachineBlock(factory,
                BlockBehaviour.Properties.of().setId(key).strength(3.0f).sound(SoundType.METAL));
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
                default -> {}
            }
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                Player player, BlockHitResult hit) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MachineBlockEntity be) {
                if (be instanceof TeleportPipeBlockEntity tp) {
                    if (player.isShiftKeyDown()) tp.cycleMode(); else tp.cycleChannel();
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
