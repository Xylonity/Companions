package dev.xylonity.companions;

import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.datagen.CompanionsLootModifierGenerator;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import net.fabricmc.api.ModInitializer;

public class CompanionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        KnightLib.initialize();

        Companions.PROXY = new CommonProxy();

        ConfigComposer.registerConfig(Companions.MOD_ID, CompanionsConfig.class);

        CompanionsLootModifierGenerator.init();

        Companions.init();
    }

}
