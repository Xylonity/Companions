package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.custom.*;
import dev.xylonity.companions.common.entity.projectile.*;
import dev.xylonity.companions.common.entity.projectile.trigger.CakeCreamTriggerProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.GenericTriggerProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CompanionsEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Companions.MOD_ID);

    public static final RegistryObject<EntityType<FroggyEntity>> FROGGY;
    public static final RegistryObject<EntityType<TeddyEntity>> TEDDY;
    public static final RegistryObject<EntityType<AntlionEntity>> ANTLION;
    public static final RegistryObject<EntityType<IllagerGolemEntity>> ILLAGER_GOLEM;
    public static final RegistryObject<EntityType<DinamoEntity>> DINAMO;
    public static final RegistryObject<EntityType<BrokenDinamoEntity>> BROKEN_DINAMO;
    public static final RegistryObject<EntityType<HostileImpEntity>> HOSTILE_IMP;
    public static final RegistryObject<EntityType<MinionEntity>> MINION;
    public static final RegistryObject<EntityType<GoldenAllayEntity>> GOLDEN_ALLAY;
    public static final RegistryObject<EntityType<SoulMageEntity>> SOUL_MAGE;
    public static final RegistryObject<EntityType<LivingCandleEntity>> LIVING_CANDLE;
    public static final RegistryObject<EntityType<CroissantDragonEntity>> CROISSANT_DRAGON;
    public static final RegistryObject<EntityType<HostilePuppetGlove>> HOSTILE_PUPPET_GLOVE;
    public static final RegistryObject<EntityType<PuppetGlove>> PUPPET_GLOVE;
    public static final RegistryObject<EntityType<PuppetEntity>> PUPPET;

    public static final RegistryObject<EntityType<SmallIceShardProjectile>> SMALL_ICE_SHARD_PROJECTILE;
    public static final RegistryObject<EntityType<BigIceShardProjectile>> BIG_ICE_SHARD_PROJECTILE;
    public static final RegistryObject<EntityType<TornadoProjectile>> TORNADO_PROJECTILE;
    public static final RegistryObject<EntityType<FireMarkProjectile>> FIRE_MARK_PROJECTILE;
    public static final RegistryObject<EntityType<FireMarkRingProjectile>> FIRE_MARK_RING_PROJECTILE;
    public static final RegistryObject<EntityType<StoneSpikeProjectile>> STONE_SPIKE_PROJECTILE;
    public static final RegistryObject<EntityType<HealRingProjectile>> HEAL_RING_PROJECTILE;
    public static final RegistryObject<EntityType<BraceProjectile>> BRACE_PROJECTILE;
    public static final RegistryObject<EntityType<MagicRayPieceProjectile>> MAGIC_RAY_PIECE_PROJECTILE;
    public static final RegistryObject<EntityType<MagicRayCircleProjectile>> MAGIC_RAY_PIECE_CIRCLE_PROJECTILE;
    public static final RegistryObject<EntityType<BlackHoleProjectile>> BLACK_HOLE_PROJECTILE;
    public static final RegistryObject<EntityType<SoulMageBookEntity>> SOUL_MAGE_BOOK;
    public static final RegistryObject<EntityType<FloorCakeCreamProjectile>> FLOOR_CAKE_CREAM;
    public static final RegistryObject<EntityType<StakeProjectile>> STAKE_PROJECTILE;

    public static final RegistryObject<EntityType<GenericTriggerProjectile>> GENERIC_TRIGGER_PROJECTILE;
    public static final RegistryObject<EntityType<CakeCreamTriggerProjectile>> CAKE_CREAM_TRIGGER_PROJECTILE;

    static {
        FROGGY = register("froggy", FroggyEntity::new, MobCategory.CREATURE, 1f, 1f, null);
        TEDDY = register("teddy", TeddyEntity::new, MobCategory.CREATURE, 1f, 1f, null);
        ANTLION = register("antlion", AntlionEntity::new, MobCategory.CREATURE, 1f, 1f, null);
        ILLAGER_GOLEM = register("illager_golem", IllagerGolemEntity::new, MobCategory.MONSTER, 1f, 2f, null);
        DINAMO = register("dinamo", DinamoEntity::new, MobCategory.CREATURE, 1f, 2f, null);
        BROKEN_DINAMO = register("broken_dinamo", BrokenDinamoEntity::new, MobCategory.CREATURE, 1f, 0.5f, null);
        HOSTILE_IMP = register("hostile_imp", HostileImpEntity::new, MobCategory.MONSTER, 0.85f, 1.5f, null);
        MINION = register("minion", MinionEntity::new, MobCategory.CREATURE, 0.85f, 1.5f, null);
        GOLDEN_ALLAY = register("golden_allay", GoldenAllayEntity::new, MobCategory.CREATURE, 0.5f, 0.5f, null);
        SOUL_MAGE = register("soul_mage", SoulMageEntity::new, MobCategory.CREATURE, 0.85f, 1.2f, null);
        LIVING_CANDLE = register("living_candle", LivingCandleEntity::new, MobCategory.CREATURE, 0.5f, 0.75f, null);
        CROISSANT_DRAGON = register("croissant_dragon", CroissantDragonEntity::new, MobCategory.CREATURE, 1.4f, 1.4f, null);
        HOSTILE_PUPPET_GLOVE = register("hostile_puppet_glove", HostilePuppetGlove::new, MobCategory.CREATURE, 0.8f, 2f, null);
        PUPPET_GLOVE = register("puppet_glove", PuppetGlove::new, MobCategory.CREATURE, 0.8f, 0.8f, null);
        PUPPET = register("puppet", PuppetEntity::new, MobCategory.CREATURE, 1f, 2.8f, null);

        SMALL_ICE_SHARD_PROJECTILE = register("small_ice_shard_projectile", SmallIceShardProjectile::new, MobCategory.MISC, 0.4f, 0.5f, EntityType.Builder::fireImmune);
        BIG_ICE_SHARD_PROJECTILE = register("big_ice_shard_projectile", BigIceShardProjectile::new, MobCategory.MISC, 1f, 0.5f, EntityType.Builder::fireImmune);
        TORNADO_PROJECTILE = register("tornado_projectile", TornadoProjectile::new, MobCategory.MISC, 0.8f, 1f, EntityType.Builder::fireImmune);
        FIRE_MARK_PROJECTILE = register("fire_mark_projectile", FireMarkProjectile::new, MobCategory.MISC, 1f, 1f, EntityType.Builder::fireImmune);
        FIRE_MARK_RING_PROJECTILE = register("fire_mark_ring_projectile", FireMarkRingProjectile::new, MobCategory.MISC, 1f, 0.2f, EntityType.Builder::fireImmune);
        STONE_SPIKE_PROJECTILE = register("stone_spike_projectile", StoneSpikeProjectile::new, MobCategory.MISC, 0.5f, 1f, EntityType.Builder::fireImmune);
        HEAL_RING_PROJECTILE = register("heal_ring_projectile", HealRingProjectile::new, MobCategory.MISC, 0.5f, 1f, EntityType.Builder::fireImmune);
        BRACE_PROJECTILE = register("brace_projectile", BraceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
        MAGIC_RAY_PIECE_PROJECTILE = register("magic_ray_piece_projectile", MagicRayPieceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
        MAGIC_RAY_PIECE_CIRCLE_PROJECTILE = register("magic_ray_circle_projectile", MagicRayCircleProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
        BLACK_HOLE_PROJECTILE = register("black_hole_projectile", BlackHoleProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
        SOUL_MAGE_BOOK = register("soul_mage_book", SoulMageBookEntity::new, MobCategory.MISC, 0.6f, 0.6f, null);
        FLOOR_CAKE_CREAM = register("floor_cake_cream_projectile", FloorCakeCreamProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
        STAKE_PROJECTILE = register("stake_projectile", StakeProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);

        GENERIC_TRIGGER_PROJECTILE = register("generic_trigger_projectile", GenericTriggerProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
        CAKE_CREAM_TRIGGER_PROJECTILE = register("cake_cream_trigger_projectile", CakeCreamTriggerProjectile::new, MobCategory.MISC, 0.6f, 0.6f, null);
    }

    private static <X extends Entity> RegistryObject<EntityType<X>> register(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable Consumer<EntityType.Builder<X>> properties) {
        return ENTITY.register(name, () -> {
            EntityType.Builder<X> builder = EntityType.Builder.of(entity, category).sized(width, height);

            if (properties != null) properties.accept(builder);

            return builder.build(new ResourceLocation(Companions.MOD_ID, name).toString());
        });
    }

}
