package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.*;
import dev.xylonity.companions.common.entity.hostile.*;
import dev.xylonity.companions.common.entity.projectile.*;
import dev.xylonity.companions.common.entity.projectile.trigger.CakeCreamTriggerProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.GenericTriggerProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import dev.xylonity.companions.common.entity.summon.*;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.List;

public class CompanionsEntities {

    public static final ResourceRegistry<EntityType<?>> ENTITIES = ResourceDispatcher.create(BuiltInRegistries.ENTITY_TYPE, Companions.MOD_ID);

    public static final ResourceEntry<EntityType<CorneliusEntity>> CORNELIUS = ENTITIES.registerEntity("cornelius", CorneliusEntity::new, MobCategory.CREATURE, 1f, 1f, null);
    public static final ResourceEntry<EntityType<TeddyEntity>> TEDDY = ENTITIES.registerEntity("teddy", TeddyEntity::new, MobCategory.CREATURE, 0.9f, 0.9f, null);
    public static final ResourceEntry<EntityType<AntlionEntity>> ANTLION = ENTITIES.registerEntity("antlion", AntlionEntity::new, MobCategory.CREATURE, 1f, 1f, null);
    public static final ResourceEntry<EntityType<DinamoEntity>> DINAMO = ENTITIES.registerEntity("dinamo", DinamoEntity::new, MobCategory.CREATURE, 1f, 2f, null);
    public static final ResourceEntry<EntityType<BrokenDinamoEntity>> BROKEN_DINAMO = ENTITIES.registerEntity("broken_dinamo", BrokenDinamoEntity::new, MobCategory.CREATURE, 1f, 0.5f, null);
    public static final ResourceEntry<EntityType<HostileImpEntity>> HOSTILE_IMP = ENTITIES.registerEntity("hostile_imp", HostileImpEntity::new, MobCategory.MONSTER, 0.85f, 1.5f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<MinionEntity>> MINION = ENTITIES.registerEntity("minion", MinionEntity::new, MobCategory.CREATURE, 0.85f, 1.5f, null);
    public static final ResourceEntry<EntityType<GoldenAllayEntity>> GOLDEN_ALLAY = ENTITIES.registerEntity("golden_allay", GoldenAllayEntity::new, MobCategory.CREATURE, 0.5f, 0.85f, null);
    public static final ResourceEntry<EntityType<SoulMageEntity>> SOUL_MAGE = ENTITIES.registerEntity("soul_mage", SoulMageEntity::new, MobCategory.CREATURE, 0.85f, 1.2f, null);
    public static final ResourceEntry<EntityType<LivingCandleEntity>> LIVING_CANDLE = ENTITIES.registerEntity("living_candle", LivingCandleEntity::new, MobCategory.CREATURE, 0.5f, 0.75f, null);
    public static final ResourceEntry<EntityType<CroissantDragonEntity>> CROISSANT_DRAGON = ENTITIES.registerEntity("croissant_dragon", CroissantDragonEntity::new, MobCategory.CREATURE, 1.4f, 1.4f, null);
    public static final ResourceEntry<EntityType<PuppetGloveEntity>> PUPPET_GLOVE = ENTITIES.registerEntity("puppet_glove", PuppetGloveEntity::new, MobCategory.CREATURE, 0.8f, 0.8f, null);
    public static final ResourceEntry<EntityType<PuppetEntity>> PUPPET = ENTITIES.registerEntity("puppet", PuppetEntity::new, MobCategory.CREATURE, 1f, 2.8f, null);
    public static final ResourceEntry<EntityType<ShadeSwordEntity>> SHADE_SWORD = ENTITIES.registerEntity("shade_sword", ShadeSwordEntity::new, MobCategory.CREATURE, 1.25f, 4f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<ShadeMawEntity>> SHADE_MAW = ENTITIES.registerEntity("shade_maw", ShadeMawEntity::new, MobCategory.CREATURE, 3f, 2.5f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<ShadeBatEntity>> SHADE_BAT = ENTITIES.registerEntity("shade_bat", ShadeBatEntity::new, MobCategory.CREATURE, 0.5f, 0.5f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<ShadeBatPartEntity>> SHADE_BAT_PART = ENTITIES.registerEntity("shade_bat_part", ShadeBatPartEntity::new, MobCategory.MISC, 0.3f, 0.3f, List.of(EntityType.Builder::fireImmune, EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<MankhEntity>> MANKH = ENTITIES.registerEntity("mankh", MankhEntity::new, MobCategory.CREATURE, 1f, 2f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<CloakEntity>> CLOAK = ENTITIES.registerEntity("cloak", CloakEntity::new, MobCategory.CREATURE, 1f, 2f, List.of(EntityType.Builder::fireImmune));

    public static final ResourceEntry<EntityType<FireworkToadEntity>> FIREWORK_TOAD = ENTITIES.registerEntity("firework_toad", FireworkToadEntity::new, MobCategory.CREATURE, 1f, 1f, null);
    public static final ResourceEntry<EntityType<BubbleFrogEntity>> BUBBLE_FROG = ENTITIES.registerEntity("bubble_frog", BubbleFrogEntity::new, MobCategory.CREATURE, 1f, 1f, null);
    public static final ResourceEntry<EntityType<EmberPoleEntity>> EMBER_POLE = ENTITIES.registerEntity("ember_pole", EmberPoleEntity::new, MobCategory.CREATURE, 1f, 1f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<NetherBullfrogEntity>> NETHER_BULLFROG = ENTITIES.registerEntity("nether_bullfrog", NetherBullfrogEntity::new, MobCategory.CREATURE, 1f, 1f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<EnderFrogEntity>> ENDER_FROG = ENTITIES.registerEntity("ender_frog", EnderFrogEntity::new, MobCategory.CREATURE, 1f, 2f, null);

    public static final ResourceEntry<EntityType<IllagerGolemEntity>> ILLAGER_GOLEM = ENTITIES.registerEntity("illager_golem", IllagerGolemEntity::new, MobCategory.MONSTER, 1f, 2f, null);
    public static final ResourceEntry<EntityType<HostilePuppetGloveEntity>> HOSTILE_PUPPET_GLOVE = ENTITIES.registerEntity("hostile_puppet_glove", HostilePuppetGloveEntity::new, MobCategory.CREATURE, 0.8f, 2f, null);
    public static final ResourceEntry<EntityType<SacredPontiffEntity>> SACRED_PONTIFF = ENTITIES.registerEntity("sacred_pontiff", SacredPontiffEntity::new, MobCategory.CREATURE, 2f, 4f, List.of(EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<WildAntlionEntity>> WILD_ANTLION = ENTITIES.registerEntity("wild_antlion", WildAntlionEntity::new, MobCategory.MONSTER, 1f, 1f, null);

    public static final ResourceEntry<EntityType<SmallIceShardProjectile>> SMALL_ICE_SHARD_PROJECTILE = ENTITIES.registerEntity("small_ice_shard_projectile", SmallIceShardProjectile::new, MobCategory.MISC, 0.4f, 0.5f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BigIceShardProjectile>> BIG_ICE_SHARD_PROJECTILE = ENTITIES.registerEntity("big_ice_shard_projectile", BigIceShardProjectile::new, MobCategory.MISC, 1f, 0.5f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<TornadoProjectile>> TORNADO_PROJECTILE = ENTITIES.registerEntity("tornado_projectile", TornadoProjectile::new, MobCategory.MISC, 0.8f, 1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BloodTornadoProjectile>> BLOOD_TORNADO_PROJECTILE = ENTITIES.registerEntity("blood_tornado_projectile", BloodTornadoProjectile::new, MobCategory.MISC, 0.8f, 1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BloodSlashProjectile>> BLOOD_SLASH_PROJECTILE = ENTITIES.registerEntity("blood_slash_projectile", BloodSlashProjectile::new, MobCategory.MISC, 0.8f, 1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FireMarkProjectile>> FIRE_MARK_PROJECTILE = ENTITIES.registerEntity("fire_mark_projectile", FireMarkProjectile::new, MobCategory.MISC, 1f, 1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FireMarkRingProjectile>> FIRE_MARK_RING_PROJECTILE = ENTITIES.registerEntity("fire_mark_ring_projectile", FireMarkRingProjectile::new, MobCategory.MISC, 1f, 0.2f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<StoneSpikeProjectile>> STONE_SPIKE_PROJECTILE = ENTITIES.registerEntity("stone_spike_projectile", StoneSpikeProjectile::new, MobCategory.MISC, 0.5f, 1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<HealRingProjectile>> HEAL_RING_PROJECTILE = ENTITIES.registerEntity("heal_ring_projectile", HealRingProjectile::new, MobCategory.MISC, 0.5f, 1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BraceProjectile>> BRACE_PROJECTILE = ENTITIES.registerEntity("brace_projectile", BraceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon, EntityType.Builder::fireImmune));
    public static final ResourceEntry<EntityType<MagicRayPieceProjectile>> MAGIC_RAY_PIECE_PROJECTILE = ENTITIES.registerEntity("magic_ray_piece_projectile", MagicRayPieceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FireRayPieceProjectile>> FIRE_RAY_PIECE_PROJECTILE = ENTITIES.registerEntity("fire_ray_piece_projectile", FireRayPieceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<MagicRayCircleProjectile>> MAGIC_RAY_PIECE_CIRCLE_PROJECTILE = ENTITIES.registerEntity("magic_ray_circle_projectile", MagicRayCircleProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BlackHoleProjectile>> BLACK_HOLE_PROJECTILE = ENTITIES.registerEntity("black_hole_projectile", BlackHoleProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
    public static final ResourceEntry<EntityType<SoulMageBookEntity>> SOUL_MAGE_BOOK = ENTITIES.registerEntity("soul_mage_book", SoulMageBookEntity::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FloorCakeCreamProjectile>> FLOOR_CAKE_CREAM = ENTITIES.registerEntity("floor_cake_cream_projectile", FloorCakeCreamProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<StakeProjectile>> STAKE_PROJECTILE = ENTITIES.registerEntity("stake_projectile", StakeProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<HolinessNaginataProjectile>> HOLINESS_NAGINATA = ENTITIES.registerEntity("holiness_naginata", HolinessNaginataProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<HolinessStartProjectile>> HOLINESS_STAR = ENTITIES.registerEntity("holiness_star", HolinessStartProjectile::new, MobCategory.MISC, 0.15f, 0.15f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<PontiffFireRingProjectile>> PONTIFF_FIRE_RING = ENTITIES.registerEntity("pontiff_fire_ring", PontiffFireRingProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<ShadeMawLandingRingProjectile>> SHADE_MAW_LANDING_RING = ENTITIES.registerEntity("shade_maw_landing_ring", ShadeMawLandingRingProjectile::new, MobCategory.MISC, 0.6f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<ShadeSwordImpactProjectile>> SHADE_SWORD_IMPACT_PROJECTILE = ENTITIES.registerEntity("shade_sword_impact", ShadeSwordImpactProjectile::new, MobCategory.MISC, 1.2f, 1.2f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<NeedleProjectile>> NEEDLE_PROJECTILE = ENTITIES.registerEntity("needle_projectile", NeedleProjectile::new, MobCategory.MISC, 1.2f, 0.4f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BlueOrbProjectile>> BLUE_ORB_PROJECTILE = ENTITIES.registerEntity("blue_orb_projectile", BlueOrbProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<HolyRingProjectile>> HOLY_RING_PROJECTILE = ENTITIES.registerEntity("holy_ring_projectile", HolyRingProjectile::new, MobCategory.MISC, 0.5f, 0.2f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<LaserTriggerProjectile>> LASER_PROJECTILE = ENTITIES.registerEntity("laser", LaserTriggerProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<LaserRingProjectile>> LASER_RING = ENTITIES.registerEntity("laser_ring", LaserRingProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FrogHealProjectile>> FROG_HEAL_PROJECTILE = ENTITIES.registerEntity("frog_heal_projectile", FrogHealProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FrogLevitateProjectile>> FROG_LEVITATE_PROJECTILE = ENTITIES.registerEntity("frog_levitate_projectile", FrogLevitateProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FrogEggProjectile>> FROG_EGG_PROJECTILE = ENTITIES.registerEntity("frog_egg_projectile", FrogEggProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<AntlionSandProjectile>> ANTLION_SAND_PROJECTILE = ENTITIES.registerEntity("antlion_sand_projectile", AntlionSandProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FireGeiserProjectile>> FIRE_GEISER_PROJECTILE = ENTITIES.registerEntity("fire_geiser_projectile", FireGeiserProjectile::new, MobCategory.MISC, 1, 0.5f, List.of(EntityType.Builder::noSummon));

    public static final ResourceEntry<EntityType<ScrollProjectile>> SCROLL = ENTITIES.registerEntity("scroll", ScrollProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<RedStarExplosion>> RED_STAR_EXPLOSION = ENTITIES.registerEntity("red_star_explosion", RedStarExplosion::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<RedStarExplosionCenter>> RED_STAR_EXPLOSION_CENTER = ENTITIES.registerEntity("red_star_explosion_center", RedStarExplosionCenter::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BlueStarExplosion>> BLUE_STAR_EXPLOSION = ENTITIES.registerEntity("blue_star_explosion", BlueStarExplosion::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<BlueStarExplosionCenter>> BLUE_STAR_EXPLOSION_CENTER = ENTITIES.registerEntity("blue_star_explosion_center", BlueStarExplosionCenter::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<ShadeAltarUpgradeHaloProjectile>> SHADE_ALTAR_UPGRADE_HALO = ENTITIES.registerEntity("shade_altar_upgrade_halo", ShadeAltarUpgradeHaloProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<RespawnTotemRingProjectile>> RESPAWN_TOTEM_RING_PROJECTILE = ENTITIES.registerEntity("respawn_totem_ring_projectile", RespawnTotemRingProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));

    public static final ResourceEntry<EntityType<GenericTriggerProjectile>> GENERIC_TRIGGER_PROJECTILE = ENTITIES.registerEntity("generic_trigger_projectile", GenericTriggerProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<CakeCreamTriggerProjectile>> CAKE_CREAM_TRIGGER_PROJECTILE = ENTITIES.registerEntity("cake_cream_trigger_projectile", CakeCreamTriggerProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    public static final ResourceEntry<EntityType<FireRayBeamEntity>> FIRE_RAY_BEAM_ENTITY = ENTITIES.registerEntity("fire_ray_beam_entity", FireRayBeamEntity::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));

}
