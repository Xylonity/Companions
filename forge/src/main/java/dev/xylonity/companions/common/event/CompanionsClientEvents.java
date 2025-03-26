package dev.xylonity.companions.common.event;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.blockentity.renderer.CroissantEggRenderer;
import dev.xylonity.companions.client.blockentity.renderer.SoulFurnaceRenderer;
import dev.xylonity.companions.client.blockentity.renderer.TeslaReceiverRenderer;
import dev.xylonity.companions.client.entity.renderer.*;
import dev.xylonity.companions.client.gui.screen.SoulFurnaceScreen;
import dev.xylonity.companions.client.gui.screen.SoulMageScreen;
import dev.xylonity.companions.client.projectile.renderer.*;
import dev.xylonity.companions.common.particle.*;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsMenuTypes;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CompanionsClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(CompanionsEntities.FROGGY.get(), FroggyRenderer::new);
        EntityRenderers.register(CompanionsEntities.TEDDY.get(), TeddyRenderer::new);
        EntityRenderers.register(CompanionsEntities.ANTLION.get(), AntlionRenderer::new);
        EntityRenderers.register(CompanionsEntities.ILLAGER_GOLEM.get(), IllagerGolemRenderer::new);
        EntityRenderers.register(CompanionsEntities.DINAMO.get(), DinamoRenderer::new);
        EntityRenderers.register(CompanionsEntities.BROKEN_DINAMO.get(), BrokenDinamoRenderer::new);
        EntityRenderers.register(CompanionsEntities.HOSTILE_IMP.get(), HostileImpRenderer::new);
        EntityRenderers.register(CompanionsEntities.MINION.get(), MinionRenderer::new);
        EntityRenderers.register(CompanionsEntities.GOLDEN_ALLAY.get(), GoldenAllayRenderer::new);
        EntityRenderers.register(CompanionsEntities.SOUL_MAGE.get(), SoulMageRenderer::new);
        EntityRenderers.register(CompanionsEntities.LIVING_CANDLE.get(), LivingCandleRenderer::new);
        EntityRenderers.register(CompanionsEntities.CROISSANT_DRAGON.get(), CroissantDragonRenderer::new);
        EntityRenderers.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE.get(), HostilePuppetGloveRenderer::new);

        EntityRenderers.register(CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE.get(), IceShardSmallRenderer::new);
        EntityRenderers.register(CompanionsEntities.BIG_ICE_SHARD_PROJECTILE.get(), IceShardBigRenderer::new);
        EntityRenderers.register(CompanionsEntities.TORNADO_PROJECTILE.get(), TornadoRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_MARK_PROJECTILE.get(), FireMarkRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get(), FireMarkRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.STONE_SPIKE_PROJECTILE.get(), StoneSpikeRenderer::new);
        EntityRenderers.register(CompanionsEntities.HEAL_RING_PROJECTILE.get(), HealRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.BRACE_PROJECTILE.get(), BraceRenderer::new);
        EntityRenderers.register(CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE.get(), MagicRayPieceRenderer::new);
        EntityRenderers.register(CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE.get(), MagicRayCircleRenderer::new);
        EntityRenderers.register(CompanionsEntities.BLACK_HOLE_PROJECTILE.get(), BlackHoleRenderer::new);
        EntityRenderers.register(CompanionsEntities.SOUL_MAGE_BOOK.get(), SoulMageBookRenderer::new);

        BlockEntityRenderers.register(CompanionsBlockEntities.TESLA_RECEIVER.get(), TeslaReceiverRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.SOUL_FURNACE.get(), SoulFurnaceRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.CROISSANT_EGG.get(), CroissantEggRenderer::new);

        MenuScreens.register(CompanionsMenuTypes.SOUL_FURNACE.get(), SoulFurnaceScreen::new);
        MenuScreens.register(CompanionsMenuTypes.SOUL_MAGE_CONTAINER.get(), SoulMageScreen::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION.get(), TeddyTransformationParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), TeddyTransformationCloudParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.ILLAGER_GOLEM_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.DINAMO_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.EMBER.get(), EmberParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.BLACK_HOLE_STAR.get(), BlackHoleStarParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.BLIZZARD_SNOW.get(), BlizzardSnowParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.BLIZZARD_ICE.get(), BlizzardIceParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
    }

}
