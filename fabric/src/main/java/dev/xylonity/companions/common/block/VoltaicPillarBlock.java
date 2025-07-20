package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
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
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (be instanceof VoltaicPillarBlockEntity pillarEntity && pillarEntity.isTop()) {
            return SHAPE_TOP;
        }

        return SHAPE_BASE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return CompanionsBlockEntities.VOLTAIC_PILLAR.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == CompanionsBlockEntities.VOLTAIC_PILLAR ? VoltaicPillarBlockEntity::tick : null;
    }

    @Override
    public void onPlace(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pOldState, boolean pMovedByPiston) {
        BlockEntity aboveBe = pLevel.getBlockEntity(pPos.above());
        if (aboveBe instanceof VoltaicPillarBlockEntity above) {
            if (pLevel.getBlockEntity(pPos) instanceof VoltaicPillarBlockEntity curr) {
                TeslaConnectionManager.getInstance().addConnection(above.asConnectionNode(), curr.asConnectionNode());
            }
        }

        BlockEntity belowBe = pLevel.getBlockEntity(pPos.below());
        if (belowBe instanceof VoltaicPillarBlockEntity below) {
            if (pLevel.getBlockEntity(pPos) instanceof VoltaicPillarBlockEntity curr) {
                if (!TeslaConnectionManager.getInstance().getIncoming(below.asConnectionNode()).contains(curr.asConnectionNode())) {
                    TeslaConnectionManager.getInstance().addConnection(curr.asConnectionNode(), below.asConnectionNode());
                }
            }
        }

        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

}
