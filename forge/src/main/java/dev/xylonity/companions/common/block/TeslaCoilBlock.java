package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class TeslaCoilBlock extends AbstractTeslaBlock implements EntityBlock {

    private static final VoxelShape SHAPE_UP = Stream.of(
            Block.box(5, 0, 5, 11, 6, 11),
            Block.box(1, 9, 1, 15, 13, 15),
            Block.box(5.5, 6, 5.5, 10.5, 16, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_DOWN = Stream.of(
            Block.box(5, 10, 5, 11, 16, 11),
            Block.box(1, 3, 1, 15, 7, 15),
            Block.box(5.5, 0, 5.5, 10.5, 10, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(5, 5, 10, 11, 11, 16),
            Block.box(1, 1, 3, 15, 15, 7),
            Block.box(5.5, 5.5, 0, 10.5, 10.5, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(5, 5, 0, 11, 11, 6),
            Block.box(1, 1, 9, 15, 15, 13),
            Block.box(5.5, 5.5, 6, 10.5, 10.5, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(10, 5, 5, 16, 11, 11),
            Block.box(3, 1, 1, 7, 15, 15),
            Block.box(0, 5.5, 5.5, 10, 10.5, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(0, 5, 5, 6, 11, 11),
            Block.box(9, 1, 1, 13, 15, 15),
            Block.box(6, 5.5, 5.5, 16, 10.5, 10.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public TeslaCoilBlock(Properties properties) {
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
        return CompanionsBlockEntities.TESLA_COIL.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == CompanionsBlockEntities.TESLA_COIL.get() ? TeslaCoilBlockEntity::tick : null;
    }

}
