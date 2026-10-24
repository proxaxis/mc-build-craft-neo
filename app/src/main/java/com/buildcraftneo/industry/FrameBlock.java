package com.buildcraftneo.industry;

import net.minecraft.world.level.block.Block;

/**
 * ProjectRed-style frame lattice block. Three kinds:
 * <ul>
 *   <li>{@code PLAIN} - moves with the structure but grabs nothing.</li>
 *   <li>{@code STICKY} - moves and carries every movable block it touches
 *       (like a slime block glued into the lattice).</li>
 *   <li>{@code ANCHORED} - part of the structure but pinned to the world:
 *       if a motor's structure contains one, the whole move is refused.</li>
 * </ul>
 */
public class FrameBlock extends Block {
    public enum Kind { PLAIN, STICKY, ANCHORED }

    private final Kind kind;

    public FrameBlock(Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }
}
