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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Macerator: doubles ores/raw materials into dusts. 100 MJ + 40 ticks per operation.
 * Slot 0 = input, slots 1-2 = output.
 */
public class MaceratorBlockEntity extends MachineBlockEntity {
    public static final long MJ_PER_OP = 100;
    public static final int TICKS_PER_OP = 40;

    private int progress;

    /** ore/raw input item -> dust output item */
    private static final Map<Item, Item> RECIPES = Map.ofEntries(
            Map.entry(Items.IRON_ORE, BCNItems.IRON_DUST),
            Map.entry(Items.DEEPSLATE_IRON_ORE, BCNItems.IRON_DUST),
            Map.entry(Items.RAW_IRON, BCNItems.IRON_DUST),
            Map.entry(Items.GOLD_ORE, BCNItems.GOLD_DUST),
            Map.entry(Items.DEEPSLATE_GOLD_ORE, BCNItems.GOLD_DUST),
            Map.entry(Items.RAW_GOLD, BCNItems.GOLD_DUST),
            Map.entry(Items.COPPER_ORE, BCNItems.COPPER_DUST),
            Map.entry(Items.DEEPSLATE_COPPER_ORE, BCNItems.COPPER_DUST),
            Map.entry(Items.RAW_COPPER, BCNItems.COPPER_DUST),
            Map.entry(Items.COAL_ORE, Items.COAL),
            Map.entry(Items.DEEPSLATE_COAL_ORE, Items.COAL),
            Map.entry(Items.DIAMOND_ORE, Items.DIAMOND),
            Map.entry(Items.DEEPSLATE_DIAMOND_ORE, Items.DIAMOND),
            Map.entry(Blocks.COBBLESTONE.asItem(), Blocks.GRAVEL.asItem()),
            Map.entry(Blocks.GRAVEL.asItem(), Blocks.SAND.asItem()));

    public MaceratorBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.MACERATOR, pos, state, 3);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MaceratorBlockEntity self) {
        if (level.isClientSide()) return;
        ItemStack in = self.items.get(0);
        Item out = in.isEmpty() ? null : RECIPES.get(in.getItem());
        if (out == null) {
            self.progress = 0;
            return;
        }
        if (!self.consumeMj(MJ_PER_OP / TICKS_PER_OP)) {
            return; // trickle charge per tick: 100/40 = 2.5 -> use integer trickle
        }
        self.progress++;
        if (self.progress >= TICKS_PER_OP) {
            self.progress = 0;
            in.shrink(1);
            insertOrDrop(level, pos, self, new ItemStack(out, 2));
            if (level instanceof ServerLevel sl) {
                Profiles.addEfp(sl.getServer().overworld(), 0.5);
            }
            self.setChanged();
        }
    }

    private static void insertOrDrop(Level level, BlockPos pos, MaceratorBlockEntity self, ItemStack stack) {
        ItemStack rest = self.insertOutput(stack);
        if (!rest.isEmpty()) {
            net.minecraft.world.level.block.Block.popResource(level, pos.above(), rest);
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
