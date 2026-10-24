package com.buildcraftneo.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * IC2-style rechargeable item: charge lives in {@code CUSTOM_DATA.eu} and is
 * refilled by energy stores such as {@code bat_box} or {@code charge_pad}.
 */
public interface EuChargeable {
    int euCapacity();

    default int eu(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? 0 : data.copyTag().getIntOr("eu", 0);
    }

    /** Add up to {@code amount} EU, returns how much was accepted. */
    default long chargeEu(ItemStack stack, long amount) {
        int cur = eu(stack);
        int add = (int) Math.min(amount, euCapacity() - cur);
        if (add > 0) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> t.putInt("eu", cur + add));
        }
        return add;
    }

    /** Drain EU, returns false when insufficient charge. */
    default boolean drainEu(ItemStack stack, int amount) {
        int cur = eu(stack);
        if (cur < amount) return false;
        int left = cur - amount;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
            if (left <= 0) t.remove("eu");
            else t.putInt("eu", left);
        });
        return true;
    }
}
