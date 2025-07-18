package dev.xylonity.companions.client.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.renderer.*;
import dev.xylonity.companions.client.entity.renderer.*;
import dev.xylonity.companions.client.gui.screen.CorneliusScreen;
import dev.xylonity.companions.client.gui.screen.PuppetScreen;
import dev.xylonity.companions.client.gui.screen.SoulFurnaceScreen;
import dev.xylonity.companions.client.gui.screen.SoulMageScreen;
import dev.xylonity.companions.client.projectile.renderer.*;
import dev.xylonity.companions.common.particle.*;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.api.BossBarBuilder;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;

public class CompanionsClientEvents {

    private static final ResourceLocation PONTIFF_BACKGROUND = new ResourceLocation(Companions.MOD_ID, "textures/gui/pontiff_bar_background.png");
    private static final ResourceLocation PONTIFF_HEALTH = new ResourceLocation(Companions.MOD_ID, "textures/gui/pontiff_bar_health.png");
    private static final ResourceLocation PONTIFF_OVERLAY = new ResourceLocation(Companions.MOD_ID, "textures/gui/pontiff_bar_overlay.png");

    private static final ResourceLocation HOLINESS_BACKGROUND = new ResourceLocation(Companions.MOD_ID, "textures/gui/holiness_bar_background.png");
    private static final ResourceLocation HOLINESS_HEALTH = new ResourceLocation(Companions.MOD_ID, "textures/gui/holiness_bar_health.png");
    private static final ResourceLocation HOLINESS_OVERLAY = new ResourceLocation(Companions.MOD_ID, "textures/gui/holiness_bar_overlay.png");

    public static void init() {
        EntityRendererRegistry.register(CompanionsEntities.CORNELIUS, CorneliusRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.TEDDY, TeddyRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.ANTLION, AntlionRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.DINAMO, DinamoRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BROKEN_DINAMO, BrokenDinamoRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.MINION, MinionRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.GOLDEN_ALLAY, GoldenAllayRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SOUL_MAGE, SoulMageRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.LIVING_CANDLE, LivingCandleRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.CROISSANT_DRAGON, CroissantDragonRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.PUPPET_GLOVE, PuppetGloveRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.PUPPET, PuppetRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SHADE_SWORD, ShadeSwordRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SHADE_MAW, ShadeMawRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.MANKH, MankhRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.CLOAK, CloakRenderer::new);

        EntityRendererRegistry.register(CompanionsEntities.FIREWORK_TOAD, FireworkToadRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.NETHER_BULLFROG, NetherBullfrogRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.ENDER_FROG, EnderFrogRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.EMBER_POLE, EmberPoleRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BUBBLE_FROG, BubbleFrogRenderer::new);

        EntityRendererRegistry.register(CompanionsEntities.ILLAGER_GOLEM, IllagerGolemRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE, HostilePuppetGloveRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.HOSTILE_IMP, HostileImpRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SACRED_PONTIFF, SacredPontiffRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.WILD_ANTLION, WildAntlionRenderer::new);

        EntityRendererRegistry.register(CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE, IceShardSmallRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BIG_ICE_SHARD_PROJECTILE, IceShardBigRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.TORNADO_PROJECTILE, TornadoRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BLOOD_TORNADO_PROJECTILE, BloodTornadoRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FIRE_MARK_PROJECTILE, FireMarkRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FIRE_MARK_RING_PROJECTILE, FireMarkRingRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.STONE_SPIKE_PROJECTILE, StoneSpikeRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.HEAL_RING_PROJECTILE, HealRingRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BRACE_PROJECTILE, BraceRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE, MagicRayPieceRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FIRE_RAY_PIECE_PROJECTILE, FireRayPieceRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE, MagicRayCircleRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BLACK_HOLE_PROJECTILE, BlackHoleRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SOUL_MAGE_BOOK, SoulMageBookRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FLOOR_CAKE_CREAM, FloorCakeCreamRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.STAKE_PROJECTILE, StakeRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.HOLINESS_NAGINATA, HolinessNaginataRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.HOLINESS_STAR, HolinessStarRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SHADE_ALTAR_UPGRADE_HALO, ShadeAltarUpgradeHaloRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SHADE_SWORD_IMPACT_PROJECTILE, ShadeSwordImpactRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.PONTIFF_FIRE_RING, PontiffFireRingRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.NEEDLE_PROJECTILE, NeedleRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.RESPAWN_TOTEM_RING_PROJECTILE, RespawnTotemRingRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.LASER_PROJECTILE, LaserRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.LASER_RING, LaserRingRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.SCROLL, ScrollRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FROG_HEAL_PROJECTILE, FrogHealRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.RED_STAR_EXPLOSION, RedStarExplosionRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.RED_STAR_EXPLOSION_CENTER, RedStarExplosionCenterRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BLUE_STAR_EXPLOSION, BlueStarExplosionRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BLUE_STAR_EXPLOSION_CENTER, BlueStarExplosionCenterRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FROG_LEVITATE_PROJECTILE, FrogLevitateRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FROG_EGG_PROJECTILE, FrogEggRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.ANTLION_SAND_PROJECTILE, AntlionSandProjectileRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.BLOOD_SLASH_PROJECTILE, BloodSlashRenderer::new);

        EntityRendererRegistry.register(CompanionsEntities.GENERIC_TRIGGER_PROJECTILE, GenericTriggerProjectileRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.CAKE_CREAM_TRIGGER_PROJECTILE, GenericTriggerProjectileRenderer::new);
        EntityRendererRegistry.register(CompanionsEntities.FIRE_RAY_BEAM_ENTITY, FireRayBeamRenderer::new);

        BlockEntityRendererRegistry.register(CompanionsBlockEntities.TESLA_COIL, TeslaCoilRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.SOUL_FURNACE, SoulFurnaceRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.CROISSANT_EGG, CroissantEggRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.PLASMA_LAMP, PlasmaLampRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.VOLTAIC_PILLAR, VoltaicPillarRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.EMPTY_PUPPET, EmptyPuppetRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.RESPAWN_TOTEM, RespawnTotemRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.SHADE_SWORD_ALTAR, ShadeSwordAltarRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.SHADE_MAW_ALTAR, ShadeMawAltarRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.RECALL_PLATFORM, RecallPlatformRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.VOLTAIC_RELAY, VoltaicRelayRenderer::new);
        BlockEntityRendererRegistry.register(CompanionsBlockEntities.FROG_BONANZA, FrogBonanzaRenderer::new);

        MenuScreens.register(CompanionsMenuTypes.SOUL_FURNACE, SoulFurnaceScreen::new);
        MenuScreens.register(CompanionsMenuTypes.SOUL_MAGE_CONTAINER, SoulMageScreen::new);
        MenuScreens.register(CompanionsMenuTypes.PUPPET_CONTAINER, PuppetScreen::new);
        MenuScreens.register(CompanionsMenuTypes.CORNELIUS_CONTAINER, CorneliusScreen::new);

        //ItemBlockRenderTypes.(CompanionsBlocks.ETERNAL_FIRE, RenderType.cutout());

        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.TEDDY_TRANSFORMATION.get(), TeddyTransformationParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), TeddyTransformationCloudParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.ILLAGER_GOLEM_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.DINAMO_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.EMBER.get(), EmberParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.BLACK_HOLE_STAR.get(), BlackHoleStarParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.BLIZZARD_SNOW.get(), BlizzardSnowParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.BLIZZARD_ICE.get(), BlizzardIceParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.CAKE_CREAM.get(), CakeCreamParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.CAKE_CREAM_STRAWBERRY.get(), CakeCreamParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.CAKE_CREAM_CHOCOLATE.get(), CakeCreamParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.SOUL_FLAME.get(), SoulFlameParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.FIREWORK_TOAD.get(), FireworkToadParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.SHADE_TRAIL.get(), ShadeTrailParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.SHADE_SUMMON.get(), ShadeSummonParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.HOLINESS_RED_STAR_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.HOLINESS_BLUE_STAR_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.RESPAWN_TOTEM.get(), GoldenAllayTrailParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.BLINK.get(), BlinkParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.LASER_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(CompanionsParticles.EMBER_POLE_EXPLOSION.get(), TeddyTransformationParticle.Provider::new);

