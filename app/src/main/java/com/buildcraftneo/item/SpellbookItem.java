package com.buildcraftneo.item;

import com.buildcraftneo.data.Faction;
import com.buildcraftneo.data.Job;
import com.buildcraftneo.data.Profiles;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * Spellbook (Iron's Spells analogue): holds several spells. Sneak+use cycles
 * the selected spell, use casts it (MP cost per spell). Requires magic faction
 * or a caster job.
 */
public class SpellbookItem extends Item {

    public record Spell(String name, int mpCost, int cooldown,
            BiConsumer<ServerPlayer, ServerLevel> effect) {}

    public static final List<Spell> SPELLS = List.of(
            new Spell("heal", 15, 160, Abilities::heal),
            new Spell("lightning", 25, 200, Abilities::lightning),
            new Spell("blink", 10, 80, Abilities::blink),
            new Spell("holy_smite", 20, 240, Abilities::holySmite),
            new Spell("frost_nova", 20, 240, Abilities::frostNova),
            new Spell("meteor", 35, 300, Abilities::meteor),
            new Spell("fortify", 20, 300, Abilities::fortify));

    public SpellbookItem(Properties properties) {
        super(properties);
    }

    private static boolean canCast(ServerPlayer sp) {
        if (Profiles.faction(sp) == Faction.MAGIC) return true;
        Job j = Profiles.job(sp);
        return j == Job.MAGE || j == Job.HEALER || j == Job.PALADIN || j == Job.PRIEST;
    }

    public static int selected(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        return data.copyTag().getIntOr("spell", 0);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        int idx = selected(stack);

        if (sp.isShiftKeyDown()) {
            int next = (idx + 1) % SPELLS.size();
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> t.putInt("spell", next));
            sp.sendSystemMessage(Component.literal(
                    "[BCN] スペル選択: " + SPELLS.get(next).name()
                            + " (MP " + SPELLS.get(next).mpCost() + ")"));
            return InteractionResult.SUCCESS_SERVER;
        }

        if (!canCast(sp)) {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] スペルブックは魔法陣営またはキャスター職専用です"));
            return InteractionResult.FAIL;
        }
        Spell spell = SPELLS.get(idx % SPELLS.size());
        if (!Profiles.consumeMp(sp, spell.mpCost())) {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] MP が不足しています (" + spell.mpCost() + " 必要)"));
            return InteractionResult.FAIL;
        }
        player.getCooldowns().addCooldown(stack, spell.cooldown());
        spell.effect().accept(sp, (ServerLevel) level);
        sp.sendSystemMessage(Component.literal("[BCN] 詠唱: " + spell.name()));
        return InteractionResult.SUCCESS_SERVER;
    }
}
