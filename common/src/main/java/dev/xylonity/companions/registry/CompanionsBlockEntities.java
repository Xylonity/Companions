package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.*;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CompanionsBlockEntities {

    public static final ResourceRegistry<BlockEntityType<?>> BLOCK_ENTITIES = ResourceDispatcher.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Companions.MOD_ID);

    public static final ResourceEntry<BlockEntityType<TeslaCoilBlockEntity>> TESLA_COIL = BLOCK_ENTITIES.registerBlockEntity("tesla_coil", TeslaCoilBlockEntity::new, CompanionsBlocks.TESLA_COIL);
    public static final ResourceEntry<BlockEntityType<PlasmaLampBlockEntity>> PLASMA_LAMP = BLOCK_ENTITIES.registerBlockEntity("plasma_lamp", PlasmaLampBlockEntity::new, CompanionsBlocks.PLASMA_LAMP);
    public static final ResourceEntry<BlockEntityType<VoltaicPillarBlockEntity>> VOLTAIC_PILLAR = BLOCK_ENTITIES.registerBlockEntity("voltaic_pillar", VoltaicPillarBlockEntity::new, CompanionsBlocks.VOLTAIC_PILLAR);
    public static final ResourceEntry<BlockEntityType<SoulFurnaceBlockEntity>> SOUL_FURNACE = BLOCK_ENTITIES.registerBlockEntity("soul_furnace", SoulFurnaceBlockEntity::new, CompanionsBlocks.SOUL_FURNACE);
    public static final ResourceEntry<BlockEntityType<CroissantEggBlockEntity>> CROISSANT_EGG = BLOCK_ENTITIES.registerBlockEntity("croissant_egg", CroissantEggBlockEntity::new, CompanionsBlocks.CROISSANT_EGG);
    public static final ResourceEntry<BlockEntityType<EmptyPuppetBlockEntity>> EMPTY_PUPPET = BLOCK_ENTITIES.registerBlockEntity("empty_puppet", EmptyPuppetBlockEntity::new, CompanionsBlocks.EMPTY_PUPPET);
    public static final ResourceEntry<BlockEntityType<RespawnTotemBlockEntity>> RESPAWN_TOTEM = BLOCK_ENTITIES.registerBlockEntity("respawn_totem", RespawnTotemBlockEntity::new, CompanionsBlocks.RESPAWN_TOTEM);
    public static final ResourceEntry<BlockEntityType<FrogBonanzaBlockEntity>> FROG_BONANZA = BLOCK_ENTITIES.registerBlockEntity("frog_bonanza", FrogBonanzaBlockEntity::new, CompanionsBlocks.FROG_BONANZA);
    public static final ResourceEntry<BlockEntityType<ShadeSwordAltarBlockEntity>> SHADE_SWORD_ALTAR = BLOCK_ENTITIES.registerBlockEntity("shade_sword_altar", ShadeSwordAltarBlockEntity::new, CompanionsBlocks.SHADE_SWORD_ALTAR);
    public static final ResourceEntry<BlockEntityType<ShadeMawAltarBlockEntity>> SHADE_MAW_ALTAR = BLOCK_ENTITIES.registerBlockEntity("shade_maw_altar", ShadeMawAltarBlockEntity::new, CompanionsBlocks.SHADE_MAW_ALTAR);
    public static final ResourceEntry<BlockEntityType<RecallPlatformBlockEntity>> RECALL_PLATFORM = BLOCK_ENTITIES.registerBlockEntity("recall_platform", RecallPlatformBlockEntity::new, CompanionsBlocks.RECALL_PLATFORM);
    public static final ResourceEntry<BlockEntityType<VoltaicRelayBlockEntity>> VOLTAIC_RELAY = BLOCK_ENTITIES.registerBlockEntity("voltaic_relay", VoltaicRelayBlockEntity::new, CompanionsBlocks.VOLTAIC_RELAY);

}