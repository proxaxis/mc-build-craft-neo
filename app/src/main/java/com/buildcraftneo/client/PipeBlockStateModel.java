package com.buildcraftneo.client;

import com.buildcraftneo.industry.BCNBlocks;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BuildCraft-style dynamic pipe model. Quads are emitted per block position:
 * a thin center box plus an arm for every connected side (pipe or inventory).
 * Connections are read from the world in {@link #emitQuads}, so the shape
 * updates with the neighbourhood instead of depending on blockstates.
 */
public class PipeBlockStateModel implements BlockStateModel {
    private static final float MIN = 5f / 16f, MAX = 11f / 16f;

    private final Material.Baked material;
    private final ItemPart itemPart = new ItemPart();

    public PipeBlockStateModel(Material.Baked material) {
        this.material = material;
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter world, BlockPos pos,
            BlockState state, RandomSource random, Predicate<Direction> cullTest) {
        emitBox(emitter, MIN, MIN, MIN, MAX, MAX, MAX);
        for (Direction d : Direction.values()) {
            if (connects(world, pos, d)) {
                emitArm(emitter, d);
            }
        }
    }

    private static boolean connects(BlockAndTintGetter world, BlockPos pos, Direction d) {
        BlockPos np = pos.relative(d);
        Block n = world.getBlockState(np).getBlock();
        if (n == BCNBlocks.ITEM_PIPE || n == BCNBlocks.TELEPORT_PIPE
                || n == BCNBlocks.REQUEST_PIPE) {
            return true;
        }
        return world.getBlockEntity(np) instanceof Container;
    }

    private void emitArm(QuadEmitter e, Direction d) {
        switch (d) {
            case NORTH -> emitBox(e, MIN, MIN, 0f, MAX, MAX, MIN);
            case SOUTH -> emitBox(e, MIN, MIN, MAX, MAX, MAX, 1f);
            case WEST -> emitBox(e, 0f, MIN, MIN, MIN, MAX, MAX);
            case EAST -> emitBox(e, MAX, MIN, MIN, 1f, MAX, MAX);
            case DOWN -> emitBox(e, MIN, 0f, MIN, MAX, MIN, MAX);
            case UP -> emitBox(e, MIN, MAX, MIN, MAX, 1f, MAX);
        }
    }

    private void emitBox(QuadEmitter e, float x0, float y0, float z0,
            float x1, float y1, float z1) {
        quad(e, Direction.DOWN, x0, z0, x1, z1, y0);
        quad(e, Direction.UP, x0, z0, x1, z1, 1f - y1);
        quad(e, Direction.NORTH, x0, y0, x1, y1, z0);
        quad(e, Direction.SOUTH, x0, y0, x1, y1, 1f - z1);
        quad(e, Direction.WEST, z0, y0, z1, y1, x0);
        quad(e, Direction.EAST, z0, y0, z1, y1, 1f - x1);
    }

    private void quad(QuadEmitter e, Direction d, float l, float b, float r, float t,
            float depth) {
        e.square(d, l, b, r, t, depth);
        e.materialBake(material, MutableQuadView.BAKE_ROTATE_NONE);
        if (depth <= 0f) {
            e.cullFace(d);
        }
        e.emit();
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> out) {
        out.add(itemPart);
    }

    @Override
    public Material.Baked particleMaterial() {
        return material;
    }

    @Override
    public int materialFlags() {
        return 0;
    }

    /** Static straight-pipe geometry used for item rendering. */
    private class ItemPart implements BlockStateModelPart {
        private List<BakedQuad> all;
        private Map<Direction, List<BakedQuad>> byFace;

        private void build() {
            MutableMesh mesh = Renderer.get().mutableMesh();
            QuadEmitter e = mesh.emitter();
            emitBox(e, MIN, MIN, MIN, MAX, MAX, MAX);
            emitArm(e, Direction.NORTH);
            emitArm(e, Direction.SOUTH);
            all = new ArrayList<>();
            byFace = new EnumMap<>(Direction.class);
            mesh.forEach(qv -> {
                BakedQuad q = qv.toBakedQuad(material.sprite());
                all.add(q);
                byFace.computeIfAbsent(q.direction(), k -> new ArrayList<>()).add(q);
            });
        }

        @Override
        public List<BakedQuad> getQuads(Direction direction) {
            if (all == null) build();
            return direction == null ? all
                    : byFace.getOrDefault(direction, List.of());
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public Material.Baked particleMaterial() {
            return material;
        }

        @Override
        public int materialFlags() {
            return 0;
        }
    }
}
