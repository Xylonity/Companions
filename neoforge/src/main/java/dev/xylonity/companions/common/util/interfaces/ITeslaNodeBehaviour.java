package dev.xylonity.companions.common.util.interfaces;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Defines the activation behavior for Tesla network components. Every block entity that
 * is part of the Tesla network should process its activation each tick.
 */
public interface ITeslaNodeBehaviour extends ITeslaUtil {

    void process(AbstractTeslaBlockEntity block, Level level, BlockPos blockPos, BlockState blockState);

}
