package dev.xylonity.companions.loot;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID)
public class CompanionsLootTables {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();
        if (!"minecraft".equals(id.getNamespace()) || !id.getPath().startsWith("chests/")) {
            return;
        }

        float chance;
        NumberProvider count;
        Item coin;
        String path = id.getPath();
        if (path.contains("nether")) {
            chance = 0.45f;
            count = UniformGenerator.between(1, 3);
            coin = CompanionsBlocks.NETHER_COIN.get().asItem();
        } else if (path.contains("end")) {
            chance = 0.8f;
            count = ConstantValue.exactly(1);
            coin = CompanionsBlocks.END_COIN.get().asItem();
        } else {
            chance = 0.075f;
            count = UniformGenerator.between(1, 5);
            coin = CompanionsBlocks.COPPER_COIN.get().asItem();
        }

        LootPool.Builder pool = LootPool.lootPool().name("coin_pool")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(coin)
                        .apply(SetItemCountFunction.setCount(count))
                        .when(LootItemRandomChanceCondition.randomChance(chance)))
                .add(LootItem.lootTableItem(CompanionsItems.BOOK_BLACK_HOLE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .when(LootItemRandomChanceCondition.randomChance(0.045f)))
                // magic2 book
                .add(LootItem.lootTableItem(CompanionsItems.BOOK_MAGIC_RAY.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .when(LootItemRandomChanceCondition.randomChance(0.045f)));

        event.getTable().addPool(pool.build());
    }

}