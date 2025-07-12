package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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
    public static final Supplier<Block> FROG_BONANZA;
    public static final Supplier<Block> SHADE_SWORD_ALTAR;
    public static final Supplier<Block> SHADE_MAW_ALTAR;
    public static final Supplier<Block> ETERNAL_FIRE;

    public static final Supplier<Block> COPPER_COIN;
    public static final Supplier<Block> NETHER_COIN;
    public static final Supplier<Block> END_COIN;
    public static final Supplier<Block> RECALL_PLATFORM;
    public static final Supplier<Block> VOLTAIC_RELAY;

    static {

        COPPER_COIN = registerBlock("copper_coin",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_ORANGE)
                        .strength(1.2f)
                        .sound(SoundType.CHAIN)
                        .instrument(NoteBlockInstrument.FLUTE)
                        .noOcclusion(), BlockType.COIN_BLOCK, BlockItem.COIN);

        NETHER_COIN = registerBlock("nether_coin",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_BLACK)
                        .strength(1.2f)
                        .sound(SoundType.CHAIN)
                        .instrument(NoteBlockInstrument.FLUTE)
                        .noOcclusion(), BlockType.COIN_BLOCK, BlockItem.COIN);

        END_COIN = registerBlock("end_coin",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_PURPLE)
                        .strength(1.2f)
                        .sound(SoundType.CHAIN)
                        .instrument(NoteBlockInstrument.FLUTE)
                        .noOcclusion(), BlockType.COIN_BLOCK, BlockItem.COIN);

        SHADE_SWORD_ALTAR = registerBlock("shade_sword_altar",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(2f, 6f)
                        .sound(SoundType.STONE)
                        .instrument(NoteBlockInstrument.SKELETON)
                        .noOcclusion(), BlockType.SHADE_SWORD_ALTAR, BlockItem.GENERIC);

        SHADE_MAW_ALTAR = registerBlock("shade_maw_altar",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(2f, 6f)
                        .sound(SoundType.STONE)
                        .instrument(NoteBlockInstrument.SKELETON)
                        .noOcclusion(), BlockType.SHADE_MAW_ALTAR, BlockItem.GENERIC);

        TESLA_COIL = registerBlock("tesla_coil_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .requiresCorrectToolForDrops()
                        .strength(5F, 6F)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .noOcclusion(), BlockType.TESLA_COIL, BlockItem.GENERIC);

        PLASMA_LAMP = registerBlock("plasma_lamp_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .requiresCorrectToolForDrops()
                        .strength(5F, 6F)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .noOcclusion(), BlockType.PLASMA_LAMP, BlockItem.GENERIC);

        VOLTAIC_PILLAR = registerBlock("voltaic_pillar_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .requiresCorrectToolForDrops()
                        .strength(5F, 6F)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .noOcclusion(), BlockType.VOLTAIC_PILLAR, BlockItem.GENERIC);

        RECALL_PLATFORM = registerBlock("recall_platform_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .requiresCorrectToolForDrops()
                        .strength(5F, 6F)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .noOcclusion(), BlockType.RECALL_PLATFORM, BlockItem.GENERIC);

        VOLTAIC_RELAY = registerBlock("voltaic_relay_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .requiresCorrectToolForDrops()
                        .strength(5F, 6F)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .noOcclusion(), BlockType.VOLTAIC_RELAY, BlockItem.GENERIC);

        SOUL_FURNACE = registerBlock("soul_furnace_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_ORANGE)
                        .requiresCorrectToolForDrops()
                        .strength(3.5f)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion(), BlockType.SOUL_FURNACE, BlockItem.GENERIC);

        CROISSANT_EGG = registerBlock("croissant_egg_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.TERRACOTTA_ORANGE)
                        .strength(0.8f)
                        .sound(SoundType.WOOL)
                        .instrument(NoteBlockInstrument.GUITAR)
                        .noOcclusion(), BlockType.CROISSANT_EGG, BlockItem.GENERIC);

        EMPTY_PUPPET = registerBlock("empty_puppet_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_BROWN)
                        .strength(2f)
                        .sound(SoundType.WOOD)
                        .instrument(NoteBlockInstrument.BASS)
                        .noOcclusion(), BlockType.EMPTY_PUPPET, BlockItem.GENERIC);

        RESPAWN_TOTEM = registerBlock("respawn_totem_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.TERRACOTTA_YELLOW)
                        .requiresCorrectToolForDrops()
                        .strength(4f, 6f)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.XYLOPHONE)
                        .noOcclusion(), BlockType.RESPAWN_TOTEM, BlockItem.GENERIC);

        FROG_BONANZA = registerBlock("frog_bonanza_block",
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(2.5f, 1200f)
                        .sound(SoundType.STONE)
                        .instrument(NoteBlockInstrument.BELL)
                        .noOcclusion(), BlockType.FROG_BONANZA, BlockItem.GENERIC);

        ETERNAL_FIRE = registerBlock("eternal_fire",
                BlockBehaviour.Properties.copy(Blocks.FIRE), BlockType.ETERNAL_FIRE, BlockItem.GENERIC);
    }

    private static <T extends Block> Supplier<T> registerBlock(String id, BlockBehaviour.Properties properties, BlockType blockType, BlockItem blockItem) {
        return CompanionsCommon.COMMON_PLATFORM.registerBlock(id, properties, blockType, blockItem);
    }

    public enum BlockType {
        TESLA_COIL,
        SOUL_FURNACE,
        CROISSANT_EGG,
        PLASMA_LAMP,
        VOLTAIC_PILLAR,
        EMPTY_PUPPET,
        RESPAWN_TOTEM,
        COIN_BLOCK,
        SHADE_SWORD_ALTAR,
        SHADE_MAW_ALTAR,
        RECALL_PLATFORM,
        FROG_BONANZA,
        VOLTAIC_RELAY,
        ETERNAL_FIRE
    }

    public enum BlockItem {
        COIN,
        GENERIC
    }

}