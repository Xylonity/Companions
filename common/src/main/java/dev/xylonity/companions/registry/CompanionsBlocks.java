package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class CompanionsBlocks {

    public static void init() { ;; }

    public static final Supplier<Block> TESLA_COIL;
    public static final Supplier<Block> PLASMA_LAMP;
    public static final Supplier<Block> VOLTAIC_PILLAR;
    public static final Supplier<Block> SOUL_FURNACE;
    public static final Supplier<Block> CROISSANT_EGG;
    public static final Supplier<Block> EMPTY_PUPPET;
    public static final Supplier<Block> RESPAWN_TOTEM;

    static {
        TESLA_COIL = registerBlock("tesla_coil_block",
                        BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.TESLA_COIL);

        PLASMA_LAMP = registerBlock("plasma_lamp_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.PLASMA_LAMP);

        VOLTAIC_PILLAR = registerBlock("voltaic_pillar_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.VOLTAIC_PILLAR);

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

        EMPTY_PUPPET = registerBlock("empty_puppet_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.EMPTY_PUPPET);

        RESPAWN_TOTEM = registerBlock("respawn_totem_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.FIRE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL)
                        .noOcclusion(), BlockType.RESPAWN_TOTEM);
    }

    private static <T extends Block> Supplier<T> registerBlock(String id, BlockBehaviour.Properties properties, BlockType blockType) {
        return CompanionsCommon.COMMON_PLATFORM.registerBlock(id, properties, blockType);
    }

    public enum BlockType {
        TESLA_COIL,
        SOUL_FURNACE,
        CROISSANT_EGG,
        PLASMA_LAMP,
        VOLTAIC_PILLAR,
        EMPTY_PUPPET,
        RESPAWN_TOTEM
    }

}
