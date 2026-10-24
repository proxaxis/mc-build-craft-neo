package com.buildcraftneo.industry;

import com.buildcraftneo.data.Profiles;
import com.buildcraftneo.item.BCNItems;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Compressor (IC2 analogue): compresses input stacks into dense outputs.
 * 300 MJ + 60 ticks per operation. Slot 0 = input, slots 1-2 = output.
 * Notable recipes: uranium_ore -> uranium_ingot (reactor fuel chain),
 * 8x coal_block -> diamond (IC2-style super-compression), 9x ingot -> block.
 */
public class CompressorBlockEntity extends MachineBlockEntity {
    public static final long MJ_PER_OP = 300;
    public static final int TICKS_PER_OP = 60;

    private int progress;

    private record Recipe(int inCount, Item out, int outCount) {}

    // Lazy: blocks/items must be registered before first tick touches this map.
    private static Map<Item, Recipe> recipes() {
        if (RECIPES == null) {
            RECIPES = Map.ofEntries(
                    Map.entry(BCNBlocks.URANIUM_ORE.asItem(),
                            new Recipe(1, BCNItems.URANIUM_INGOT, 1)),
                    Map.entry(Items.COAL_BLOCK, new Recipe(8, Items.DIAMOND, 1)),
                    Map.entry(Items.IRON_INGOT, new Recipe(9, Items.IRON_BLOCK, 1)),
                    Map.entry(Items.GOLD_INGOT, new Recipe(9, Items.GOLD_BLOCK, 1)),
                    Map.entry(Items.WHEAT, new Recipe(9, Items.HAY_BLOCK, 1)),
                    Map.entry(Items.CLAY_BALL, new Recipe(4, Items.CLAY, 1)),
                    Map.entry(Items.DIAMOND, new Recipe(9, Items.DIAMOND_BLOCK, 1)),
                    Map.entry(Items.EMERALD, new Recipe(9, Items.EMERALD_BLOCK, 1)),
                    Map.entry(Items.LAPIS_LAZULI, new Recipe(9, Items.LAPIS_BLOCK, 1)),
                    Map.entry(Items.REDSTONE, new Recipe(9, Items.REDSTONE_BLOCK, 1)),
                    Map.entry(Items.QUARTZ, new Recipe(4, Items.QUARTZ_BLOCK, 1)),
                    Map.entry(Items.SNOWBALL, new Recipe(4, Items.SNOW_BLOCK, 1)));
        }
        return RECIPES;
    }

    private static Map<Item, Recipe> RECIPES;

    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.COMPRESSOR, pos, state, 3);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CompressorBlockEntity self) {
        if (level.isClientSide()) return;
        ItemStack in = self.items.get(0);
        Recipe recipe = in.isEmpty() ? null : recipes().get(in.getItem());
        if (recipe == null || in.getCount() < recipe.inCount()) {
            self.progress = 0;
            return;
        }
        if (!self.consumeMj(MJ_PER_OP / TICKS_PER_OP)) {
            return;
        }
        self.progress++;
        if (self.progress >= TICKS_PER_OP) {
            self.progress = 0;
            in.shrink(recipe.inCount());
            ItemStack rest = self.insertOutput(new ItemStack(recipe.out(), recipe.outCount()));
            if (!rest.isEmpty()) {
                Block.popResource(level, pos.above(), rest);
            }
            if (level instanceof ServerLevel sl) {
                Profiles.addEfp(sl.getServer().overworld(), 0.5);
            }
            self.setChanged();
        }
    }

    private ItemStack insertOutput(ItemStack stack) {
        for (int i = 1; i < items.size() && !stack.isEmpty(); i++) {
            ItemStack cur = items.get(i);
            if (cur.isEmpty()) {
                items.set(i, stack.copy());
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameComponents(cur, stack)) {
                int room = Math.min(cur.getMaxStackSize(), getMaxStackSize()) - cur.getCount();
                int move = Math.min(room, stack.getCount());
                cur.grow(move);
                stack.shrink(move);
            }
        }
        return stack;
    }

    @Override
    public int progressPercent() {
        return progress * 100 / TICKS_PER_OP;
    }

    @Override
    public String status() {
        return super.status() + " progress=" + progress + "/" + TICKS_PER_OP;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        progress = in.getIntOr("progress", 0);
    }
}
