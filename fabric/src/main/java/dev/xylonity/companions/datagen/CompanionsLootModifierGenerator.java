package dev.xylonity.companions.datagen;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.tag.CompanionsTags;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class CompanionsLootModifierGenerator {

    public static void init() {
        LootTableEvents.MODIFY.register((rm, lm, id, tableBuilder, source) -> {
            if (!id.getPath().startsWith("entities/")) return;

            ResourceLocation rl = new ResourceLocation(id.getNamespace(), id.getPath().substring("entities/".length()));

            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) return;
            if (BuiltInRegistries.ENTITY_TYPE.get(rl).is(CompanionsTags.DEMON_FLESH_DROP)) return;
            LootPool.Builder smallEssencePool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(CompanionsItems.DEMON_FLESH.get())
                            .apply(SetItemCountFunction.setCount(
                                    UniformGenerator.between(1.0F, 1.0F)
                            ))
                    )
                    .when(LootItemRandomChanceCondition.randomChance(
                            (float) CompanionsConfig.DEMON_FLESH_DROP_RATE
                    ));

            tableBuilder.withPool(smallEssencePool);
        });
    }

}