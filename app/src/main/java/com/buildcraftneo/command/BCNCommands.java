package com.buildcraftneo.command;

import com.buildcraftneo.data.BCNAttachments;
import com.buildcraftneo.data.Faction;
import com.buildcraftneo.data.Job;
import com.buildcraftneo.data.Profiles;
import com.buildcraftneo.restriction.Restrictions;
import com.buildcraftneo.test.TestFakes;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class BCNCommands {
    private BCNCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                register(dispatcher, registryAccess));
    }

    private static void register(CommandDispatcher<CommandSourceStack> d,
                                 net.minecraft.commands.CommandBuildContext ctx) {
        d.register(Commands.literal("bcn")
                .then(Commands.literal("info").executes(BCNCommands::info))
                .then(Commands.literal("status").then(Commands.argument("player", EntityArgument.player())
                        .executes(BCNCommands::status)))
                .then(Commands.literal("faction")
                        .then(Commands.literal("get").then(Commands.argument("player", EntityArgument.player())
                                .executes(BCNCommands::factionGet)))
                        .then(Commands.literal("set").then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("value", StringArgumentType.word())
                                        .executes(BCNCommands::factionSet)))))
                .then(Commands.literal("job")
                        .then(Commands.literal("get").then(Commands.argument("player", EntityArgument.player())
                                .executes(BCNCommands::jobGet)))
                        .then(Commands.literal("set").then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("value", StringArgumentType.word())
                                        .executes(BCNCommands::jobSet)))))
                .then(Commands.literal("md")
                        .then(Commands.literal("get").then(Commands.argument("player", EntityArgument.player())
                                .executes(BCNCommands::mdGet)))
                        .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(BCNCommands::mdAdd))))
                        .then(Commands.literal("pay").then(Commands.argument("from", EntityArgument.player())
                                .then(Commands.argument("to", EntityArgument.player())
                                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                                .executes(BCNCommands::mdPay))))))
                .then(Commands.literal("mp")
                        .then(Commands.literal("get").then(Commands.argument("player", EntityArgument.player())
                                .executes(BCNCommands::mpGet)))
                        .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(BCNCommands::mpAdd)))))
                .then(Commands.literal("efp")
                        .then(Commands.literal("get").executes(BCNCommands::efpGet))
                        .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(BCNCommands::efpAdd))))
                .then(Commands.literal("check").then(Commands.argument("player", EntityArgument.player())
                        .executes(BCNCommands::check)))
                .then(Commands.literal("disaster")
                        .then(Commands.literal("trigger").executes(BCNCommands::disasterTrigger))
                        .then(Commands.literal("threshold").then(Commands.argument("value",
                                        IntegerArgumentType.integer(1))
                                .executes(BCNCommands::disasterThreshold))))
                .then(Commands.literal("join").then(Commands.argument("value", StringArgumentType.word())
                        .executes(BCNCommands::joinFaction)))
                .then(Commands.literal("dim")
                        .then(Commands.literal("list").executes(BCNCommands::dimList))
                        .then(Commands.literal("tp").then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("dim", StringArgumentType.word())
                                        .executes(BCNCommands::dimTp)))))
                .then(Commands.literal("vein").then(Commands.argument("pos",
                                net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos())
                        .executes(BCNCommands::veinMine)))
                .then(Commands.literal("machine")
                        .then(Commands.literal("info").then(Commands.argument("pos",
                                        net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos())
                                .executes(BCNCommands::machineInfo)))
                        .then(Commands.literal("insert").then(Commands.argument("pos",
                                        net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos())
                                .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(ctx))
                                        .executes(BCNCommands::machineInsert))))
                        .then(Commands.literal("extract").then(Commands.argument("pos",
                                        net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos())
                                .executes(BCNCommands::machineExtract)))
                        .then(Commands.literal("mj").then(Commands.argument("pos",
                                        net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos())
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(BCNCommands::machineMj))))
                        .then(Commands.literal("request").then(Commands.argument("pos",
                                        net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos())
                                .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(ctx))
                                        .executes(BCNCommands::machineRequest)))))
                .then(Commands.literal("fake")
                        .then(Commands.literal("list").executes(BCNCommands::fakeList))
                        .then(Commands.literal("spawn").then(Commands.argument("name", StringArgumentType.word())
                                .executes(BCNCommands::fakeSpawn)))
                        .then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.word())
                                .executes(BCNCommands::fakeRemove)))
                        .then(Commands.literal("status").then(Commands.argument("name", StringArgumentType.word())
                                .executes(BCNCommands::fakeStatus)))
                        .then(Commands.literal("set").then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("attr", StringArgumentType.word())
                                        .then(Commands.argument("value", StringArgumentType.word())
                                                .executes(BCNCommands::fakeSet)))))
                        .then(Commands.literal("give").then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(ctx))
                                        .executes(BCNCommands::fakeGive))))
                        .then(Commands.literal("use").then(Commands.argument("name", StringArgumentType.word())
                                .executes(BCNCommands::fakeUse)))
                        .then(Commands.literal("deposit").then(Commands.argument("name", StringArgumentType.word())
                                .executes(BCNCommands::fakeDeposit)))
                        .then(Commands.literal("withdraw").then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                        .executes(BCNCommands::fakeWithdraw))))
                        .then(Commands.literal("jobeffects").then(Commands.argument("name", StringArgumentType.word())
                                .executes(BCNCommands::fakeJobEffects)))));
    }

    private static int info(CommandContext<CommandSourceStack> ctx) {
        tell(ctx, "BuildCraftNeo ok. Attachments registered. Use /bcn status <player>.");
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        tell(ctx, String.format("%s: faction=%s job=%s md=%d mp=%d/%d efp_own=%.1f",
                p.getName().getString(),
                Profiles.faction(p).id(), Profiles.job(p).id(),
                Profiles.md(p), Profiles.mp(p), Profiles.mpMax(p), Profiles.efpOwn(p)));
        return 1;
    }

    private static int factionGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        tell(ctx, p.getName().getString() + " faction=" + Profiles.faction(p).id());
        return 1;
    }

    private static int factionSet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        Faction f = Faction.byName(StringArgumentType.getString(ctx, "value"));
        Profiles.setFaction(p, f);
        tell(ctx, p.getName().getString() + " faction=" + Profiles.faction(p).id()
                + " job=" + Profiles.job(p).id());
        return 1;
    }

    private static int jobGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        tell(ctx, p.getName().getString() + " job=" + Profiles.job(p).id());
        return 1;
    }

    private static int jobSet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        Job j = Job.byName(StringArgumentType.getString(ctx, "value"));
        if (!j.isAllowedFor(Profiles.faction(p))) {
            ctx.getSource().sendFailure(Component.literal(
                    "Job " + j.id() + " is not allowed for faction " + Profiles.faction(p).id()));
            return 0;
        }
        Profiles.setJob(p, j);
        tell(ctx, p.getName().getString() + " job=" + Profiles.job(p).id());
        return 1;
    }

    private static int mdGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        tell(ctx, p.getName().getString() + " md=" + Profiles.md(p));
        return 1;
    }

    private static int mdAdd(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        long next = Profiles.addMd(p, LongArgumentType.getLong(ctx, "amount"));
        tell(ctx, p.getName().getString() + " md=" + next);
        return 1;
    }

    private static int mdPay(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer from = EntityArgument.getPlayer(ctx, "from");
        ServerPlayer to = EntityArgument.getPlayer(ctx, "to");
        long amount = LongArgumentType.getLong(ctx, "amount");
        if (Profiles.md(from) < amount) {
            ctx.getSource().sendFailure(Component.literal("Insufficient MD balance"));
            return 0;
        }
        Profiles.addMd(from, -amount);
        Profiles.addMd(to, amount);
        tell(ctx, "Paid " + amount + " MD: " + from.getName().getString() + " -> " + to.getName().getString());
        return 1;
    }

    private static int mpGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        tell(ctx, p.getName().getString() + " mp=" + Profiles.mp(p) + "/" + Profiles.mpMax(p));
        return 1;
    }

    private static int mpAdd(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        int next = Profiles.addMp(p, IntegerArgumentType.getInteger(ctx, "amount"));
        tell(ctx, p.getName().getString() + " mp=" + next + "/" + Profiles.mpMax(p));
        return 1;
    }

    private static int efpGet(CommandContext<CommandSourceStack> ctx) {
        double efp = Profiles.efp(ctx.getSource().getServer().overworld());
        tell(ctx, "world efp=" + efp);
        return 1;
    }

    private static int efpAdd(CommandContext<CommandSourceStack> ctx) {
        double next = Profiles.addEfp(ctx.getSource().getServer().overworld(),
                IntegerArgumentType.getInteger(ctx, "amount"));
        tell(ctx, "world efp=" + next);
        return 1;
    }

    private static int check(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = player(ctx);
        ItemStack stack = p.getMainHandItem();
        String name = stack.isEmpty() ? "(empty)" : stack.getHoverName().getString();
        tell(ctx, name + ": " + (Restrictions.forbidden(p, stack) ? "FORBIDDEN" : "allowed"));
        return 1;
    }

    // ---- fake player test harness ----

    private static int fakeList(CommandContext<CommandSourceStack> ctx) {
        tell(ctx, "fakes: " + TestFakes.list());
        return 1;
    }

    private static int fakeSpawn(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        TestFakes.spawn(ctx.getSource().getServer().overworld(), name);
        tell(ctx, "spawned fake player " + name);
        return 1;
    }

    private static int fakeRemove(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        tell(ctx, TestFakes.remove(name) ? "removed " + name : "no such fake " + name);
        return 1;
    }

    private static int fakeStatus(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = fake(ctx);
        if (p == null) return 0;
        tell(ctx, String.format("%s: faction=%s job=%s md=%d mp=%d/%d",
                p.getName().getString(), Profiles.faction(p).id(), Profiles.job(p).id(),
                Profiles.md(p), Profiles.mp(p), Profiles.mpMax(p)));
        return 1;
    }

    // ---- machine test helpers ----

    private static int machineInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var pos = net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        var be = ctx.getSource().getLevel().getBlockEntity(pos);
        if (be instanceof com.buildcraftneo.industry.MachineBlockEntity m) {
            tell(ctx, m.status());
            return 1;
        }
        ctx.getSource().sendFailure(Component.literal("no machine at " + pos.toShortString()
                + " (be=" + be + ")"));
        return 0;
    }

    private static int machineInsert(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var pos = net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        var be = ctx.getSource().getLevel().getBlockEntity(pos);
        if (!(be instanceof com.buildcraftneo.industry.MachineBlockEntity m)) {
            ctx.getSource().sendFailure(Component.literal("no machine at " + pos.toShortString()));
            return 0;
        }
        ItemStack stack = net.minecraft.commands.arguments.item.ItemArgument.getItem(ctx, "item")
                .createItemStack(8);
        for (int i = 0; i < m.getContainerSize() && !stack.isEmpty(); i++) {
            ItemStack cur = m.getItem(i);
            if (cur.isEmpty()) {
                m.setItem(i, stack.copy());
                return okMachine(ctx, m);
            }
            if (ItemStack.isSameItemSameComponents(cur, stack)) {
                int move = Math.min(cur.getMaxStackSize() - cur.getCount(), stack.getCount());
                cur.grow(move);
                stack.shrink(move);
                m.setChanged();
            }
        }
        tell(ctx, "inserted -> " + m.status());
        return 1;
    }

    private static int okMachine(CommandContext<CommandSourceStack> ctx,
            com.buildcraftneo.industry.MachineBlockEntity m) {
        tell(ctx, "inserted -> " + m.status());
        return 1;
    }

    private static int machineExtract(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var pos = net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        var be = ctx.getSource().getLevel().getBlockEntity(pos);
        if (!(be instanceof com.buildcraftneo.industry.MachineBlockEntity m)) {
            ctx.getSource().sendFailure(Component.literal("no machine at " + pos.toShortString()));
            return 0;
        }
        StringBuilder sb = new StringBuilder("extracted: ");
        for (int i = 0; i < m.getContainerSize(); i++) {
            ItemStack s = m.removeItemNoUpdate(i);
            if (!s.isEmpty()) {
                net.minecraft.world.level.block.Block.popResource(ctx.getSource().getLevel(), pos.above(), s);
                sb.append(s.getCount()).append("x")
                        .append(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(s.getItem()))
                        .append(" ");
            }
        }
        tell(ctx, sb.toString());
        return 1;
    }

    private static int machineMj(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var pos = net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        var be = ctx.getSource().getLevel().getBlockEntity(pos);
        if (!(be instanceof com.buildcraftneo.industry.MachineBlockEntity m)) {
            ctx.getSource().sendFailure(Component.literal("no machine at " + pos.toShortString()));
            return 0;
        }
        long leftover = m.accept(LongArgumentType.getLong(ctx, "amount"));
        tell(ctx, m.status() + " leftover=" + leftover);
        return 1;
    }

    private static int fakeSet(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = fake(ctx);
        if (p == null) return 0;
        String attr = StringArgumentType.getString(ctx, "attr");
        String value = StringArgumentType.getString(ctx, "value");
        try {
            switch (attr) {
                case "faction" -> Profiles.setFaction(p, Faction.byName(value));
                case "job" -> {
                    Job j = Job.byName(value);
                    if (!j.isAllowedFor(Profiles.faction(p))) {
                        ctx.getSource().sendFailure(Component.literal("job " + j.id()
                                + " not allowed for faction " + Profiles.faction(p).id()));
                        return 0;
                    }
                    Profiles.setJob(p, j);
                }
                case "md" -> Profiles.setMd(p, Long.parseLong(value));
                case "mp" -> Profiles.setMp(p, Integer.parseInt(value));
                case "mpmax" -> p.setAttached(BCNAttachments.MP_MAX, Integer.parseInt(value));
                default -> {
                    ctx.getSource().sendFailure(Component.literal("unknown attr " + attr));
                    return 0;
                }
            }
        } catch (NumberFormatException e) {
            ctx.getSource().sendFailure(Component.literal("bad number " + value));
            return 0;
        }
        tell(ctx, "set " + attr + " -> ok");
        return fakeStatus(ctx);
    }

    private static int fakeGive(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = fake(ctx);
        if (p == null) return 0;
        var input = net.minecraft.commands.arguments.item.ItemArgument.getItem(ctx, "item");
        ItemStack stack = input.createItemStack(1);
        p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, stack);
        tell(ctx, "gave " + stack.getHoverName().getString() + " to " + p.getName().getString()
                + " (forbidden=" + Restrictions.forbidden(p, stack) + ")");
        return 1;
    }

    private static int fakeUse(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = fake(ctx);
        if (p == null) return 0;
        ItemStack stack = p.getMainHandItem();
        if (stack.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("fake holds nothing"));
            return 0;
        }
        if (Restrictions.forbidden(p, stack)) {
            tell(ctx, "use blocked: " + stack.getHoverName().getString() + " is FORBIDDEN");
            return 0;
        }
        var result = stack.getItem().use(ctx.getSource().getServer().overworld(), p,
                net.minecraft.world.InteractionHand.MAIN_HAND);
        tell(ctx, "use result=" + result);
        return 1;
    }

    private static ServerPlayer fake(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        ServerPlayer p = TestFakes.get(name);
        if (p == null) {
            ctx.getSource().sendFailure(Component.literal("no fake player " + name
                    + " (spawn with /bcn fake spawn)"));
        }
        return p;
    }

    private static int disasterTrigger(CommandContext<CommandSourceStack> ctx) {
        int n = com.buildcraftneo.disaster.DisasterManager.triggerRaid(ctx.getSource().getServer());
        tell(ctx, "raid spawned " + n + " hostiles; efp="
                + Profiles.efp(ctx.getSource().getServer().overworld()));
        return n;
    }

    private static int disasterThreshold(CommandContext<CommandSourceStack> ctx) {
        com.buildcraftneo.disaster.DisasterManager.setThreshold(
                IntegerArgumentType.getInteger(ctx, "value"));
        tell(ctx, "disaster threshold=" + com.buildcraftneo.disaster.DisasterManager.threshold());
        return 1;
    }

    private static int veinMine(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var pos = net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        var level = ctx.getSource().getLevel();
        var state = level.getBlockState(pos);
        if (!com.buildcraftneo.util.VeinMiner.isVeinBlock(state)) {
            ctx.getSource().sendFailure(Component.literal("not a vein block: " + state));
            return 0;
        }
        // test with a dedicated fake player holding the right tool
        ServerPlayer fake = com.buildcraftneo.test.TestFakes.get("__vein_test__");
        if (fake == null) {
            fake = com.buildcraftneo.test.TestFakes.spawn(ctx.getSource().getServer().overworld(), "__vein_test__");
        }
        ItemStack tool = com.buildcraftneo.util.VeinMiner.isRightTool(state, fake.getMainHandItem())
                ? fake.getMainHandItem()
                : new ItemStack(net.minecraft.world.item.Items.NETHERITE_PICKAXE);
        int n = com.buildcraftneo.util.VeinMiner.veinMine(level, pos, fake, tool);
        tell(ctx, "vein-mined " + n + " extra " + net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .getKey(state.getBlock()) + " blocks");
        return n;
    }

    private static int fakeDeposit(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = fake(ctx);
        if (p == null) return 0;
        long dep = com.buildcraftneo.economy.Mint.depositAll(p);
        tell(ctx, "deposited " + dep + " MD -> balance=" + Profiles.md(p));
        return 1;
    }

    private static int fakeWithdraw(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = fake(ctx);
        if (p == null) return 0;
        long out = com.buildcraftneo.economy.Mint.withdraw(p, LongArgumentType.getLong(ctx, "amount"));
        tell(ctx, "withdrew " + out + " MD -> balance=" + Profiles.md(p));
        return 1;
    }

    private static int fakeJobEffects(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = fake(ctx);
        if (p == null) return 0;
        com.buildcraftneo.job.JobEffects.apply(p, ctx.getSource().getServer().getTickCount());
        tell(ctx, "applied job effects; speed=" + p.getAttributeValue(
                net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
                + " attack=" + p.getAttributeValue(
                net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
                + " mpmax=" + Profiles.mpMax(p));
        return 1;
    }

    private static int joinFaction(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        Faction f = Faction.byName(StringArgumentType.getString(ctx, "value"));
        if (f == Faction.NONE) {
            ctx.getSource().sendFailure(Component.literal("choose science or magic"));
            return 0;
        }
        if (Profiles.faction(p) != Faction.NONE) {
            ctx.getSource().sendFailure(Component.literal("already in faction " + Profiles.faction(p).id()));
            return 0;
        }
        Profiles.setFaction(p, f);
        p.sendSystemMessage(Component.literal("[BCN] 陣営 " + f.id() + " に参加しました"));
        tell(ctx, p.getName().getString() + " joined " + f.id());
        return 1;
    }

    private static int dimList(CommandContext<CommandSourceStack> ctx) {
        StringBuilder sb = new StringBuilder("levels:");
        for (var l : ctx.getSource().getServer().getAllLevels()) {
            sb.append(" ").append(l.dimension().identifier());
        }
        tell(ctx, sb.toString());
        return 1;
    }

    private static int dimTp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = EntityArgument.getPlayer(ctx, "player");
        String dim = StringArgumentType.getString(ctx, "dim");
        var id = net.minecraft.resources.Identifier.tryParse(dim);
        var target = id == null ? null : ctx.getSource().getServer().getLevel(
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id));
        if (target == null) {
            ctx.getSource().sendFailure(Component.literal("no dimension " + dim));
            return 0;
        }
        var pos = target.getRespawnData().pos();
        p.teleportTo(target, pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5,
                java.util.Set.of(), 0.0f, 0.0f, true);
        tell(ctx, "teleported " + p.getName().getString() + " -> " + target.dimension().identifier());
        return 1;
    }

    private static int machineRequest(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var pos = net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        var be = ctx.getSource().getLevel().getBlockEntity(pos);
        if (!(be instanceof com.buildcraftneo.industry.RequestPipeBlockEntity rp)) {
            ctx.getSource().sendFailure(Component.literal("no request pipe at " + pos.toShortString()));
            return 0;
        }
        var item = net.minecraft.commands.arguments.item.ItemArgument.getItem(ctx, "item").item().value();
        rp.setRequested(item);
        tell(ctx, "request=" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item));
        return 1;
    }

    private static ServerPlayer player(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return EntityArgument.getPlayer(ctx, "player");
    }

    private static void tell(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
    }
}
