package dev.xylonity.companions;

import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.common.event.CompanionsCommonEvents;
import dev.xylonity.companions.common.event.CompanionsEntityRespawnTracker;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.event.CompanionsServerEvents;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.datagen.CompanionsLootModifierGenerator;
import dev.xylonity.companions.proxy.IProxy;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import net.fabricmc.api.ModInitializer;

public class Companions implements ModInitializer {

    public static final String MOD_ID = CompanionsCommon.MOD_ID;

    public static IProxy PROXY;

    @Override
    public void onInitialize() {
        PROXY = new CommonProxy();

        KnightLib.initialize();

        ConfigComposer.registerConfig(Companions.MOD_ID, CompanionsConfig.class);

        CompanionsEntities.init();
        CompanionsBlockEntities.init();
        CompanionsRecipes.init();
        CompanionsSpawns.init();
        CompanionsMenuTypes.init();

        CompanionsCommonEvents.init();
        CompanionsEntityRespawnTracker.init();
        CompanionsEntityTracker.init();
        CompanionsServerEvents.init();

        CompanionsLootModifierGenerator.init();

        CompanionsCommon.init();
    }

}
