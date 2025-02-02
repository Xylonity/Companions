package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.custom.*;
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
    public static final RegistryObject<EntityType<TamedIllagerGolemEntity>> TAMED_ILLAGER_GOLEM;

    static {
        FROGGY = register("froggy", FroggyEntity::new, MobCategory.CREATURE, 1f, 1f);
        TEDDY = register("teddy", TeddyEntity::new, MobCategory.CREATURE, 1f, 1f);
        ANTLION = register("antlion", AntlionEntity::new, MobCategory.CREATURE, 1f, 1f);
        ILLAGER_GOLEM = register("illager_golem", IllagerGolemEntity::new, MobCategory.MONSTER, 1f, 2f);
        TAMED_ILLAGER_GOLEM = register("tamed_illager_golem", TamedIllagerGolemEntity::new, MobCategory.CREATURE, 1f, 2f);
    }

    private static <X extends Entity> RegistryObject<EntityType<X>> register(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height) {
        return ENTITY.register(name, () -> EntityType.Builder.of(entity, category).sized(width, height).build(String.valueOf(new ResourceLocation(Companions.MOD_ID, name))));
    }

}
