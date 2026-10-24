package com.buildcraftneo.entity;

import com.buildcraftneo.item.BCNItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Point Blank-style impact grenade projectile: explodes the moment it hits
 * a block or an entity, unlike the timed {@code grenade} which lobs a
 * short-fuse primed TNT.
 */
public class GrenadeEntity extends ThrowableItemProjectile {
    public static final float POWER = 2.5f;

    public GrenadeEntity(EntityType<? extends ThrowableItemProjectile> type,
            Level level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return BCNItems.IMPACT_GRENADE;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide()) {
            level().explode(this, getX(), getY(), getZ(), POWER,
                    Level.ExplosionInteraction.TNT);
            discard();
        }
    }
}
