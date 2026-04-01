package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
    public void onRemove(@NotNull BlockState oldState, Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (oldState.getBlock() == newState.getBlock()) {
            super.onRemove(oldState, level, pos, newState, movedByPiston);
            return;
        }

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AbstractTeslaBlockEntity blockEntity) {
            final TeslaNetwork network = TeslaNetwork.get(level);
            final ConnectionTarget self = blockEntity.asConnectionTarget();

            for (ConnectionTarget source : network.getIncoming(self)) {
                if (source.isBlock()) {
                    final AbstractTeslaBlockEntity sourceBlockEntity = network.getBlockEntity(source.blockPos());
                    if (sourceBlockEntity != null) {
                        sourceBlockEntity.removeOutgoing(self);
                        sourceBlockEntity.sync();
                    }
                }
                else if (source.isEntity()) {
                    final Entity entity = ((ServerLevel) level).getEntity(source.entityId());
                    if (entity instanceof DinamoEntity dinamo) {
                        dinamo.removeOutgoingConnection(self);
                    }

                }

            }

            network.unregisterBlockEntity(blockEntity);
        }

        super.onRemove(oldState, level, pos, newState, movedByPiston);
    }

}