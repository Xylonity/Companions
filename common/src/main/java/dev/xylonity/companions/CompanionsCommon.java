package dev.xylonity.companions;

import dev.xylonity.companions.platform.CompanionsPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * Common mod loader class.
 */
public class CompanionsCommon {

    public static final String MOD_ID = "knightquestcompanions";
    public static final Logger LOGGER = LoggerFactory.getLogger("Knight Quest Companions");

    public static final CompanionsPlatform COMMON_PLATFORM = ServiceLoader.load(CompanionsPlatform.class).findFirst().orElseThrow();

    public static void init() {
    }

}