package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public class VoltaicPillarBlock extends AbstractTeslaBlock {

    private static final VoxelShape SHAPE_BASE = Stream.of(
            Block.box(4, 6.5, 4, 12, 9.5, 12),
            Block.box(5.5, 0, 5.5, 10.5, 16, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_TOP = Stream.of(
            Block.box(4, 6.5, 4, 12, 9.5, 12),
            Block.box(5.5, 0, 5.5, 10.5, 10, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public VoltaicPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        final BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof VoltaicPillarBlockEntity pillar && pillar.isTop()) {
            return SHAPE_TOP;
        }
        return SHAPE_BASE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return CompanionsBlockEntities.VOLTAIC_PILLAR.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == CompanionsBlockEntities.VOLTAIC_PILLAR.get() ? VoltaicPillarBlockEntity::tick : null;
    }

    @Override
    public void onPlace(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            final TeslaNetwork network = TeslaNetwork.get(pLevel);

            // If there's a pillar above, connects above to this
            if (pLevel.getBlockEntity(pPos.above()) instanceof VoltaicPillarBlockEntity above
                    && pLevel.getBlockEntity(pPos) instanceof VoltaicPillarBlockEntity current) {
                above.addOutgoing(current.asConnectionTarget());
                network.onConnectionAdded(above.asConnectionTarget(), current.asConnectionTarget());
            }

            // If there's a pillar below, connects this to below
            if (pLevel.getBlockEntity(pPos.below()) instanceof VoltaicPillarBlockEntity below
                    && pLevel.getBlockEntity(pPos) instanceof VoltaicPillarBlockEntity current) {
                if (!network.getIncoming(below.asConnectionTarget()).contains(current.asConnectionTarget())) {
                    current.addOutgoing(below.asConnectionTarget());
                    network.onConnectionAdded(current.asConnectionTarget(), below.asConnectionTarget());
                }

            }

        }

        if (pLevel.getBlockEntity(pPos) instanceof VoltaicPillarBlockEntity curr) {
            if (!pLevel.getBlockState(pPos.above()).isAir()) {
                curr.setHasBlockOnTop(true);
            }
            else {
                curr.setHasBlockOnTop(false);
                curr.setIsTop(true);
            }

        }

        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

}