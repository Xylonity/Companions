package dev.xylonity.companions.loot;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber(modid = CompanionsCommon.MOD_ID)
public class CompanionsLootTables {

    private static final ResourceLocation DESERT_PYRAMID_CHEST = ResourceLocation.fromNamespaceAndPath("minecraft", "chests/desert_pyramid");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        //if (event.getName().equals(DESERT_PYRAMID_CHEST)) {
        //    if (event.getTable().addPool("custom_tesla_receiver_pool"); == null) {
        //        LootPool customPool = LootPool.lootPool()
        //                .name("custom_tesla_receiver_pool")
        //                .setRolls(ConstantValue.exactly(1))
        //                .add(LootItem.lootTableItem(CompanionsBlocks.TESLA_COIL.get())
        //                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 40))))
        //                .build();
//
        //        event.getTable().addPool(customPool);
        //    }
        //}
    }
}