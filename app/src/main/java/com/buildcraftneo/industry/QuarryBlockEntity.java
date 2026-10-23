package com.buildcraftneo.industry;

import com.buildcraftneo.data.Profiles;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Quarry: consumes 50 MJ per block and mines a 5x5 column downward,
 * collecting drops into its 9-slot output.
 */
public class QuarryBlockEntity extends MachineBlockEntity {
    public static final long MJ_PER_BLOCK = 50;
    public static final int RADIUS = 2; // 5x5
    public static final int PERIOD = 10; // ticks between digs

    /** Tool passed to the loot context so tool-gated drops are produced. */
    private static final ItemStack VIRTUAL_TOOL = new ItemStack(
            net.minecraft.world.item.Items.NETHERITE_PICKAXE);

    private int cursorX = -RADIUS;
    private int cursorZ = -RADIUS;
    private int cursorY = 1; // relative to pos.y - 1, counts downward
    private boolean done;

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.QUARRY, pos, state, 9);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, QuarryBlockEntity self) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (self.done || serverLevel.getGameTime() % PERIOD != 0) return;
        if (!self.consumeMj(MJ_PER_BLOCK)) return;

        BlockPos target = self.nextTarget(serverLevel, pos);
        if (target == null) {
            self.done = true;
            return;
        }
        BlockState targetState = serverLevel.getBlockState(target);
        List<ItemStack> drops = Block.getDrops(targetState, serverLevel, target,
                serverLevel.getBlockEntity(target), null, VIRTUAL_TOOL);
        serverLevel.destroyBlock(target, false);
        for (ItemStack drop : drops) {
            insertOrDrop(serverLevel, pos, self, drop);
        }
        Profiles.addEfp(serverLevel.getServer().overworld(), 1.0);
        self.setChanged();
    }

    /** Advance cursor; returns next minable pos or null when finished. */
    private BlockPos nextTarget(ServerLevel level, BlockPos pos) {
        while (cursorY < 64) {
            BlockPos target = pos.offset(cursorX, -cursorY, cursorZ);
            BlockState s = level.getBlockState(target);
            advanceCursor();
            if (s.isAir() || s.getDestroySpeed(level, target) < 0) continue;
            return target;
        }
        return null;
    }

    private void advanceCursor() {
        cursorX++;
        if (cursorX > RADIUS) {
            cursorX = -RADIUS;
            cursorZ++;
            if (cursorZ > RADIUS) {
                cursorZ = -RADIUS;
                cursorY++;
            }
        }
    }

    private static void insertOrDrop(ServerLevel level, BlockPos pos, QuarryBlockEntity self, ItemStack stack) {
        ItemStack rest = self.insert(stack);
        if (!rest.isEmpty()) {
            Block.popResource(level, pos.above(), rest);
        }
    }

    /** Insert stack into our output slots; returns leftover. */
    public ItemStack insert(ItemStack stack) {
        for (int i = 0; i < items.size() && !stack.isEmpty(); i++) {
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

    public boolean isDone() {
        return done;
    }

    @Override
    public String status() {
        return super.status() + String.format(" cursor=(%d,%d,%d) done=%s", cursorX, cursorY, cursorZ, done);
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("cx", cursorX);
        out.putInt("cy", cursorY);
        out.putInt("cz", cursorZ);
        out.putBoolean("done", done);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        cursorX = in.getIntOr("cx", -RADIUS);
        cursorY = in.getIntOr("cy", 1);
        cursorZ = in.getIntOr("cz", -RADIUS);
        done = in.getBooleanOr("done", false);
    }
}
