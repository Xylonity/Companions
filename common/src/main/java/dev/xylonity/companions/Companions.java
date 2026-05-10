package dev.xylonity.companions;

import dev.xylonity.companions.platform.CompanionsPlatform;
import dev.xylonity.companions.proxy.IProxy;
import dev.xylonity.companions.registry.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class Companions {

    public static final String MOD_ID = "companions";
    public static final Logger LOGGER = LoggerFactory.getLogger("Companions!");

    public static final CompanionsPlatform PLATFORM = ServiceLoader.load(CompanionsPlatform.class).findFirst().orElseThrow();

    public static IProxy PROXY;

    public static void init() {
        CompanionsBlocks.BLOCKS.init();
        CompanionsEntities.ENTITIES.init();
        CompanionsItems.ITEMS.init();
        CompanionsBlockEntities.BLOCK_ENTITIES.init();
        CompanionsCreativeModeTabs.CREATIVE_TABS.init();
        CompanionsEffects.EFFECTS.init();
        CompanionsParticles.PARTICLES.init();
        CompanionsSounds.SOUNDS.init();
        CompanionsMenuTypes.MENUS.init();
        CompanionsRecipeTypes.RECIPE_TYPES.init();
        CompanionsRecipeSerializers.RECIPE_SERIALIZERS.init();
        CompanionsStructureProcessors.PROCESSORS.init();

        CompanionsEntitySpawns.init();
        CompanionsEntityDrops.init();
    }

    public static ResourceLocation of(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}