package com.buildcraftneo.item;

import net.minecraft.world.item.Item;

/**
 * IC2-style powered armor piece: a wearable item that also carries an EU
 * charge ({@link EuChargeable}). {@code ArmorEffects} drains charge each
 * time it refreshes the piece's buff, so an uncharged suit grants nothing -
 * matching the IC2 nano/quantum behavior where dead armor is just armor.
 */
public class EuArmorItem extends Item implements EuChargeable {
    private final int euCapacity;

    public EuArmorItem(Properties properties, int euCapacity) {
        super(properties);
        this.euCapacity = euCapacity;
    }

    @Override
    public int euCapacity() {
        return euCapacity;
    }
}
