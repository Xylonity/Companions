package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsFabric;
import dev.xylonity.companions.common.blockentity.*;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import dev.xylonity.knightlib.registry.KnightLibBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class CompanionsBlockEntities {

    public static final ResourceRegistry<BlockEntityType<?>> BLOCK_ENTITIES = ResourceDispatcher.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Companions.MOD_ID);

    public static final ResourceEntry<BlockEntityType<TeslaCoilBlockEntity>> TESLA_COIL = BLOCK_ENTITIES.register("tesla_coil", BlockEntityType.Builder.of(TeslaCoilBlockEntity::new, CompanionsBlocks.TESLA_COIL.get()).build(null));
    public static final ResourceEntry<BlockEntityType<PlasmaLampBlockEntity>> PLASMA_LAMP = register("plasma_lamp", PlasmaLampBlockEntity::new, CompanionsBlocks.PLASMA_LAMP);
    public static final ResourceEntry<BlockEntityType<VoltaicPillarBlockEntity>> VOLTAIC_PILLAR = register("voltaic_pillar", VoltaicPillarBlockEntity::new, CompanionsBlocks.VOLTAIC_PILLAR);
    public static final ResourceEntry<BlockEntityType<SoulFurnaceBlockEntity>> SOUL_FURNACE = register("soul_furnace", SoulFurnaceBlockEntity::new, CompanionsBlocks.SOUL_FURNACE);
    public static final ResourceEntry<BlockEntityType<CroissantEggBlockEntity>> CROISSANT_EGG = register("croissant_egg", CroissantEggBlockEntity::new, CompanionsBlocks.CROISSANT_EGG);
    public static final ResourceEntry<BlockEntityType<EmptyPuppetBlockEntity>> EMPTY_PUPPET = register("empty_puppet", EmptyPuppetBlockEntity::new, CompanionsBlocks.EMPTY_PUPPET);
    public static final ResourceEntry<BlockEntityType<RespawnTotemBlockEntity>> RESPAWN_TOTEM = register("respawn_totem", RespawnTotemBlockEntity::new, CompanionsBlocks.RESPAWN_TOTEM);
    public static final ResourceEntry<BlockEntityType<FrogBonanzaBlockEntity>> FROG_BONANZA = register("frog_bonanza", FrogBonanzaBlockEntity::new, CompanionsBlocks.FROG_BONANZA);
    public static final ResourceEntry<BlockEntityType<ShadeSwordAltarBlockEntity>> SHADE_SWORD_ALTAR = register("shade_sword_altar", ShadeSwordAltarBlockEntity::new, CompanionsBlocks.SHADE_SWORD_ALTAR);
    public static final ResourceEntry<BlockEntityType<ShadeMawAltarBlockEntity>> SHADE_MAW_ALTAR = register("shade_maw_altar", ShadeMawAltarBlockEntity::new, CompanionsBlocks.SHADE_MAW_ALTAR);
    public static final ResourceEntry<BlockEntityType<RecallPlatformBlockEntity>> RECALL_PLATFORM = register("recall_platform", RecallPlatformBlockEntity::new, CompanionsBlocks.RECALL_PLATFORM);
    public static final ResourceEntry<BlockEntityType<VoltaicRelayBlockEntity>> VOLTAIC_RELAY = register("voltaic_relay", VoltaicRelayBlockEntity::new, CompanionsBlocks.VOLTAIC_RELAY);

    public <T extends BlockEntity> ResourceEntry<BlockEntityType<T>> registerBlockEntity(
            String name,
            KnightLibBlockEntities.BlockEntityFactory<T> factory,
            Supplier<? extends Block> block
    ) {
        return ((ResourceRegistry<BlockEntityType<T>>) this).register(name, () ->
                KnightLib.PLATFORM.createBlockEntityType(factory, block, "")
        );
    }

    public <B extends BlockEntity> ResourceEntry<BlockEntityType<B>> registerBlockEntity(
            String name,
            KnightLibBlockEntities.BlockEntityFactory<B> factory,
            Supplier<? extends Block> block
    ) {
        return this.registerBlockEntity(name, factory, () -> List.of(block.get()));
    }

}
