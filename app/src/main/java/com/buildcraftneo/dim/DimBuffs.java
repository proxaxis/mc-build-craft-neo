package com.buildcraftneo.dim;

import com.buildcraftneo.BCN;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

/**
 * Dimension-specific mob scaling: monsters that spawn in the custom
 * dimensions are stronger than their overworld counterparts.
 * Twilight mobs are tankier, Skyreach mobs hit harder.
 */
public final class DimBuffs {
    private DimBuffs() {}

    private static final String TAG = "bcn_buffed";

    private record Scaling(double hp, double damage, String prefix) {}

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof Monster monster) || entity.entityTags().contains(TAG)) return;
            Identifier dim = level.dimension().identifier();
            Scaling s = scalingFor(dim);
            if (s == null) return;
            entity.addTag(TAG);
            var hp = monster.getAttribute(Attributes.MAX_HEALTH);
            if (hp != null) {
                hp.setBaseValue(hp.getBaseValue() * s.hp());
                monster.setHealth(monster.getMaxHealth());
            }
            var atk = monster.getAttribute(Attributes.ATTACK_DAMAGE);
            if (atk != null) atk.setBaseValue(atk.getBaseValue() * s.damage());
            monster.setCustomName(Component.literal(s.prefix()
                    + entity.getType().toShortString()));
        });
    }

    private static Scaling scalingFor(Identifier dim) {
        if (dim.equals(BCN.id("twilight"))) return new Scaling(3.0, 2.0, "Twilight ");
        if (dim.equals(BCN.id("skyreach"))) return new Scaling(2.0, 3.0, "Skyreach ");
        return null;
    }
}
