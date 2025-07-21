package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.PlasmaLampBlockEntity;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class PlasmaLampBlock extends AbstractTeslaBlock implements EntityBlock {

    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    private static final VoxelShape SHAPE_UP = Stream.of(
            Block.box(4, 7, 4, 12, 19, 12),
            Block.box(5.5, 0, 5.5, 10.5, 4, 10.5),
            Block.box(2, 4, 2, 14, 7, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_DOWN = Stream.of(
            Block.box(4, -3, 4, 12, 9, 12),
            Block.box(5.5, 12, 5.5, 10.5, 16, 10.5),
            Block.box(2, 9, 2, 14, 12, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(4, 4, -3, 12, 12, 9),
            Block.box(5.5, 5.5, 12, 10.5, 10.5, 16),
            Block.box(2, 2, 9, 14, 14, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(4, 4, 7, 12, 12, 19),
            Block.box(5.5, 5.5, 0, 10.5, 10.5, 4),
            Block.box(2, 2, 4, 14, 14, 7)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(-3, 4, 4, 9, 12, 12),
            Block.box(12, 5.5, 5.5, 16, 10.5, 10.5),
            Block.box(9, 2, 2, 12, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(7, 4, 4, 19, 12, 12),
            Block.box(0, 5.5, 5.5, 4, 10.5, 10.5),
            Block.box(4, 2, 2, 7, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public PlasmaLampBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (pState.getValue(PlasmaLampBlock.FACING)) {
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == CompanionsBlockEntities.PLASMA_LAMP.get() ? PlasmaLampBlockEntity::tick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return CompanionsBlockEntities.PLASMA_LAMP.get().create(pos, state);
    }
}