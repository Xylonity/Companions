package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CompanionsBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Companions.MOD_ID);

    public static final RegistryObject<BlockEntityType<TeslaCoilBlockEntity>> TESLA_COIL;
    public static final RegistryObject<BlockEntityType<PlasmaLampBlockEntity>> PLASMA_LAMP;
    public static final RegistryObject<BlockEntityType<VoltaicPillarBlockEntity>> VOLTAIC_PILLAR;
    public static final RegistryObject<BlockEntityType<SoulFurnaceBlockEntity>> SOUL_FURNACE;
    public static final RegistryObject<BlockEntityType<CroissantEggBlockEntity>> CROISSANT_EGG;
    public static final RegistryObject<BlockEntityType<EmptyPuppetBlockEntity>> EMPTY_PUPPET;
    public static final RegistryObject<BlockEntityType<RespawnTotemBlockEntity>> RESPAWN_TOTEM;

    static {
        TESLA_COIL = register("tesla_coil", TeslaCoilBlockEntity::new, CompanionsBlocks.TESLA_COIL);
        PLASMA_LAMP = register("plasma_lamp", PlasmaLampBlockEntity::new, CompanionsBlocks.PLASMA_LAMP);
        VOLTAIC_PILLAR = register("voltaic_pillar", VoltaicPillarBlockEntity::new, CompanionsBlocks.VOLTAIC_PILLAR);
        SOUL_FURNACE = register("soul_furnace", SoulFurnaceBlockEntity::new, CompanionsBlocks.SOUL_FURNACE);
        CROISSANT_EGG = register("croissant_egg", CroissantEggBlockEntity::new, CompanionsBlocks.CROISSANT_EGG);
        EMPTY_PUPPET = register("empty_puppet", EmptyPuppetBlockEntity::new, CompanionsBlocks.EMPTY_PUPPET);
        RESPAWN_TOTEM = register("respawn_totem", RespawnTotemBlockEntity::new, CompanionsBlocks.RESPAWN_TOTEM);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block> block) {
        return BLOCK_ENTITY.register(name, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
    }

}
