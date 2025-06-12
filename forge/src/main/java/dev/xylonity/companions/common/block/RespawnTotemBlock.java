package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class RespawnTotemBlock extends Block implements EntityBlock {

    private static final VoxelShape SHAPE_LOWER_N = Stream.of(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(3, 4, 4, 13, 17, 12),
            Block.box(3, 17, 3, 13, 28, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_UPPER_N = Stream.of(
            Block.box(0, -16, 0, 16, -12, 16),
            Block.box(3, -12, 4, 13, 1, 12),
            Block.box(3, 1, 3, 13, 12, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_UPPER_E = Stream.of(
            Block.box(0, -16, 0, 16, -12, 16),
            Block.box(4, -12, 3, 12, 1, 13),
            Block.box(3, 1, 3, 13, 12, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_LOWER_E = Stream.of(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(4, 4, 3, 12, 17, 13),
            Block.box(3, 17, 3, 13, 28, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public RespawnTotemBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public long getSeed(@NotNull BlockState pState, @NotNull BlockPos pPos) {
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER ?
                pPos.asLong() : pPos.below().asLong();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        Direction d = pState.getValue(FACING);
        if (d == Direction.NORTH || d == Direction.SOUTH)
            return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_LOWER_N : SHAPE_UPPER_N;

        return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_LOWER_E : SHAPE_UPPER_E;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER);
        }

        return null;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return pPos.getY() < pLevel.getMaxBuildHeight() - 1;
        } else {
            BlockState belowState = pLevel.getBlockState(pPos.below());
            return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockPos pNeighborPos) {
        DoubleBlockHalf half = pState.getValue(HALF);
        if (pDirection.getAxis() == Direction.Axis.Y) {
            if (half == DoubleBlockHalf.LOWER && pDirection == Direction.UP) {
                return pNeighborState.is(this) && pNeighborState.getValue(HALF) == DoubleBlockHalf.UPPER ? pState : Blocks.AIR.defaultBlockState();
            } else if (half == DoubleBlockHalf.UPPER && pDirection == Direction.DOWN) {
                return pNeighborState.is(this) && pNeighborState.getValue(HALF) == DoubleBlockHalf.LOWER ? pState : Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public void playerWillDestroy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Player pPlayer) {
        if (!pLevel.isClientSide) {
            if (pPlayer.isCreative()) {
                preventCreativeTabDestroy(pLevel, pPos, pState, pPlayer);
            } else {
                dropResources(pState, pLevel, pPos, null, pPlayer, pPlayer.getMainHandItem());
            }
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    public static void preventCreativeTabDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = pPos.below();
            BlockState lowerState = pLevel.getBlockState(lowerPos);
            if (lowerState.is(pState.getBlock()) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                pLevel.setBlock(lowerPos, Blocks.AIR.defaultBlockState(), 35);
                pLevel.levelEvent(pPlayer, 2001, lowerPos, Block.getId(lowerState));
            }
        }

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT, HALF);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? CompanionsBlockEntities.RESPAWN_TOTEM.get().create(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER && pBlockEntityType == CompanionsBlockEntities.RESPAWN_TOTEM.get() ? RespawnTotemBlockEntity::tick : null;
    }

    @Nullable
    public BlockEntity getMultiblockBlockEntity(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return level.getBlockEntity(pos);
        } else {
            BlockPos lowerPos = pos.below();
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                return level.getBlockEntity(lowerPos);
            }
        }

        return null;
    }

    public void updateLitState(Level level, BlockPos pos, BlockState state, boolean lit) {
        level.setBlock(pos, state.setValue(LIT, lit), 3);

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = pos.below();
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this)) {
                level.setBlock(lowerPos, lowerState.setValue(LIT, lit), 3);
            }
        } else {
            BlockPos upperPos = pos.above();
            BlockState upperState = level.getBlockState(upperPos);
            if (upperState.is(this)) {
                level.setBlock(upperPos, upperState.setValue(LIT, lit), 3);
            }
        }

    }

}