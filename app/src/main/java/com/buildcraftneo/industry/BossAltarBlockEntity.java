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
    private static final String[] TWILIGHT_BOSSES = {"Twilight Knight", "Naga", "Lich"};

    private String lastBoss = "-";
    private int boss;

    public BossAltarBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.BOSS_ALTAR, pos, state, 0);
    }

    /** Cycle the twilight-dimension boss selection. Returns the new boss name. */
    public String cycleBoss() {
        boss = (boss + 1) % TWILIGHT_BOSSES.length;
        setChanged();
        return TWILIGHT_BOSSES[boss];
    }

    public String bossName() {
        return TWILIGHT_BOSSES[boss];
    }

    public void setBoss(int index) {
        boss = Math.floorMod(index, TWILIGHT_BOSSES.length);
        setChanged();
    }

    public boolean summonBoss(ServerLevel level) {
        if (BossManager.bossAliveAt(level, getBlockPos(), 32)) {
            lastBoss = "alive-nearby";
            return false;
        }
        String dim = level.dimension().identifier().toString();
        net.minecraft.world.entity.Mob boss = switch (dim) {
            case "buildcraftneo:skyreach" -> skyreachGuardian(level);
            case "minecraft:overworld" -> netherMonstrosity(level);
            default -> twilightBoss(level);
        };
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

    private net.minecraft.world.entity.Mob twilightBoss(ServerLevel level) {
        return switch (boss) {
            case 1 -> naga(level);
            case 2 -> lich(level);
            default -> twilightKnight(level);
        };
    }

    /** Twilight Forest-style Naga: a huge segmented serpent approximation (giant slime). */
    private static net.minecraft.world.entity.monster.cubemob.Slime naga(ServerLevel level) {
        var boss = new net.minecraft.world.entity.monster.cubemob.Slime(
                net.minecraft.world.entity.EntityTypes.SLIME, level);
        boss.setSize(6, true);
        boss.setCustomName(net.minecraft.network.chat.Component.literal("Naga"));
        var hp = boss.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(300.0);
            boss.setHealth(300.0f);
        }
        return boss;
    }

    /** Twilight Forest-style Lich: spellcasting boss (evoker approximation). */
    private static net.minecraft.world.entity.monster.illager.Evoker lich(ServerLevel level) {
        var boss = new net.minecraft.world.entity.monster.illager.Evoker(
                net.minecraft.world.entity.EntityTypes.EVOKER, level);
        boss.setCustomName(net.minecraft.network.chat.Component.literal("Lich"));
        var hp = boss.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(250.0);
            boss.setHealth(250.0f);
        }
        return boss;
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

    /** Cataclysm-style raid boss for the overworld altar. */
    private static net.minecraft.world.entity.monster.skeleton.WitherSkeleton netherMonstrosity(
            ServerLevel level) {
        var boss = new net.minecraft.world.entity.monster.skeleton.WitherSkeleton(
                net.minecraft.world.entity.EntityTypes.WITHER_SKELETON, level);
        boss.setCustomName(net.minecraft.network.chat.Component.literal("Nether Monstrosity"));
        var hp = boss.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(350.0);
            boss.setHealth(350.0f);
        }
        var atk = boss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (atk != null) atk.setBaseValue(16.0);
        boss.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
        boss.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        return boss;
    }

    @Override
    public String status() {
        return super.status() + " boss=" + TWILIGHT_BOSSES[boss] + " last=" + lastBoss;
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("boss", boss);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput in) {
        super.loadAdditional(in);
        boss = in.getIntOr("boss", 0);
    }
}
