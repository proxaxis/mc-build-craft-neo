package com.buildcraftneo.industry;

import com.buildcraftneo.BCN;
import java.util.List;
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
    public static Block COMPRESSOR;
    public static Block URANIUM_ORE;
    public static Block INSULATED_WIRE;
    public static Block INSULATED_WIRE_RED;
    public static Block INSULATED_WIRE_GREEN;
    public static Block INSULATED_WIRE_BLUE;
    public static Block BUNDLED_WIRE;
    public static Block FRAME;
    public static Block STICKY_FRAME;
    public static Block ANCHORED_FRAME;
    public static Block FRAME_MOTOR;
    public static Block SOLAR_PANEL;
    public static Block WIND_MILL;
    public static Block GEOTHERMAL_GENERATOR;
    public static Block BAT_BOX;
    public static Block BUS_CONVERTER;
    public static Block BUS_TRANSCEIVER;
    public static Block CHARGE_PAD;
    public static Block COPPER_CABLE;
    public static Block HV_CABLE;
    public static Block BUS_RANDOMIZER;

    /** Extra insulated-wire colors by color name (channels 4-15). */
    public static final java.util.Map<String, Block> INSULATED_WIRES =
            new java.util.LinkedHashMap<>();

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
        COMPRESSOR = machine("compressor", CompressorBlockEntity::new);
        URANIUM_ORE = block("uranium_ore", Block::new,
                BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)
                        .requiresCorrectToolForDrops());
        INSULATED_WIRE = block("insulated_wire", InsulatedWireBlock::new,
                wireProps());
        INSULATED_WIRE_RED = block("insulated_wire_red",
                p -> new InsulatedWireBlock(p, "red"), wireProps());
        INSULATED_WIRE_GREEN = block("insulated_wire_green",
                p -> new InsulatedWireBlock(p, "green"), wireProps());
        INSULATED_WIRE_BLUE = block("insulated_wire_blue",
                p -> new InsulatedWireBlock(p, "blue"), wireProps());
        // remaining bundled-bus channel colors (channels 4-15)
        for (String c : List.of("white", "orange", "magenta", "light_blue",
                "yellow", "lime", "pink", "gray", "light_gray", "cyan",
                "purple", "brown")) {
            INSULATED_WIRES.put(c, block("insulated_wire_" + c,
                    p -> new InsulatedWireBlock(p, c), wireProps()));
        }
        BUNDLED_WIRE = block("bundled_wire", BundledWireBlock::new, wireProps());
        FRAME = block("frame", p -> new FrameBlock(p, FrameBlock.Kind.PLAIN),
                BlockBehaviour.Properties.of().strength(1.0f).sound(SoundType.METAL));
        STICKY_FRAME = block("sticky_frame",
                p -> new FrameBlock(p, FrameBlock.Kind.STICKY),
                BlockBehaviour.Properties.of().strength(1.0f).sound(SoundType.METAL)
                        .friction(0.8f));
        ANCHORED_FRAME = block("anchored_frame",
                p -> new FrameBlock(p, FrameBlock.Kind.ANCHORED),
                BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.METAL));
        FRAME_MOTOR = block("frame_motor", FrameMotorBlock::new,
                BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.METAL));
        SOLAR_PANEL = machine("solar_panel", SolarPanelBlockEntity::new);
        WIND_MILL = machine("wind_mill", WindMillBlockEntity::new);
        GEOTHERMAL_GENERATOR = machine("geothermal_generator",
                GeothermalGeneratorBlockEntity::new);
        BAT_BOX = machine("bat_box", BatBoxBlockEntity::new);
        BUS_CONVERTER = block("bus_converter", BusConverterBlock::new,
                BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.STONE)
                        .isRedstoneConductor((state, level, pos) -> false));
        BUS_TRANSCEIVER = block("bus_transceiver", BusTransceiverBlock::new,
                BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.STONE)
                        .isRedstoneConductor((state, level, pos) -> false));
        CHARGE_PAD = machine("charge_pad", ChargePadBlockEntity::new);
        COPPER_CABLE = block("copper_cable",
                p -> new EuCableBlock(p, 256, 4), wireProps());
        HV_CABLE = block("hv_cable",
                p -> new EuCableBlock(p, 4096, 64), wireProps());
        BUS_RANDOMIZER = block("bus_randomizer", BusRandomizerBlock::new,
                BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.STONE)
                        .isRedstoneConductor((state, level, pos) -> false));
    }

    private static BlockBehaviour.Properties wireProps() {
        // non-conductor: direct signals in must not leak out as weak power
        return BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.STONE)
                .isRedstoneConductor((state, level, pos) -> false);
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
                case CompressorBlockEntity e -> CompressorBlockEntity.tick(level, pos, state, e);
                case SolarPanelBlockEntity e -> SolarPanelBlockEntity.tick(level, pos, state, e);
                case WindMillBlockEntity e -> WindMillBlockEntity.tick(level, pos, state, e);
                case GeothermalGeneratorBlockEntity e ->
                        GeothermalGeneratorBlockEntity.tick(level, pos, state, e);
                case BatBoxBlockEntity e -> BatBoxBlockEntity.tick(level, pos, state, e);
                case ChargePadBlockEntity e -> ChargePadBlockEntity.tick(level, pos, state, e);
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
                    if (player.isShiftKeyDown()) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "[BCN ATM] 引き出し " + atm.withdraw(sp, 500) + " MD"
                                        + " (残高=" + com.buildcraftneo.data.Profiles.md(sp) + ")"));
                    } else {
                        sp.openMenu(new net.minecraft.world.SimpleMenuProvider(
                                (id, inv, pl) -> new AtmMenu(id, inv,
                                        new net.minecraft.world.SimpleContainer(
                                                AtmMenu.DEPOSIT_SLOTS),
                                        atm.getBlockPos()),
                                net.minecraft.network.chat.Component.literal("ATM")));
                    }
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
