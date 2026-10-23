package com.buildcraftneo.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.phys.Vec3;

/** Apprentice wand: shoots a small fireball. Costs 10 MP. */
public class FireballWandItem extends SpellItem {
    public FireballWandItem(Properties properties) {
        super(properties, 10);
    }

    @Override
    protected void cast(ServerPlayer caster) {
        Vec3 look = caster.getLookAngle();
        LargeFireball fireball = new LargeFireball(
                caster.level(), caster,
                new Vec3(look.x * 0.1, look.y * 0.1, look.z * 0.1), 1);
        fireball.setPos(caster.getX() + look.x, caster.getEyeY() + look.y, caster.getZ() + look.z);
        caster.level().addFreshEntity(fireball);
    }
}
