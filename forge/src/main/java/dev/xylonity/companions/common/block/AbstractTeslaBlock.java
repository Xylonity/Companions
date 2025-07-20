package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for the definition of abstract components belonging to the Tesla network.
 *
 * @see AbstractTeslaBlockEntity
 */
public abstract class AbstractTeslaBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public AbstractTeslaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
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
