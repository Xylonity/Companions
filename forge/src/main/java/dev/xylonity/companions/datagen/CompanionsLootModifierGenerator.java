package dev.xylonity.companions.datagen;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class CompanionsLootModifierGenerator extends GlobalLootModifierProvider {

    public CompanionsLootModifierGenerator(PackOutput output) {
        super(output, Companions.MOD_ID);
    }

    @Override
    protected void start() {
        TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Companions.MOD_ID, "demon_flesh_drop"));
        LootItemCondition condition = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(tag).build()).build();

        add("demon_flesh_hostiles", new CompanionsAddItemModifier(
                new LootItemCondition[]{condition},
                CompanionsItems.DEMON_FLESH.get(),
                (float) CompanionsConfig.DEMON_FLESH_DROP_RATE
        ));
    }

}