        // Pontiff
        BossBarBuilder
                .matcher(boss -> boss.getName().getString().contains("Sacred Pontiff") && !boss.getName().getString().equalsIgnoreCase("Sacred Pontiff Invisible"))
                .renderer((gui, boss, x, y) -> {
                    // background
                    gui.blit(PONTIFF_BACKGROUND, x, y + 22, 0, 0, 183, 5);

                    // health
                    int healthWidth = (int) (boss.getProgress() * 183);
                    if (healthWidth > 0) {
                        gui.blit(PONTIFF_HEALTH, x, y, 0, 0, healthWidth, 27);
                    }

                    // overlay
                    gui.blit(PONTIFF_OVERLAY, x - 3, y, 0, 0, 189, 30);

                    int text = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - Minecraft.getInstance().font.width(boss.getName()) / 2;
                    // name
                    gui.drawString(Minecraft.getInstance().font, boss.getName(), text, y, 0xFFFFFF);
                })
                .padding(24)
                .hideVanillaName()
                .register();

        // Invisible
        BossBarBuilder
                .matcher(boss -> boss.getName().getString().contains("Sacred Pontiff Invisible"))
                .renderer((gui, boss, x, y) -> { ;; })
                .hideVanillaName()
                .register();

        // Holiness
        BossBarBuilder
                .matcher(boss -> boss.getName().getString().contains("His Holiness"))
                .renderer((gui, boss, x, y) -> {
                    // background
                    gui.blit(HOLINESS_BACKGROUND, x - 2, y + 15, 0, 0, 185, 5);

                    // health
                    int healthWidth = (int) (boss.getProgress() * 185);
                    if (healthWidth > 0) {
                        gui.blit(HOLINESS_HEALTH, x - 2, y + 15, 0, 0, healthWidth, 28);
                    }

                    // overlay
                    gui.blit(HOLINESS_OVERLAY, x - 23, y, 0, 0, 227, 42);

                    int text = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - Minecraft.getInstance().font.width(boss.getName()) / 2;
                    gui.drawString(Minecraft.getInstance().font, boss.getName(), text, y, 0xFFFFFF);
                })
                .padding(24)
                .hideVanillaName()
                .register();
    }

}
