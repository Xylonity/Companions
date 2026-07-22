package dev.xylonity.companions.platform;

public class CompanionsFabricPlatform implements CompanionsPlatform {

    @Override
    public boolean requiresGlobalTeslaRenderer() {
        return true;
    }

}
