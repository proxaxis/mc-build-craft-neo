package com.buildcraftneo.industry;

import com.buildcraftneo.dim.BossManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Boss altar: summons the Twilight Knight boss when activated
 * (right-click or /bcn dungeon summon). One boss per altar at a time.
 */
public class BossAltarBlockEntity extends MachineBlockEntity {
    private String lastBoss = "-";

    public BossAltarBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.BOSS_ALTAR, pos, state, 0);
    }

    public boolean summonBoss(ServerLevel level) {
        if (BossManager.bossAliveAt(level, getBlockPos(), 32)) {
            lastBoss = "alive-nearby";
            return false;
        }
        boolean skyreach = level.dimension().identifier().getPath().equals("skyreach")
                && level.dimension().identifier().getNamespace().equals("buildcraftneo");
        net.minecraft.world.entity.Mob boss = skyreach
                ? skyreachGuardian(level) : twilightKnight(level);
        boss.setPos(getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0,
                getBlockPos().getZ() + 0.5);
        boss.setPersistenceRequired();
        boss.addTag("bcn_boss");
        level.addFreshEntity(boss);
        BossManager.track(boss, level.getServer());
        lastBoss = boss.getUUID().toString().substring(0, 8);
        setChanged();
        return true;
    }

    private static Zombie twilightKnight(ServerLevel level) {
        Zombie boss = new Zombie(EntityTypes.ZOMBIE, level);
        boss.setCustomName(net.minecraft.network.chat.Component.literal("Twilight Knight"));
        var hp = boss.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(200.0);
            boss.setHealth(200.0f);
        }
        var atk = boss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (atk != null) atk.setBaseValue(9.0);
        boss.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
        boss.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
        boss.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
        boss.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        boss.setDropChance(EquipmentSlot.HEAD, 0.0f);
        boss.setDropChance(EquipmentSlot.CHEST, 0.0f);
        return boss;
    }

    private static net.minecraft.world.entity.monster.Ravager skyreachGuardian(ServerLevel level) {
        var boss = new net.minecraft.world.entity.monster.Ravager(
                net.minecraft.world.entity.EntityTypes.RAVAGER, level);
        boss.setCustomName(net.minecraft.network.chat.Component.literal("Skyreach Guardian"));
        var hp = boss.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(300.0);
            boss.setHealth(300.0f);
        }
        var atk = boss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (atk != null) atk.setBaseValue(14.0);
        return boss;
    }

    @Override
    public String status() {
        return super.status() + " boss=" + lastBoss;
    }
}
