package dev.xylonity.companions.loot;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.registry.CompanionsBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID)
public class CompanionsLootTables {

    private static final ResourceLocation DESERT_PYRAMID_CHEST = new ResourceLocation("minecraft", "chests/desert_pyramid");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(DESERT_PYRAMID_CHEST)) {
            if (event.getTable().getPool("custom_tesla_receiver_pool") == null) {
                LootPool customPool = LootPool.lootPool()
                        .name("custom_tesla_receiver_pool")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(CompanionsBlocks.TESLA_RECEIVER.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 40))))
                        .build();

                event.getTable().addPool(customPool);
            }
        }
    }
}