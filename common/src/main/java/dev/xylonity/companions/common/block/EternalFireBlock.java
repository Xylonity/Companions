package dev.xylonity.companions.common.block;

import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EternalFireBlock extends BaseFireBlock {
    public EternalFireBlock(Properties properties) {
        super(properties, 2f);
    }

    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        return this.canSurvive(state, level, pos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos down = pos.below();
        BlockState belowState = level.getBlockState(down);
        return belowState.isFaceSturdy(level, down, Direction.UP);
    }

    protected boolean canBurn(@NotNull BlockState state) {
        return true;
    }

    @Override
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        super.animateTick(blockState, level, pos, randomSource);

        if (level.random.nextFloat() < 0.35f) {
            double x = pos.getX() + randomSource.nextDouble();
            double y = pos.getY() + randomSource.nextDouble();
            double z = pos.getZ() + randomSource.nextDouble();
            level.addParticle(CompanionsParticles.HOLINESS_BLUE_STAR_TRAIL.get(), x, y, z, 0, 0, 0);
        }

        if (level.random.nextFloat() < 0.35f) {
            double x = pos.getX() + randomSource.nextDouble();
            double y = pos.getY() + randomSource.nextDouble();
            double z = pos.getZ() + randomSource.nextDouble();
            level.addParticle(CompanionsParticles.HOLINESS_RED_STAR_TRAIL.get(), x, y, z, 0, 0, 0);
        }

    }

}
