package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.VoltaicRelayBlockEntity;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
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

public class VoltaicRelayBlock extends AbstractTeslaBlock implements EntityBlock {

    private static final VoxelShape SHAPE_UP = Stream.of(
            Block.box(0, 7.5, 6, 16, 10.5, 10),
            Block.box(12, 10.5, 6, 16, 20.5, 10),
            Block.box(0, 10.5, 6, 4, 20.5, 10),
            Block.box(6, 10.5, 12, 10, 20.5, 16),
            Block.box(6, 7.5, 0, 10, 10.5, 16),
            Block.box(6, 10.5, 0, 10, 20.5, 4),
            Block.box(5.5, 0, 5.5, 10.5, 7, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_DOWN = Stream.of(
            Block.box(0, 5.5, 6, 16, 8.5, 10),
            Block.box(0, -4.5, 6, 4, 5.5, 10),
            Block.box(12, -4.5, 6, 16, 5.5, 10),
            Block.box(6, -4.5, 12, 10, 5.5, 16),
            Block.box(6, 5.5, 0, 10, 8.5, 16),
            Block.box(6, -4.5, 0, 10, 5.5, 4),
            Block.box(5.5, 9, 5.5, 10.5, 16, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(0, 6, 5.5, 16, 10, 8.5),
            Block.box(0, 6, -4.5, 4, 10, 5.5),
            Block.box(12, 6, -4.5, 16, 10, 5.5),
            Block.box(6, 0, -4.5, 10, 4, 5.5),
            Block.box(6, 0, 5.5, 10, 16, 8.5),
            Block.box(6, 12, -4.5, 10, 16, 5.5),
            Block.box(5.5, 5.5, 9, 10.5, 10.5, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(0, 6, 7.5, 16, 10, 10.5),
            Block.box(12, 6, 10.5, 16, 10, 20.5),
            Block.box(0, 6, 10.5, 4, 10, 20.5),
            Block.box(6, 0, 10.5, 10, 4, 20.5),
            Block.box(6, 0, 7.5, 10, 16, 10.5),
            Block.box(6, 12, 10.5, 10, 16, 20.5),
            Block.box(5.5, 5.5, 0, 10.5, 10.5, 7)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(5.5, 6, 0, 8.5, 10, 16),
            Block.box(-4.5, 6, 12, 5.5, 10, 16),
            Block.box(-4.5, 6, 0, 5.5, 10, 4),
            Block.box(-4.5, 0, 6, 5.5, 4, 10),
            Block.box(5.5, 0, 6, 8.5, 16, 10),
            Block.box(-4.5, 12, 6, 5.5, 16, 10),
            Block.box(9, 5.5, 5.5, 16, 10.5, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(7.5, 6, 0, 10.5, 10, 16),
            Block.box(10.5, 6, 0, 20.5, 10, 4),
            Block.box(10.5, 6, 12, 20.5, 10, 16),
            Block.box(10.5, 0, 6, 20.5, 4, 10),
            Block.box(7.5, 0, 6, 10.5, 16, 10),
            Block.box(10.5, 12, 6, 20.5, 16, 10),
            Block.box(0, 5.5, 5.5, 7, 10.5, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public VoltaicRelayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(TeslaCoilBlock.FACING)) {
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            case NORTH -> SHAPE_N;
            case SOUTH -> SHAPE_S;
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return CompanionsBlockEntities.VOLTAIC_RELAY.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == CompanionsBlockEntities.VOLTAIC_RELAY ? VoltaicRelayBlockEntity::tick : null;
    }

}
