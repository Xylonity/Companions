package dev.xylonity.companions.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoinBlock extends Block {
    private static final VoxelShape SHAPE_1 = Block.box(0, 0, 0, 16, 1, 16);
    private static final VoxelShape SHAPE_2 = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape SHAPE_3 = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE_4 = Block.box(0, 0, 0, 16, 6, 16);
    private static final VoxelShape SHAPE_5 = Block.box(0, 0, 0, 16, 11, 16);
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;

    public CoinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(LEVEL)) {
            case 0:
                yield SHAPE_1;
            case 1:
                yield SHAPE_2;
            case 2:
                yield SHAPE_3;
            case 3:
                yield SHAPE_4;
            case 4:
                yield SHAPE_5;
            default:
                yield super.getShape(pState, pLevel, pPos, pContext);
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if (state.is(this)) {
            return state.cycle(LEVEL);
        }

        return super.getStateForPlacement(pContext);
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState blockState, BlockPlaceContext ctx) {
        return !ctx.isSecondaryUseActive() && ctx.getItemInHand().getItem() == this.asItem() && blockState.getValue(LEVEL) < 5 || super.canBeReplaced(blockState, ctx);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL);
    }

}
