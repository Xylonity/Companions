package dev.xylonity.companions.platform;

public interface CompanionsPlatform {

    default boolean requiresGlobalTeslaRenderer() {
        return false;
    }

}
