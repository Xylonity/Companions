package dev.xylonity.companions;

import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.common.event.CompanionsServerEvents;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import dev.xylonity.knightlib.api.event.KnightLibEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Companions.MOD_ID)
public class CompanionsNeoForge {

    public CompanionsNeoForge() {
        KnightLib.initialize();

        Companions.PROXY = FMLEnvironment.dist == Dist.CLIENT ? new ClientProxy() : new CommonProxy();

        KnightLibEvents.SERVER.register(CompanionsServerEvents.class);
        Companions.PROXY.registerClientEvents();

        ConfigComposer.registerConfig(Companions.MOD_ID, CompanionsConfig.class);

        Companions.init();
    }

}
