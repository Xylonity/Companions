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

                                output.accept(CompanionsItems.COPPER_COIN.get());
                                output.accept(CompanionsItems.NETHER_COIN.get());
                                output.accept(CompanionsItems.END_COIN.get());
                                output.accept(CompanionsItems.NETHERITE_CHAINS.get());
                                output.accept(CompanionsItems.BIG_BREAD.get());
                                output.accept(CompanionsItems.SOUL_GEM.get());

                                output.accept(CompanionsItems.BOOK_ICE_SHARD.get());
                                output.accept(CompanionsItems.BOOK_ICE_TORNADO.get());
                                output.accept(CompanionsItems.BOOK_FIRE_MARK.get());
                                output.accept(CompanionsItems.BOOK_BRACE.get());
                                output.accept(CompanionsItems.BOOK_HEAL_RING.get());
                                output.accept(CompanionsItems.BOOK_STONE_SPIKES.get());
                                output.accept(CompanionsItems.BOOK_MAGIC_RAY.get());
                                output.accept(CompanionsItems.BOOK_BLACK_HOLE.get());

                                output.accept(CompanionsItems.ETERNAL_LIGHTER.get());
                                output.accept(CompanionsItems.WRENCH.get());

                                output.accept(CompanionsItems.MAGE_HAT.get());
                                output.accept(CompanionsItems.MAGE_COAT.get());

                                output.accept(CompanionsBlocks.TESLA_RECEIVER.get());
                                output.accept(CompanionsBlocks.SOUL_FURNACE.get());

                            })
                            .build());

}
