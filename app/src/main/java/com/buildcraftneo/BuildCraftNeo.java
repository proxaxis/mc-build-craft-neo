package com.buildcraftneo;

import com.buildcraftneo.command.BCNCommands;
import com.buildcraftneo.data.BCNAttachments;
import com.buildcraftneo.disaster.DisasterManager;
import com.buildcraftneo.industry.BCNBlockEntities;
import com.buildcraftneo.industry.BCNBlocks;
import com.buildcraftneo.item.BCNItems;
import com.buildcraftneo.job.JobEffects;
import com.buildcraftneo.restriction.Restrictions;
import com.buildcraftneo.util.VeinMiner;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildCraftNeo implements ModInitializer {
    public static final String MOD_ID = "buildcraftneo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay()
                .register(com.buildcraftneo.net.ShopHistoryPayload.TYPE,
                        com.buildcraftneo.net.ShopHistoryPayload.CODEC);
        BCNAttachments.register();
        BCNItems.register();
        BCNBlocks.register();
        BCNBlockEntities.register();
        com.buildcraftneo.industry.BCNMenus.register();
        Restrictions.register();
        JobEffects.register();
        com.buildcraftneo.job.ArmorEffects.register();
        com.buildcraftneo.job.JobXp.register();
        DisasterManager.register();
        com.buildcraftneo.data.ItemLocks.register();
        com.buildcraftneo.dim.DimBuffs.register();
        com.buildcraftneo.dim.BossManager.register();
        VeinMiner.register();
        BCNCommands.register();
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.getPlayer() instanceof net.minecraft.server.level.ServerPlayer p
                    && com.buildcraftneo.data.Profiles.faction(p) == com.buildcraftneo.data.Faction.NONE) {
                p.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "[BCN] 陣営を選択してください: /bcn join science または /bcn join magic"));
            }
        });
        LOGGER.info("[BuildCraftNeo] initialized");
    }
}
