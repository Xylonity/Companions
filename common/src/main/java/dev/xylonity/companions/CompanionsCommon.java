package dev.xylonity.companions;

import dev.xylonity.companions.platform.CompanionsPlatform;
import dev.xylonity.companions.registry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class CompanionsCommon {

    public static final String MOD_ID = "companions";
    public static final Logger LOGGER = LoggerFactory.getLogger("Companions!");

    public static final CompanionsPlatform COMMON_PLATFORM = ServiceLoader.load(CompanionsPlatform.class)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service"));

    public static void init() {
        CompanionsItems.init();
        CompanionsBlocks.init();
        CompanionsCreativeModeTabs.init();
        CompanionsEffects.init();
        CompanionsParticles.init();
        CompanionsSounds.init();
    }

}