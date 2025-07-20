package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CompanionsBlockEntities {

    public static void init() { ;; }

    public static final BlockEntityType<TeslaCoilBlockEntity> TESLA_COIL;
    public static final BlockEntityType<PlasmaLampBlockEntity> PLASMA_LAMP;
    public static final BlockEntityType<VoltaicPillarBlockEntity> VOLTAIC_PILLAR;
    public static final BlockEntityType<SoulFurnaceBlockEntity> SOUL_FURNACE;
    public static final BlockEntityType<CroissantEggBlockEntity> CROISSANT_EGG;
    public static final BlockEntityType<EmptyPuppetBlockEntity> EMPTY_PUPPET;
    public static final BlockEntityType<RespawnTotemBlockEntity> RESPAWN_TOTEM;
    public static final BlockEntityType<FrogBonanzaBlockEntity> FROG_BONANZA;
    public static final BlockEntityType<ShadeSwordAltarBlockEntity> SHADE_SWORD_ALTAR;
    public static final BlockEntityType<ShadeMawAltarBlockEntity> SHADE_MAW_ALTAR;
    public static final BlockEntityType<RecallPlatformBlockEntity> RECALL_PLATFORM;
    public static final BlockEntityType<VoltaicRelayBlockEntity> VOLTAIC_RELAY;

    static {
        TESLA_COIL = register("tesla_coil", TeslaCoilBlockEntity::new, CompanionsBlocks.TESLA_COIL);
        PLASMA_LAMP = register("plasma_lamp", PlasmaLampBlockEntity::new, CompanionsBlocks.PLASMA_LAMP);
        VOLTAIC_PILLAR = register("voltaic_pillar", VoltaicPillarBlockEntity::new, CompanionsBlocks.VOLTAIC_PILLAR);
        SOUL_FURNACE = register("soul_furnace", SoulFurnaceBlockEntity::new, CompanionsBlocks.SOUL_FURNACE);
        CROISSANT_EGG = register("croissant_egg", CroissantEggBlockEntity::new, CompanionsBlocks.CROISSANT_EGG);
        EMPTY_PUPPET = register("empty_puppet", EmptyPuppetBlockEntity::new, CompanionsBlocks.EMPTY_PUPPET);
        RESPAWN_TOTEM = register("respawn_totem", RespawnTotemBlockEntity::new, CompanionsBlocks.RESPAWN_TOTEM);
        FROG_BONANZA = register("frog_bonanza", FrogBonanzaBlockEntity::new, CompanionsBlocks.FROG_BONANZA);
        SHADE_SWORD_ALTAR = register("shade_sword_altar", ShadeSwordAltarBlockEntity::new, CompanionsBlocks.SHADE_SWORD_ALTAR);
        SHADE_MAW_ALTAR = register("shade_maw_altar", ShadeMawAltarBlockEntity::new, CompanionsBlocks.SHADE_MAW_ALTAR);
        RECALL_PLATFORM = register("recall_platform", RecallPlatformBlockEntity::new, CompanionsBlocks.RECALL_PLATFORM);
        VOLTAIC_RELAY = register("voltaic_relay", VoltaicRelayBlockEntity::new, CompanionsBlocks.VOLTAIC_RELAY);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> block) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Companions.MOD_ID, name), BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

}
