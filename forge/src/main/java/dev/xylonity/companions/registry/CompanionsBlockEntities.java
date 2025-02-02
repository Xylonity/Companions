package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CompanionsBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Companions.MOD_ID);

    public static final RegistryObject<BlockEntityType<TeslaReceiverBlockEntity>> TESLA_RECEIVER;

    static {
        TESLA_RECEIVER = register("tesla_receiver", TeslaReceiverBlockEntity::new, CompanionsBlocks.TESLA_RECEIVER);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block> block) {
        return BLOCK_ENTITY.register(name, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
    }

}
