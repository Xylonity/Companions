package dev.xylonity.companions.common.event;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.blockentity.renderer.*;
import dev.xylonity.companions.client.entity.renderer.*;
import dev.xylonity.companions.client.gui.screen.CorneliusScreen;
import dev.xylonity.companions.client.gui.screen.PuppetScreen;
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
        EntityRenderers.register(CompanionsEntities.CORNELIUS.get(), CorneliusRenderer::new);
        EntityRenderers.register(CompanionsEntities.TEDDY.get(), TeddyRenderer::new);
        EntityRenderers.register(CompanionsEntities.ANTLION.get(), AntlionRenderer::new);
        EntityRenderers.register(CompanionsEntities.DINAMO.get(), DinamoRenderer::new);
        EntityRenderers.register(CompanionsEntities.BROKEN_DINAMO.get(), BrokenDinamoRenderer::new);
        EntityRenderers.register(CompanionsEntities.HOSTILE_IMP.get(), HostileImpRenderer::new);
        EntityRenderers.register(CompanionsEntities.MINION.get(), MinionRenderer::new);
        EntityRenderers.register(CompanionsEntities.GOLDEN_ALLAY.get(), GoldenAllayRenderer::new);
        EntityRenderers.register(CompanionsEntities.SOUL_MAGE.get(), SoulMageRenderer::new);
        EntityRenderers.register(CompanionsEntities.LIVING_CANDLE.get(), LivingCandleRenderer::new);
        EntityRenderers.register(CompanionsEntities.CROISSANT_DRAGON.get(), CroissantDragonRenderer::new);
        EntityRenderers.register(CompanionsEntities.PUPPET_GLOVE.get(), PuppetGloveRenderer::new);
        EntityRenderers.register(CompanionsEntities.PUPPET.get(), PuppetRenderer::new);
        EntityRenderers.register(CompanionsEntities.SHADE_SWORD.get(), ShadeSwordRenderer::new);
        EntityRenderers.register(CompanionsEntities.SHADE_MAW.get(), ShadeMawRenderer::new);
        EntityRenderers.register(CompanionsEntities.MANKH.get(), MankhRenderer::new);
        EntityRenderers.register(CompanionsEntities.CLOAK.get(), CloakRenderer::new);

        EntityRenderers.register(CompanionsEntities.FIREWORK_TOAD.get(), FireworkToadRenderer::new);
        EntityRenderers.register(CompanionsEntities.NETHER_BULLFROG.get(), NetherBullfrogRenderer::new);
        EntityRenderers.register(CompanionsEntities.ENDER_FROG.get(), EnderFrogRenderer::new);
        EntityRenderers.register(CompanionsEntities.EMBER_POLE.get(), EmberPoleRenderer::new);
        EntityRenderers.register(CompanionsEntities.BUBBLE_FROG.get(), BubbleFrogRenderer::new);

        EntityRenderers.register(CompanionsEntities.ILLAGER_GOLEM.get(), IllagerGolemRenderer::new);
        EntityRenderers.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE.get(), HostilePuppetGloveRenderer::new);
        EntityRenderers.register(CompanionsEntities.SACRED_PONTIFF.get(), SacredPontiffRenderer::new);

        EntityRenderers.register(CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE.get(), IceShardSmallRenderer::new);
        EntityRenderers.register(CompanionsEntities.BIG_ICE_SHARD_PROJECTILE.get(), IceShardBigRenderer::new);
        EntityRenderers.register(CompanionsEntities.TORNADO_PROJECTILE.get(), TornadoRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_MARK_PROJECTILE.get(), FireMarkRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get(), FireMarkRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.STONE_SPIKE_PROJECTILE.get(), StoneSpikeRenderer::new);
        EntityRenderers.register(CompanionsEntities.HEAL_RING_PROJECTILE.get(), HealRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.BRACE_PROJECTILE.get(), BraceRenderer::new);
        EntityRenderers.register(CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE.get(), MagicRayPieceRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_RAY_PIECE_PROJECTILE.get(), FireRayPieceRenderer::new);
        EntityRenderers.register(CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE.get(), MagicRayCircleRenderer::new);
        EntityRenderers.register(CompanionsEntities.BLACK_HOLE_PROJECTILE.get(), BlackHoleRenderer::new);
        EntityRenderers.register(CompanionsEntities.SOUL_MAGE_BOOK.get(), SoulMageBookRenderer::new);
        EntityRenderers.register(CompanionsEntities.FLOOR_CAKE_CREAM.get(), FloorCakeCreamRenderer::new);
        EntityRenderers.register(CompanionsEntities.STAKE_PROJECTILE.get(), StakeRenderer::new);
        EntityRenderers.register(CompanionsEntities.HOLINESS_NAGINATA.get(), HolinessNaginataRenderer::new);
        EntityRenderers.register(CompanionsEntities.HOLINESS_NAGINATA.get(), HolinessNaginataRenderer::new);
        EntityRenderers.register(CompanionsEntities.HOLINESS_STAR.get(), HolinessStarRenderer::new);
        EntityRenderers.register(CompanionsEntities.SHADE_ALTAR_UPGRADE_HALO.get(), ShadeAltarUpgradeHaloRenderer::new);
        EntityRenderers.register(CompanionsEntities.SHADE_SWORD_IMPACT_PROJECTILE.get(), ShadeSwordImpactRenderer::new);
        EntityRenderers.register(CompanionsEntities.PONTIFF_FIRE_RING.get(), PontiffFireRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.NEEDLE_PROJECTILE.get(), NeedleRenderer::new);
        EntityRenderers.register(CompanionsEntities.RESPAWN_TOTEM_RING_PROJECTILE.get(), RespawnTotemRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.LASER_PROJECTILE.get(), LaserRenderer::new);
        EntityRenderers.register(CompanionsEntities.LASER_RING.get(), LaserRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.SCROLL.get(), ScrollRenderer::new);
        EntityRenderers.register(CompanionsEntities.FROG_HEAL_PROJECTILE.get(), FrogHealRenderer::new);
        EntityRenderers.register(CompanionsEntities.RED_STAR_EXPLOSION.get(), RedStarExplosionRenderer::new);
        EntityRenderers.register(CompanionsEntities.FROG_LEVITATE_PROJECTILE.get(), FrogLevitateRenderer::new);

        EntityRenderers.register(CompanionsEntities.GENERIC_TRIGGER_PROJECTILE.get(), GenericTriggerProjectileRenderer::new);
        EntityRenderers.register(CompanionsEntities.CAKE_CREAM_TRIGGER_PROJECTILE.get(), GenericTriggerProjectileRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_RAY_BEAM_ENTITY.get(), FireRayBeamRenderer::new);

        BlockEntityRenderers.register(CompanionsBlockEntities.TESLA_COIL.get(), TeslaCoilRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.SOUL_FURNACE.get(), SoulFurnaceRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.CROISSANT_EGG.get(), CroissantEggRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.PLASMA_LAMP.get(), PlasmaLampRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.VOLTAIC_PILLAR.get(), VoltaicPillarRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.EMPTY_PUPPET.get(), EmptyPuppetRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.RESPAWN_TOTEM.get(), RespawnTotemRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.SHADE_SWORD_ALTAR.get(), ShadeSwordAltarRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.SHADE_MAW_ALTAR.get(), ShadeMawAltarRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.RECALL_PLATFORM.get(), RecallPlatformRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.VOLTAIC_RELAY.get(), VoltaicRelayRenderer::new);
        BlockEntityRenderers.register(CompanionsBlockEntities.FROG_BONANZA.get(), FrogBonanzaRenderer::new);

        MenuScreens.register(CompanionsMenuTypes.SOUL_FURNACE.get(), SoulFurnaceScreen::new);
        MenuScreens.register(CompanionsMenuTypes.SOUL_MAGE_CONTAINER.get(), SoulMageScreen::new);
        MenuScreens.register(CompanionsMenuTypes.PUPPET_CONTAINER.get(), PuppetScreen::new);
        MenuScreens.register(CompanionsMenuTypes.CORNELIUS_CONTAINER.get(), CorneliusScreen::new);
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
        event.registerSpriteSet(CompanionsParticles.CAKE_CREAM.get(), CakeCreamParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.CAKE_CREAM_STRAWBERRY.get(), CakeCreamParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.CAKE_CREAM_CHOCOLATE.get(), CakeCreamParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.SOUL_FLAME.get(), SoulFlameParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.FIREWORK_TOAD.get(), FireworkToadParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.SHADE_TRAIL.get(), ShadeTrailParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.SHADE_SUMMON.get(), ShadeSummonParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.HOLINESS_STAR_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.BLINK.get(), BlinkParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.LASER_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
    }

}
