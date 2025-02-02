package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CompanionsCreativeModeTabs {

    public static void init() { ;; }

    private static final List<Supplier<Block>> platformItems = new ArrayList<>();

    public static void registerPlatformItem(Supplier<Block> itemSupplier) {
        platformItems.add(itemSupplier);
    }

    public static final Supplier<CreativeModeTab> KNIGHTQUEST_TAB =
            CompanionsCommon.COMMON_PLATFORM.registerCreativeModeTab("knightquest_tab",
                    () -> CompanionsCommon.COMMON_PLATFORM.creativeTabBuilder()
                            .icon(() -> new ItemStack(Items.ACACIA_BOAT))
                            .title(Component.translatable("itemgroup.common"))
                            .displayItems((itemDisplayParameters, output) -> {

                                output.accept(CompanionsItems.TEST.get());
                                output.accept(CompanionsItems.TEST2.get());
                                output.accept(CompanionsItems.ETERNAL_LIGHTER.get());

                                for (Supplier<Block> itemSupplier : platformItems) {
                                    Block item = itemSupplier.get();
                                    if (item != null) {
                                        output.accept(item);
                                    }
                                }

                            })
                            .build());

}
