package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.FrogBonanzaBlockEntity;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class FrogBonanzaBlock extends Block implements EntityBlock {

    private static final VoxelShape SHAPE_LOWER_N = Stream.of(
            Block.box(2, 4, 4, 14, 17, 12),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(-0.5, 16, 3, 16.5, 26, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_UPPER_N = Stream.of(
            Block.box(2, -12, 4, 14, 1, 12),
            Block.box(0, -16, 0, 16, -12, 16),
            Block.box(-0.5, 0, 3, 16.5, 10, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_UPPER_E = Stream.of(
            Block.box(4, -12, 2, 12, 1, 14),
            Block.box(0, -16, 0, 16, -12, 16),
            Block.box(3, 0, -0.5, 13, 10, 16.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_LOWER_E = Stream.of(
            Block.box(4, 4, 2, 12, 17, 14),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(3, 16, -0.5, 13, 26, 16.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public FrogBonanzaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public long getSeed(@NotNull BlockState pState, @NotNull BlockPos pPos) {
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? pPos.asLong() : pPos.below().asLong();
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
                BlockPos oPos = pState.getValue(HALF) == DoubleBlockHalf.LOWER ? pPos.above() : pPos.below();
                BlockState oState = pLevel.getBlockState(oPos);

                if (oState.is(this)) {
                    pLevel.setBlock(oPos, Blocks.AIR.defaultBlockState(), 35);
                    pLevel.levelEvent(pPlayer, 2001, oPos, Block.getId(oState));
                }

                popResource(pLevel, pPos, new ItemStack(this));
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
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (getMultiblockBlockEntity(pLevel, pPos, pState) instanceof FrogBonanzaBlockEntity bonanza) {
            return bonanza.interact(pPlayer, pHand);
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
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
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? CompanionsBlockEntities.FROG_BONANZA.create(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER && pBlockEntityType == CompanionsBlockEntities.FROG_BONANZA ? FrogBonanzaBlockEntity::tick : null;
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

}