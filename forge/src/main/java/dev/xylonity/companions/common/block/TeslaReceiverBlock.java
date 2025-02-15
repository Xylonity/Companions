package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class TeslaReceiverBlock extends PoweredBlock implements EntityBlock {

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(8.881784197001252e-16, 9, 0, 16, 13, 16),
            Block.box(3.499999999999999, 1.7763568394002505e-15, 3.5, 12.5, 9, 12.5),
            Block.box(2, 13, 2, 14, 16, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public TeslaReceiverBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SHAPE_N;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return CompanionsBlockEntities.TESLA_RECEIVER.get().create(pos, state);
    }

    @Override
    public int getSignal(@NotNull BlockState pBlockState, BlockGetter pBlockAccess, @NotNull BlockPos pPos, @NotNull Direction pSide) {
        if (pBlockAccess.getBlockEntity(pPos) instanceof TeslaReceiverBlockEntity receiver) {
            return receiver.hasSignal() ? 15 : 0;
        }

        return 0;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof TeslaReceiverBlockEntity receiver) {

            TeslaConnectionManager connectionManager = TeslaConnectionManager.getInstance();
            TeslaConnectionManager.getInstance().unregisterBlockEntity(receiver);

            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);

            for (TeslaConnectionManager.ConnectionNode targetNode : connectionManager.getOutgoing(receiver.asConnectionNode())) {
                connectionManager.getIncoming(targetNode).remove(receiver.asConnectionNode());
            }

            connectionManager.getOutgoing(receiver.asConnectionNode()).clear();

            for (TeslaConnectionManager.ConnectionNode sourceNode : connectionManager.getIncoming(receiver.asConnectionNode())) {
                connectionManager.getOutgoing(sourceNode).remove(receiver.asConnectionNode());
            }

            connectionManager.getIncoming(receiver.asConnectionNode()).clear();
        } else {
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == CompanionsBlockEntities.TESLA_RECEIVER.get() ? TeslaReceiverBlockEntity::tick : null;
    }

    public int getAnalogOutputSignal(BlockState blockState, BlockGetter level, BlockPos pos) {
        return 0;
    }

}
