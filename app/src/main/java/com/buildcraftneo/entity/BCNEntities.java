package com.buildcraftneo.entity;

import com.buildcraftneo.BCN;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;

/** Entity registrations. */
public final class BCNEntities {
    private BCNEntities() {}

    public static EntityType<GrenadeEntity> GRENADE;

    public static void register() {
        Identifier id = BCN.id("grenade");
        GRENADE = Registry.register(BuiltInRegistries.ENTITY_TYPE, id,
                EntityType.Builder
                        .<GrenadeEntity>of(GrenadeEntity::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(4)
                        .updateInterval(10)
                        .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));
    }
}
