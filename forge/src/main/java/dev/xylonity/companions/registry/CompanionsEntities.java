package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.custom.*;
import dev.xylonity.companions.common.entity.projectile.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CompanionsEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Companions.MOD_ID);

    public static final RegistryObject<EntityType<FroggyEntity>> FROGGY;
    public static final RegistryObject<EntityType<TeddyEntity>> TEDDY;
    public static final RegistryObject<EntityType<AntlionEntity>> ANTLION;
    public static final RegistryObject<EntityType<IllagerGolemEntity>> ILLAGER_GOLEM;
    public static final RegistryObject<EntityType<DinamoEntity>> DINAMO;
    public static final RegistryObject<EntityType<BrokenDinamoEntity>> BROKEN_DINAMO;

    public static final RegistryObject<EntityType<SmallIceShardProjectile>> SMALL_ICE_SHARD_PROJECTILE;
    public static final RegistryObject<EntityType<BigIceShardProjectile>> BIG_ICE_SHARD_PROJECTILE;
    public static final RegistryObject<EntityType<TornadoProjectile>> TORNADO_PROJECTILE;
    public static final RegistryObject<EntityType<FireMarkProjectile>> FIRE_MARK_PROJECTILE;
    public static final RegistryObject<EntityType<FireMarkRingProjectile>> FIRE_MARK_RING_PROJECTILE;
    public static final RegistryObject<EntityType<StoneSpikeProjectile>> STONE_SPIKE_PROJECTILE;

    static {
        FROGGY = register("froggy", FroggyEntity::new, MobCategory.CREATURE, 1f, 1f);
        TEDDY = register("teddy", TeddyEntity::new, MobCategory.CREATURE, 1f, 1f);
        ANTLION = register("antlion", AntlionEntity::new, MobCategory.CREATURE, 1f, 1f);
        ILLAGER_GOLEM = register("illager_golem", IllagerGolemEntity::new, MobCategory.MONSTER, 1f, 2f);
        DINAMO = register("dinamo", DinamoEntity::new, MobCategory.CREATURE, 1f, 2f);
        BROKEN_DINAMO = register("broken_dinamo", BrokenDinamoEntity::new, MobCategory.CREATURE, 1f, 0.5f);
        SMALL_ICE_SHARD_PROJECTILE = register("small_ice_shard_projectile", SmallIceShardProjectile::new, MobCategory.MISC, 1f, 0.5f);
        BIG_ICE_SHARD_PROJECTILE = register("big_ice_shard_projectile", BigIceShardProjectile::new, MobCategory.MISC, 1f, 0.5f);
        TORNADO_PROJECTILE = register("tornado_projectile", TornadoProjectile::new, MobCategory.MISC, 1f, 0.5f);
        FIRE_MARK_PROJECTILE = register("fire_mark_projectile", FireMarkProjectile::new, MobCategory.MISC, 1f, 1f);
        FIRE_MARK_RING_PROJECTILE = register("fire_mark_ring_projectile", FireMarkRingProjectile::new, MobCategory.MISC, 1f, 0.2f);
        STONE_SPIKE_PROJECTILE = register("stone_spike_projectile", StoneSpikeProjectile::new, MobCategory.MISC, 0.5f, 1f);
    }

    private static <X extends Entity> RegistryObject<EntityType<X>> register(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height) {
        return ENTITY.register(name, () -> EntityType.Builder.of(entity, category).sized(width, height).build(String.valueOf(new ResourceLocation(Companions.MOD_ID, name))));
    }

}
