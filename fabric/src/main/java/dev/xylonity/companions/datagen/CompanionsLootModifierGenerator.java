package dev.xylonity.companions.datagen;

import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class CompanionsLootModifierGenerator {

    public static void init() {
        LootTableEvents.MODIFY.register((rm, lm, id, tableBuilder, source) -> {
            if (!"minecraft".equals(id.getNamespace()) || !id.getPath().startsWith("chests/")) return;

            String p = id.getPath();
            Item coin;
            float chance;
            int min;
            int max;
            if (p.contains("nether")) {
                coin = CompanionsBlocks.NETHER_COIN.get().asItem();
                chance = 0.45f;
                min = 1;
                max = 3;
            } else if (p.contains("end")) {
                coin = CompanionsBlocks.END_COIN.get().asItem();
                chance = 0.8f;
                min = max = 1;
            } else {
                coin = CompanionsBlocks.COPPER_COIN.get().asItem();
                chance = 0.075f;
                min = 1;
                max = 5;
            }

            LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(coin)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                            .when(LootItemRandomChanceCondition.randomChance(chance)))
                    .add(LootItem.lootTableItem(CompanionsItems.BOOK_MAGIC_RAY.get())
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .when(LootItemRandomChanceCondition.randomChance(0.045f)))
                    .add(LootItem.lootTableItem(CompanionsItems.BOOK_BLACK_HOLE.get())
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .when(LootItemRandomChanceCondition.randomChance(0.045f)));

            tableBuilder.withPool(pool);
        });

    }

}
