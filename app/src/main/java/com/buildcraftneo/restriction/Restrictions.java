package com.buildcraftneo.restriction;

import com.buildcraftneo.data.Faction;
import com.buildcraftneo.data.Job;
import com.buildcraftneo.data.Profiles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;

/**
 * ItemLocks-style equipment restriction enforcing faction/job rules.
 */
public final class Restrictions {
    private Restrictions() {}

    private static final EquipmentSlot[] EQUIPPED = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND,
    };

    public static void register() {
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (player instanceof ServerPlayer sp && forbidden(sp, player.getItemInHand(hand))) {
                deny(sp, player.getItemInHand(hand));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (player instanceof ServerPlayer sp && forbidden(sp, player.getItemInHand(hand))) {
                deny(sp, player.getItemInHand(hand));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // periodical sweep: strip forbidden equipped items back to inventory
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 40 != 0) return;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                for (EquipmentSlot slot : EQUIPPED) {
                    ItemStack stack = player.getItemBySlot(slot);
                    if (!stack.isEmpty() && forbidden(player, stack)) {
                        player.setItemSlot(slot, ItemStack.EMPTY);
                        if (!player.getInventory().add(stack)) {
                            player.drop(stack, false, net.minecraft.util.Prediction.SERVER_ONLY);
                        }
                        deny(player, stack);
                    }
                }
            }
        });
    }

    /** Whether the player is forbidden from using the item under faction/job rules. */
    public static boolean forbidden(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return false;
        Faction faction = Profiles.faction(player);
        Job job = Profiles.job(player);

        if (isSpellItem(stack)) {
            return faction != Faction.MAGIC;
        }
        if (isGun(stack)) {
            return faction != Faction.SCIENCE;
        }
        return switch (faction) {
            case SCIENCE -> isFantasyWeapon(stack);
            case MAGIC -> isRangedWeapon(stack) && job != Job.ARCHER;
            case NONE -> false;
        };
    }

    /** Fantasy melee/ranged weapons sealed for the science faction. */
    public static boolean isFantasyWeapon(ItemStack stack) {
        return stack.is(ItemTags.SWORDS)
                || stack.is(ItemTags.SPEARS)
                || stack.getItem() instanceof MaceItem
                || stack.getItem() instanceof TridentItem
                || isRangedWeapon(stack);
    }

    /** Bow and crossbow (restricted to Archer job inside the magic faction). */
    public static boolean isRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem;
    }

    public static boolean isBowOrCrossbow(ItemStack stack) {
        return stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem;
    }

    /** Our own spell items (magic faction only). */
    public static boolean isSpellItem(ItemStack stack) {
        return stack.getItem() instanceof com.buildcraftneo.item.SpellItem;
    }

    /** Our own gun items (science faction only). */
    public static boolean isGun(ItemStack stack) {
        return stack.getItem() instanceof com.buildcraftneo.item.GunItem;
    }

    private static void deny(ServerPlayer player, ItemStack stack) {
        player.sendSystemMessage(Component.literal(
                "[BCN] " + stack.getHoverName().getString() + " はあなたの陣営/職業では使用できません"));
    }
}
