package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class CompanionsBlocks {

    public static void init() { ;; }

    public static final Supplier<Block> TESLA_RECEIVER;
    public static final Supplier<Block> SOUL_FURNACE;
    public static final Supplier<Block> CROISSANT_EGG;

    static {
        TESLA_RECEIVER = registerBlock("tesla_receiver_block",
                        BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.TESLA_RECEIVER);

        SOUL_FURNACE = registerBlock("soul_furnace_block",
                        BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.SOUL_FURNACE);

        CROISSANT_EGG = registerBlock("croissant_egg_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.CROISSANT_EGG);
    }

    private static <T extends Block> Supplier<T> registerBlock(String id, BlockBehaviour.Properties properties, BlockType blockType) {
        return CompanionsCommon.COMMON_PLATFORM.registerBlock(id, properties, blockType);
    }

    public enum BlockType {
        TESLA_RECEIVER,
        SOUL_FURNACE,
        CROISSANT_EGG
    }

}
