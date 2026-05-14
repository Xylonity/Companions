package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.network.packets.ShadeMawLandingPostShaderS2C;

public final class CompanionsPackets {

    public static void registerAll() {
        registerS2C();
    }

    public static void registerS2C() {
        Companions.NETWORK.register(ShadeMawLandingPostShaderS2C.TYPE);
    }

}
