package dev.xylonity.companions.common.util.interfaces;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Defines the activation behavior for Tesla network components.
 * Every block entity that is part of the Tesla network should process its activation
 * each tick using an instance of ActivationBehavior.
 */
public interface ITeslaNodeBehaviour extends ITeslaUtil {

    /**
     * Processes the activation logic for the given Tesla block entity.
     *
     * @param block the block entity processing its activation logic
     */
    void process(AbstractTeslaBlockEntity block, Level level, BlockPos blockPos, BlockState blockState);

}
