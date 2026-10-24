package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Crafting pipe (LogisticsPipes analogue): configured with a simple recipe
 * (inputCount x input -> outputCount x output). Periodically pulls ingredients
 * from the connected pipe network and deposits the crafted result into an
 * adjacent container.
 */
public class CraftingPipeBlockEntity extends MachineBlockEntity {
    public static final int PERIOD = 40;

    private Item input;
    private int inputCount;
    private Item output;
    private int outputCount;
    private ItemStack transit = ItemStack.EMPTY;
    private String lastAct = "-";

    public CraftingPipeBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.CRAFTING_PIPE, pos, state, 0);
    }

    public void setRecipe(Item input, int inputCount, Item output, int outputCount) {
        this.input = input;
        this.inputCount = Math.max(1, inputCount);
        this.output = output;
        this.outputCount = Math.max(1, outputCount);
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CraftingPipeBlockEntity self) {
        if (level.isClientSide() || level.getGameTime() % PERIOD != 0) return;

        if (!self.transit.isEmpty()) {
            self.transit = PipeNetwork.depositAdjacent(level, pos, self.transit);
            self.setChanged();
            return;
        }
        if (self.input == null || self.output == null) return;

        int have = PipeNetwork.countInNetwork(level, pos, self.input);
        if (have < self.inputCount) {
            self.lastAct = "missing " + have + "/" + self.inputCount;
            return;
        }
        ItemStack pulled = PipeNetwork.pullFromNetwork(level, pos, self.input, self.inputCount);
        if (pulled.getCount() < self.inputCount) {
            // race/shortage: send what we got back through the network
            if (!pulled.isEmpty()) self.transit = PipeNetwork.route(level, pos, pulled, pos);
            self.lastAct = "short " + pulled.getCount() + "/" + self.inputCount;
            self.setChanged();
            return;
        }
        self.transit = PipeNetwork.depositAdjacent(level, pos,
                new ItemStack(self.output, self.outputCount));
        self.lastAct = "crafted " + self.outputCount + "x "
                + BuiltInRegistries.ITEM.getKey(self.output);
        self.setChanged();
    }

    @Override
    public String status() {
        return super.status() + " recipe=" + (input == null ? "-" :
                inputCount + "x" + BuiltInRegistries.ITEM.getKey(input)
                        + "->" + outputCount + "x" + BuiltInRegistries.ITEM.getKey(output))
                + " last=" + lastAct;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        if (input != null) out.putString("input", BuiltInRegistries.ITEM.getKey(input).toString());
        out.putInt("inputCount", inputCount);
        if (output != null) out.putString("output", BuiltInRegistries.ITEM.getKey(output).toString());
        out.putInt("outputCount", outputCount);
        if (!transit.isEmpty()) out.store("transit", ItemStack.CODEC, transit);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        String i = in.getStringOr("input", "");
        String o = in.getStringOr("output", "");
        input = i.isEmpty() ? null : BuiltInRegistries.ITEM.getValue(Identifier.parse(i));
        output = o.isEmpty() ? null : BuiltInRegistries.ITEM.getValue(Identifier.parse(o));
        inputCount = in.getIntOr("inputCount", 0);
        outputCount = in.getIntOr("outputCount", 0);
        transit = in.read("transit", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
