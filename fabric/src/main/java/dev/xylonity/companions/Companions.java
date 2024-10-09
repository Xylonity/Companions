package dev.xylonity.companions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Companions implements ModInitializer, ClientModInitializer {

    private static final String KNIGHTQUEST_MOD_ID = "knightquest";
    private static final String FCAP_MOD_ID = "forgeconfigapiport";

    @Override
    public void onInitialize() {

        CompanionsCommon.init();
    }

    /**
     * Verifies if the `Knight Quest` mod is loaded.
     *
     * @return true if it is loaded, false if not.
     */
    public static boolean isKnightQuestLoaded() {
        return FabricLoader.getInstance().isModLoaded(KNIGHTQUEST_MOD_ID);
    }

    /**
     * Verifies if the `Knight Quest` mod is loaded.
     *
     * @return true if it is loaded, false if not.
     */
    public static boolean isFCAPLoaded() {
        return FabricLoader.getInstance().isModLoaded(FCAP_MOD_ID);
    }

    @Override
    public void onInitializeClient() {

    }
}
