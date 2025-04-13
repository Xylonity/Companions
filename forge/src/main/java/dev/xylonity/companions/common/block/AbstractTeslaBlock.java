package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for the definition of abstract components belonging to the Tesla network.
 *
 * @see AbstractTeslaBlockEntity
 */
public abstract class AbstractTeslaBlock extends Block implements EntityBlock {

    public AbstractTeslaBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof AbstractTeslaBlockEntity receiver) {
            TeslaConnectionManager connectionManager = TeslaConnectionManager.getInstance();
            connectionManager.getIncoming(receiver.asConnectionNode()).forEach(sourceNode -> connectionManager.getOutgoing(sourceNode).remove(receiver.asConnectionNode()));
            TeslaConnectionManager.getInstance().unregisterBlockEntity(receiver);
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

}
