package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.function.Supplier;

public class CompanionsCreativeModeTabs {

    public static void init() { ;; }

    public static final Supplier<CreativeModeTab> COMPANIONS_TAB =
            CompanionsCommon.COMMON_PLATFORM.registerCreativeModeTab("companions_tab",
                    () -> CompanionsCommon.COMMON_PLATFORM.creativeTabBuilder()
                            .icon(() -> new ItemStack(Items.ACACIA_BOAT))
                            .title(Component.translatable("creativetab.companions.title"))
                            .displayItems((itemDisplayParameters, output) -> {

                                output.accept(CompanionsItems.COPPER_COIN.get());
                                output.accept(CompanionsItems.NETHER_COIN.get());
                                output.accept(CompanionsItems.END_COIN.get());
                                output.accept(CompanionsItems.NETHERITE_CHAINS.get());
                                output.accept(CompanionsItems.BIG_BREAD.get());
                                output.accept(CompanionsItems.SOUL_GEM.get());
                                output.accept(CompanionsItems.MUTANT_ARM.get());
                                output.accept(CompanionsItems.WHIP_ARM.get());
                                output.accept(CompanionsItems.BLADE_ARM.get());
                                output.accept(CompanionsItems.CANNON_ARM.get());
                                output.accept(CompanionsItems.NETHERITE_DAGGER.get());
                                output.accept(CompanionsItems.HOURGLASS.get());

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
                                output.accept(CompanionsItems.MAGE_STAFF.get());

                                output.accept(CompanionsBlocks.TESLA_COIL.get());
                                output.accept(CompanionsBlocks.PLASMA_LAMP.get());
                                output.accept(CompanionsBlocks.VOLTAIC_PILLAR.get());
                                output.accept(CompanionsBlocks.SOUL_FURNACE.get());
                                output.accept(CompanionsBlocks.CROISSANT_EGG.get());
                                output.accept(CompanionsBlocks.EMPTY_PUPPET.get());
                                output.accept(CompanionsBlocks.RESPAWN_TOTEM.get());
                                output.accept(CompanionsBlocks.RECALL_PLATFORM.get());

                                output.accept(CompanionsItems.CROISSANT_DRAGON_ARMOR_CHOCOLATE.get());
                                output.accept(CompanionsItems.CROISSANT_DRAGON_ARMOR_VANILLA.get());
                                output.accept(CompanionsItems.CROISSANT_DRAGON_ARMOR_STRAWBERRY.get());

                            })
                            .build());

}