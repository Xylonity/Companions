package dev.xylonity.companions.datagen;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.tag.CompanionsTags;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class CompanionsLootModifierGenerator {

    private static final TagKey<EntityType<?>> DEMON_FLESH_DROP = CompanionsTags.DEMON_FLESH_DROP;

    public static void init() {
        LootTableEvents.MODIFY.register((rm, lm, id, provider) -> {
            if (!rm.location().getPath().startsWith("entities/")) return;

            LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(CompanionsItems.DEMON_FLESH.get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F))))
                    .when(LootItemRandomChanceCondition.randomChance(
                            (float) CompanionsConfig.DEMON_FLESH_DROP_RATE))
                    .when(LootItemEntityPropertyCondition.hasProperties(
                            LootContext.EntityTarget.THIS,
                            EntityPredicate.Builder.entity().of(DEMON_FLESH_DROP).build()
                    ));

            lm.withPool(pool);
        });

    }

}
