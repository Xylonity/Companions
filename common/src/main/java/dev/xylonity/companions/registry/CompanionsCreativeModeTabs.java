package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class CompanionsCreativeModeTabs {

    public static void init() { ;; }

    public static final Supplier<CreativeModeTab> KNIGHTQUEST_TAB =
            CompanionsCommon.COMMON_PLATFORM.registerCreativeModeTab("knightquest_tab",
                    () -> CompanionsCommon.COMMON_PLATFORM.creativeTabBuilder()
                            .icon(() -> new ItemStack(Items.ACACIA_BOAT))
                            .title(Component.translatable("itemgroup.common"))
                            .displayItems((itemDisplayParameters, output) -> {

                                output.accept(CompanionsItems.TEST.get());
                                output.accept(CompanionsItems.TEST2.get());
                                output.accept(CompanionsItems.ETERNAL_LIGHTER.get());

                            })
                            .build());

}
