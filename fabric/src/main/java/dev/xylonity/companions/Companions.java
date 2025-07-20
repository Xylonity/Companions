package dev.xylonity.companions;

import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.client.event.CompanionsClientEvents;
import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.common.event.CompanionsCommonEvents;
import dev.xylonity.companions.common.event.CompanionsEntityRespawnTracker;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.event.CompanionsServerEvents;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.datagen.CompanionsLootModifierGenerator;
import dev.xylonity.companions.proxy.IProxy;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.config.ConfigComposer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.stream.Collectors;

public class Companions implements ModInitializer, ClientModInitializer {

    public static final String MOD_ID = CompanionsCommon.MOD_ID;

    public static IProxy PROXY = new CommonProxy();

    @Override
    public void onInitialize() {

        ConfigComposer.registerConfig(CompanionsConfig.class);

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

    @Override
    public void onInitializeClient() {

        PROXY = new ClientProxy();
        CompanionsClientEvents.init();
    }

}
