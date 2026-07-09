package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

public class RecallPlatformBlock extends AbstractTeslaBlock implements EntityBlock {

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(0, 0, 0, 16, 3, 16),
            Block.box(2, 3, 2, 14, 6, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public RecallPlatformBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SHAPE_N;
    }

    @Override
    public void stepOn(@NotNull Level lvl, @NotNull BlockPos pos, @NotNull BlockState st, @NotNull Entity e) {
        if (!(e instanceof ServerPlayer player)) return;

        BlockEntity be = lvl.getBlockEntity(pos);
        if (be instanceof RecallPlatformBlockEntity rp) {
            rp.onStepped(player);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return CompanionsBlockEntities.RECALL_PLATFORM.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == CompanionsBlockEntities.RECALL_PLATFORM.get() ? RecallPlatformBlockEntity::tick : null;
    }

}
