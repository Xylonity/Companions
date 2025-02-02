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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeslaReceiverBlock extends PoweredBlock implements EntityBlock {

    public TeslaReceiverBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
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
