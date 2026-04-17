package dev.xylonity.companions;

import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.client.event.CompanionsClientEvents;
import dev.xylonity.companions.client.event.CompanionsExtraClientEvents;
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
import dev.xylonity.knightlib.api.event.KnightLibEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class CompanionsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Companions.PROXY = new ClientProxy();
        CompanionsClientEvents.init();
        KnightLibEvents.CLIENT.register(CompanionsExtraClientEvents.class);
    }

}
