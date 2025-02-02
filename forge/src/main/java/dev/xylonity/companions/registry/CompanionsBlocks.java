package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.block.TeslaReceiverBlock;
import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import dev.xylonity.companions.common.item.GenericBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CompanionsBlocks {

    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, Companions.MOD_ID);

    public static final RegistryObject<Block> TESLA_RECEIVER;

    static {
        TESLA_RECEIVER = register("tesla_receiver_block",
                () -> new TeslaReceiverBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion()));
    }

    private static <X extends Block> RegistryObject<X> register(String name, Supplier<X> block) {
        RegistryObject<X> toReturn = BLOCK.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <X extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<X> block) {
        return Companions.ITEMS.register(name, () -> new GenericBlockItem(block.get(), new Item.Properties()));
    }

}
