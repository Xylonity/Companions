package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.block.*;
import dev.xylonity.companions.common.item.HolyPorcelainPottery;
import dev.xylonity.companions.common.item.PorcelainPottery;
import dev.xylonity.companions.common.item.blockitem.CoinItem;
import dev.xylonity.companions.common.item.blockitem.GenericBlockItem;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class CompanionsBlocks {

    public static final ResourceRegistry<Block> BLOCKS = ResourceDispatcher.create(BuiltInRegistries.BLOCK, Companions.MOD_ID);

    public static final Supplier<Block> TESLA_COIL = BLOCKS.registerBlock("tesla_coil_block",
            () -> new TeslaCoilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5F, 6F)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "tesla_coil_block")
    );

    public static final Supplier<Block> COPPER_COIN = BLOCKS.registerBlock("copper_coin",
            () -> new CoinBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(1.2f)
                    .sound(SoundType.CHAIN)
                    .instrument(NoteBlockInstrument.FLUTE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new CoinItem(block, new Item.Properties(), "copper_coin")
    );

    public static final Supplier<Block> NETHER_COIN = BLOCKS.registerBlock("nether_coin",
            () -> new CoinBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.2f)
                    .sound(SoundType.CHAIN)
                    .instrument(NoteBlockInstrument.FLUTE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new CoinItem(block, new Item.Properties(), "nether_coin")
    );

    public static final Supplier<Block> END_COIN = BLOCKS.registerBlock("end_coin",
            () -> new CoinBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(1.2f)
                    .sound(SoundType.CHAIN)
                    .instrument(NoteBlockInstrument.FLUTE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new CoinItem(block, new Item.Properties(), "end_coin")
    );

    public static final Supplier<Block> SHADE_SWORD_ALTAR = BLOCKS.registerBlock("shade_sword_altar",
            () -> new ShadeSwordAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2f, 6f)
                    .sound(SoundType.STONE)
                    .instrument(NoteBlockInstrument.SKELETON)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "shade_sword_altar")
    );

    public static final Supplier<Block> SHADE_MAW_ALTAR = BLOCKS.registerBlock("shade_maw_altar",
            () -> new ShadeMawAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2f, 6f)
                    .sound(SoundType.STONE)
                    .instrument(NoteBlockInstrument.SKELETON)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "shade_maw_altar")
    );

    public static final Supplier<Block> SHADE_BAT_ALTAR = BLOCKS.registerBlock("shade_bat_altar",
            () -> new ShadeBatAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2f, 6f)
                    .sound(SoundType.STONE)
                    .instrument(NoteBlockInstrument.SKELETON)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "shade_bat_altar")
    );

    public static final Supplier<Block> PLASMA_LAMP = BLOCKS.registerBlock("plasma_lamp_block",
            () -> new PlasmaLampBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5F, 6F)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "plasma_lamp_block")
    );

    public static final Supplier<Block> VOLTAIC_PILLAR = BLOCKS.registerBlock("voltaic_pillar_block",
            () -> new VoltaicPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5F, 6F)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "voltaic_pillar_block")
    );

    public static final Supplier<Block> RECALL_PLATFORM = BLOCKS.registerBlock("recall_platform_block",
            () -> new RecallPlatformBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5F, 6F)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "recall_platform_block")
    );

    public static final Supplier<Block> VOLTAIC_RELAY = BLOCKS.registerBlock("voltaic_relay_block",
            () -> new VoltaicRelayBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5F, 6F)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "voltaic_relay_block")
    );

    public static final Supplier<Block> SOUL_FURNACE = BLOCKS.registerBlock("soul_furnace_block",
            () -> new SoulFurnaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5f)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "soul_furnace_block")
    );

    public static final Supplier<Block> CROISSANT_EGG = BLOCKS.registerBlock("croissant_egg_block",
            () -> new CroissantEggBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(0.8f)
                    .sound(SoundType.WOOL)
                    .instrument(NoteBlockInstrument.GUITAR)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "croissant_egg_block")
    );

    public static final Supplier<Block> EMPTY_PUPPET = BLOCKS.registerBlock("empty_puppet_block",
            () -> new EmptyPuppetBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(2f)
                    .sound(SoundType.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "empty_puppet_block")
    );

    public static final Supplier<Block> RESPAWN_TOTEM = BLOCKS.registerBlock("respawn_totem_block",
            () -> new RespawnTotemBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .requiresCorrectToolForDrops()
                    .strength(4f, 6f)
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.XYLOPHONE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "respawn_totem_block")
    );

    public static final Supplier<Block> FROG_BONANZA = BLOCKS.registerBlock("frog_bonanza_block",
            () -> new FrogBonanzaBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.5f, 1200f)
                    .sound(SoundType.STONE)
                    .instrument(NoteBlockInstrument.BELL)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "frog_bonanza_block")
    );

    public static final Supplier<Block> PORCELAIN_POTTERY = BLOCKS.registerBlock("porcelain_pottery",
            () -> new PorcelainPotteryBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(1.2f)
                    .sound(SoundType.COPPER)
                    .instrument(NoteBlockInstrument.FLUTE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new PorcelainPottery(block, new Item.Properties(), "porcelain_pottery")
    );

    public static final Supplier<Block> HOLY_PORCELAIN_POTTERY = BLOCKS.registerBlock("holy_porcelain_pottery",
            () -> new HolyPorcelainPotteryBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .strength(1.2f)
                    .sound(SoundType.COPPER)
                    .instrument(NoteBlockInstrument.FLUTE)
                    .noOcclusion()),
            CompanionsItems.ITEMS,
            block -> new HolyPorcelainPottery(block, new Item.Properties(), "holy_porcelain_pottery")
    );

    public static final Supplier<Block> ETERNAL_FIRE = BLOCKS.registerBlock("eternal_fire",
            () -> new EternalFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE)),
            CompanionsItems.ITEMS,
            block -> new GenericBlockItem(block, new Item.Properties(), "eternal_fire")
    );

}