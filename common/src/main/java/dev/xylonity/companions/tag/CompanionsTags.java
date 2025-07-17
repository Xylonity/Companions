package dev.xylonity.companions.tag;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class CompanionsTags {

    public static final Supplier<TagKey<EntityType<?>>> DEMON_FLESH_DROP = () -> TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CompanionsCommon.MOD_ID, "demon_flesh_drop"));

}
